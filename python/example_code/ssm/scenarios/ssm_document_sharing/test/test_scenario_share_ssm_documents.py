# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the SSM document sharing with organizations and OUs scenario.

Runs the scenario end to end against real AWS (no mocking of the SSM, RAM,
AWS Organizations, or AWS CloudFormation clients), feeding the interactive
prompts through the shared input_mocker fixture and asserting on the scenario's
output. Follows the pattern used by the other Python scenario integration tests
(see cloudwatch-logs/scenarios/syslog_ingestion/test).

The test must run in the management account of an existing organization. It
creates two OUs under the organization root with a CloudFormation stack, and
deletes them when it finishes. It is skipped in any other account so that it
never creates or deletes an organization.
"""

import logging
import uuid

import boto3
import pytest
from botocore.exceptions import ClientError, WaiterError

from organization_setup import OrganizationSetup
from ram_wrapper import RamWrapper
from scenario_share_ssm_documents import ShareSsmDocumentsScenario
from ssm_wrapper import SsmDocumentWrapper

logger = logging.getLogger(__name__)


@pytest.mark.integ
def test_run_share_ssm_documents_scenario_integ(input_mocker, capsys):
    sts_client = boto3.client("sts")
    org_setup = OrganizationSetup.from_client()
    account_id = sts_client.get_caller_identity()["Account"]

    organization = org_setup.get_organization()
    if organization is None or organization["Arn"].split(":")[4] != account_id:
        pytest.skip(
            "This test must run in the management account of an existing "
            "organization."
        )

    # Share the second document directly with another account in the organization,
    # when there is one.
    other_accounts = [
        account["Id"]
        for page in org_setup.organizations_client.get_paginator(
            "list_accounts"
        ).paginate()
        for account in page["Accounts"]
        if account["Id"] != account_id and account["Status"] == "ACTIVE"
    ]

    ssm_wrapper = SsmDocumentWrapper.from_client()
    scenario = ShareSsmDocumentsScenario(
        ssm_wrapper, RamWrapper.from_client(), sts_client, org_setup
    )

    suffix = uuid.uuid4().hex[:8]
    # Answers, in order, for the scenario's prompts.
    answers = [
        "y",  # Create two OUs under the organization root.
        "",  # Press Enter to continue.
        f"ssm-sharing-integ-{suffix}",  # SSM document name.
        "",  # Press Enter to continue.
        f"SSM-Document-OrgShare-integ-{suffix}",  # RAM resource share name.
        "",  # Press Enter to continue (Step 1).
        "",  # Press Enter to continue (Step 2).
        "y",  # Add the second OU to the resource share.
        "",  # Press Enter to continue (Step 3).
        "",  # Press Enter to continue (Step 4).
    ]
    if other_accounts:
        answers += [
            "y",  # Demonstrate direct account sharing.
            other_accounts[0],  # Account ID to share with.
            "",  # Press Enter to continue (Step 5).
        ]
    else:
        answers += ["n"]  # Skip direct account sharing.
    answers += [
        "",  # Press Enter to continue (Step 6).
        "y",  # Clean up all resources.
    ]
    input_mocker.mock_answers(answers)

    try:
        scenario.run_scenario()
    finally:
        # Best-effort cleanup for anything the scenario's own cleanup didn't
        # reach, for example if the scenario failed before its cleanup prompt.
        # The order matches the scenario's cleanup: the resource share first,
        # then the documents, then the stack that holds the OUs. Each step
        # ignores errors so that one failure doesn't stop the others.
        if scenario.resource_share_arn:
            try:
                scenario.ram_wrapper.delete_resource_share(scenario.resource_share_arn)
            except ClientError:
                pass
        for name in (scenario.direct_document_name, scenario.document_name):
            if name:
                try:
                    ssm_wrapper.delete_document(name)
                except ClientError:
                    pass
        if scenario.stack_name:
            try:
                org_setup.delete_stack(scenario.stack_name)
            except (ClientError, WaiterError):
                logger.warning(
                    "Cleanup: couldn't delete stack '%s'. Delete it in the "
                    "AWS CloudFormation console.",
                    scenario.stack_name,
                )

    # Reached only if the scenario finished without raising.
    capt = capsys.readouterr()
    assert "Cleanup complete." in capt.out
    assert "Scenario complete!" in capt.out
