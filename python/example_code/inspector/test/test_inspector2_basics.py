# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for Amazon Inspector Basics scenario.

These tests exercise the Inspector2 wrapper and scenario against live AWS
resources. They will create and delete real AWS resources.

WARNING: Running these tests may incur AWS charges.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import logging
import time

import boto3
import pytest
from botocore.exceptions import ClientError

from inspector2_wrapper import Inspector2Wrapper

logger = logging.getLogger(__name__)

STACK_NAME = "inspector2-integ-test-resources"


@pytest.mark.integ
class TestInspector2Hello:
    """Integration tests for the Hello Inspector2 example."""

    def test_hello_list_account_permissions(self):
        """Test that ListAccountPermissions returns a list (possibly empty)."""
        inspector2_client = boto3.client("inspector2")
        wrapper = Inspector2Wrapper(inspector2_client)

        try:
            permissions = wrapper.list_account_permissions()
            assert isinstance(permissions, list)
            logger.info("ListAccountPermissions returned %d permissions.", len(permissions))
            for perm in permissions:
                assert "operation" in perm
                assert "service" in perm
        except ClientError as err:
            # AccessDeniedException is acceptable if Inspector is not enabled
            if err.response["Error"]["Code"] == "AccessDeniedException":
                pytest.skip("Access denied - Inspector may not be enabled in this account.")
            raise


@pytest.mark.integ
class TestInspector2Wrapper:
    """Integration tests for individual Inspector2 wrapper operations."""

    @pytest.fixture(autouse=True)
    def setup_wrapper(self):
        """Set up the Inspector2 wrapper client for each test."""
        self.inspector2_client = boto3.client("inspector2")
        self.wrapper = Inspector2Wrapper(self.inspector2_client)
        self.was_previously_enabled = False

        # Check if Inspector is currently enabled
        try:
            status_resp = self.wrapper.batch_get_account_status()
            if status_resp.get("accounts"):
                state = status_resp["accounts"][0].get("state", dict())
                current_status = state.get("status", "DISABLED")
                self.was_previously_enabled = current_status in (
                    "ENABLED",
                    "ENABLING",
                )
        except ClientError:
            pass

    def test_enable_and_disable(self):
        """Test enabling and disabling Inspector for EC2 and ECR."""
        try:
            # Enable
            enable_resp = self.wrapper.enable(resource_types=["EC2", "ECR"])
            assert "accounts" in enable_resp or "failedAccounts" in enable_resp

            # Brief wait for activation
            time.sleep(3)

            # Verify status
            status_resp = self.wrapper.batch_get_account_status()
            assert "accounts" in status_resp
            if status_resp["accounts"]:
                state = status_resp["accounts"][0].get("state", dict())
                assert state.get("status") in ("ENABLED", "ENABLING")

        finally:
            # Disable only if it was not previously enabled
            if not self.was_previously_enabled:
                try:
                    time.sleep(2)
                    self.wrapper.disable(resource_types=["EC2", "ECR"])
                except ClientError as err:
                    logger.warning("Could not disable Inspector during cleanup: %s", err)

    def test_batch_get_account_status(self):
        """Test retrieving account status."""
        response = self.wrapper.batch_get_account_status()
        assert "accounts" in response
        assert isinstance(response["accounts"], list)

    def test_get_configuration(self):
        """Test getting scan configuration (only when Inspector is enabled)."""
        if not self.was_previously_enabled:
            pytest.skip("Inspector not enabled - skipping configuration test.")

        config = self.wrapper.get_configuration()
        # At minimum, the response should have ec2Configuration or ecrConfiguration
        assert "ec2Configuration" in config or "ecrConfiguration" in config

    def test_list_finding_aggregations(self):
        """Test listing finding aggregations (only when Inspector is enabled)."""
        if not self.was_previously_enabled:
            pytest.skip("Inspector not enabled - skipping aggregation test.")

        aggregations = self.wrapper.list_finding_aggregations("FINDING_TYPE")
        assert isinstance(aggregations, list)

    def test_search_vulnerabilities(self):
        """Test searching for a well-known CVE."""
        vulnerabilities = self.wrapper.search_vulnerabilities(["CVE-2021-44228"])
        assert isinstance(vulnerabilities, list)
        # Log4Shell is well-known and should be in the database
        if vulnerabilities:
            assert "id" in vulnerabilities[0]
            assert vulnerabilities[0]["id"] == "CVE-2021-44228"

    def test_list_account_permissions(self):
        """Test listing account permissions."""
        permissions = self.wrapper.list_account_permissions()
        assert isinstance(permissions, list)


