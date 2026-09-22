# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the CloudWatch Logs syslog ingestion scenario.

These tests exercise the real AWS CloudWatch Logs APIs. They do NOT
mock the main service. Resources created during the test are cleaned up
in 'finally' blocks so that the account is left in a clean state even if
a test fails partway through.
"""

import logging
import uuid

import boto3
import pytest
from botocore.exceptions import ClientError

from cloudwatch_logs_wrapper import CloudWatchLogsWrapper

logger = logging.getLogger(__name__)

# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

UNIQUE_SUFFIX = uuid.uuid4().hex[:8]
TEST_LOG_GROUP = f"/syslog/integ-test-{UNIQUE_SUFFIX}"


@pytest.fixture(scope="module")
def logs_wrapper():
    """Provides a CloudWatchLogsWrapper using real AWS credentials."""
    return CloudWatchLogsWrapper.from_client()


# ---------------------------------------------------------------------------
# Full scenario integration test
# ---------------------------------------------------------------------------


@pytest.mark.integ
def test_run_scenario(logs_wrapper, capsys, input_mocker):
    """
    Runs the interactive scenario end-to-end.

    NOTE: This test requires the syslog VPC endpoint service to be
    available in the test Region. If the CloudFormation stack creation
    fails (e.g., the service is not available), the test will be
    skipped rather than failing.
    """
    from syslog_ingestion_scenario import SyslogIngestionScenario

    cf_client = boto3.client("cloudformation")
    scenario = SyslogIngestionScenario(logs_wrapper, cf_client)

    log_group_name = f"/syslog/integ-scenario-{UNIQUE_SUFFIX}"

    input_mocker.mock_answers(
        [
            log_group_name,  # Log group name prompt
            "",  # Press Enter to continue
            "y",  # Confirm cleanup
        ]
    )

    try:
        scenario.run_scenario()
        captured = capsys.readouterr()
        assert "Scenario complete!" in captured.out
    except ClientError as error:
        if "not available" in str(error).lower():
            pytest.skip("Syslog VPC endpoint service not available in this Region.")
        raise
    finally:
        # Belt-and-suspenders cleanup in case the scenario's own cleanup
        # was interrupted.
        try:
            logs_wrapper.delete_syslog_configuration(
                log_group_identifier=log_group_name,
                vpc_endpoint_id="vpce-00000000000000000",
            )
        except Exception:
            pass
        try:
            logs_wrapper.delete_log_group(log_group_name)
        except Exception:
            pass
        try:
            cf_client.delete_stack(StackName=SyslogIngestionScenario.STACK_NAME)
        except Exception:
            pass