@pytest.mark.integ
class TestInspector2Scenario:
    """Integration test for the full scenario flow."""

    def test_scenario_end_to_end(self):
        """
        Runs a simplified version of the scenario end-to-end.

        This test enables Inspector, performs key operations, then cleans up.
        It uses a CloudFormation stack for S3/KMS resources.
        """
        inspector2_client = boto3.client("inspector2")
        cf_client = boto3.client("cloudformation")
        s3_client = boto3.client("s3")
        wrapper = Inspector2Wrapper(inspector2_client)

        was_previously_enabled = False
        bucket_name = ""
        stack_created = False

        try:
            # Check current state
            status_resp = wrapper.batch_get_account_status()
            if status_resp.get("accounts"):
                state = status_resp["accounts"][0].get("state", dict())
                was_previously_enabled = state.get("status") in (
                    "ENABLED",
                    "ENABLING",
                )

            # Enable Inspector
            enable_resp = wrapper.enable(resource_types=["EC2", "ECR"])
            assert "accounts" in enable_resp or "failedAccounts" in enable_resp

            time.sleep(5)

            # Verify status
            status_resp = wrapper.batch_get_account_status()
            assert len(status_resp.get("accounts", list())) > 0

            # Get and update configuration
            config = wrapper.get_configuration()
            assert config is not None

            wrapper.update_configuration(ecr_rescan_duration="DAYS_90")

            # List finding aggregations
            aggs = wrapper.list_finding_aggregations("FINDING_TYPE")
            assert isinstance(aggs, list)

            # Search vulnerabilities
            vulns = wrapper.search_vulnerabilities(["CVE-2021-44228"])
            assert isinstance(vulns, list)

            # List permissions
            perms = wrapper.list_account_permissions()
            assert isinstance(perms, list)

            # Deploy CloudFormation for report test
            from scenario_inspector2_basics import CLOUDFORMATION_TEMPLATE

            cf_client.create_stack(
                StackName=STACK_NAME,
                TemplateBody=CLOUDFORMATION_TEMPLATE,
                Capabilities=["CAPABILITY_IAM"],
            )
            stack_created = True
            waiter = cf_client.get_waiter("stack_create_complete")
            waiter.wait(
                StackName=STACK_NAME,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )

            describe_resp = cf_client.describe_stacks(StackName=STACK_NAME)
            outputs = {
                o["OutputKey"]: o["OutputValue"]
                for o in describe_resp["Stacks"][0]["Outputs"]
            }
            bucket_name = outputs["BucketName"]
            kms_key_arn = outputs["KmsKeyArn"]

            # Create findings report
            report_id = wrapper.create_findings_report(
                report_format="CSV",
                bucket_name=bucket_name,
                key_prefix="test-reports/",
                kms_key_arn=kms_key_arn,
            )
            assert report_id

            # Check report status
            report_status = wrapper.get_findings_report_status(report_id)
            assert "status" in report_status

        finally:
            # Disable Inspector if not previously enabled
            if not was_previously_enabled:
                try:
                    time.sleep(2)
                    wrapper.disable(resource_types=["EC2", "ECR"])
                except ClientError as err:
                    logger.warning("Cleanup - could not disable Inspector: %s", err)

            # Clean up S3 bucket
            if bucket_name:
                try:
                    objects_resp = s3_client.list_objects_v2(Bucket=bucket_name)
                    objects = objects_resp.get("Contents", list())
                    if objects:
                        delete_objects = [{"Key": obj["Key"]} for obj in objects]
                        s3_client.delete_objects(
                            Bucket=bucket_name,
                            Delete={"Objects": delete_objects},
                        )
                except ClientError as err:
                    logger.warning("Cleanup - could not empty bucket: %s", err)

            # Delete CloudFormation stack
            if stack_created:
                try:
                    cf_client.delete_stack(StackName=STACK_NAME)
                    delete_waiter = cf_client.get_waiter("stack_delete_complete")
                    delete_waiter.wait(
                        StackName=STACK_NAME,
                        WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                    )
                except ClientError as err:
                    logger.warning("Cleanup - could not delete stack: %s", err)
