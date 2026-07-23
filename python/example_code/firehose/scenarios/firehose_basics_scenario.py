# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon Data Firehose Basics scenario.
These tests run against real AWS services.
"""

import time

import boto3
import pytest

from firehose_wrapper import FirehoseWrapper
from scenario_firehose_basics import FirehoseScenario


@pytest.mark.integ
class TestFirehoseScenarioInteg:
    """Integration tests for the Firehose basics scenario."""

    @pytest.fixture(autouse=True)
    def setup(self):
        """Set up test resources."""
        self.firehose_client = boto3.client("firehose")
        self.cf_client = boto3.client("cloudformation")
        self.firehose_wrapper = FirehoseWrapper(self.firehose_client)
        self.stream_name = None
        self.stack_name = None

    def test_run_full_scenario(self, capsys):
        """
        Test the full Firehose basics scenario end-to-end.
        This test creates real AWS resources, runs all steps, and cleans up.
        """
        scenario = FirehoseScenario(self.firehose_wrapper, self.cf_client)

        try:
            scenario.run_scenario()

            # Verify scenario completed by checking output
            captured = capsys.readouterr()
            assert "Amazon Data Firehose Basics Scenario complete!" in captured.out
            assert "All resources have been cleaned up." in captured.out
        finally:
            # Ensure cleanup happens even if test assertions fail
            if scenario.stream_name:
                try:
                    self.firehose_wrapper.delete_delivery_stream(
                        scenario.stream_name, allow_force_delete=True
                    )
                    self.firehose_wrapper.wait_for_stream_deleted(
                        scenario.stream_name, max_wait_seconds=60
                    )
                except Exception:
                    pass

            if scenario.stack_name:
                try:
                    self.cf_client.delete_stack(StackName=scenario.stack_name)
                    waiter = self.cf_client.get_waiter("stack_delete_complete")
                    waiter.wait(
                        StackName=scenario.stack_name,
                        WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                    )
                except Exception:
                    pass

    def test_hello_firehose(self, capsys):
        """Test the Hello Firehose example."""
        from hello_firehose import hello_firehose

        hello_firehose()

        captured = capsys.readouterr()
        assert "Hello, Amazon Data Firehose!" in captured.out

    def test_wrapper_create_describe_delete(self):
        """
        Integration test for the core wrapper operations:
        create, describe, and delete a delivery stream.
        """
        timestamp = str(int(time.time()))
        self.stack_name = f"firehose-integ-test-{timestamp}"
        self.stream_name = f"firehose-integ-stream-{timestamp}"

        try:
            # Deploy CloudFormation stack
            from scenario_firehose_basics import CLOUDFORMATION_TEMPLATE

            self.cf_client.create_stack(
                StackName=self.stack_name,
                TemplateBody=CLOUDFORMATION_TEMPLATE,
                Capabilities=["CAPABILITY_NAMED_IAM"],
            )
            waiter = self.cf_client.get_waiter("stack_create_complete")
            waiter.wait(
                StackName=self.stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )

            # Get outputs
            response = self.cf_client.describe_stacks(StackName=self.stack_name)
            outputs = response["Stacks"][0]["Outputs"]
            bucket_arn = None
            role_arn = None
            for output in outputs:
                if output["OutputKey"] == "BucketArn":
                    bucket_arn = output["OutputValue"]
                elif output["OutputKey"] == "RoleArn":
                    role_arn = output["OutputValue"]

            # Wait for IAM propagation
            time.sleep(15)

            # Create stream
            stream_arn = self.firehose_wrapper.create_delivery_stream(
                stream_name=self.stream_name,
                role_arn=role_arn,
                bucket_arn=bucket_arn,
            )
            assert stream_arn is not None
            assert "firehose" in stream_arn

            # Wait for ACTIVE
            description = self.firehose_wrapper.wait_for_stream_active(
                self.stream_name, max_wait_seconds=120
            )
            assert description["DeliveryStreamStatus"] == "ACTIVE"

            # Describe stream
            description = self.firehose_wrapper.describe_delivery_stream(self.stream_name)
            assert description["DeliveryStreamName"] == self.stream_name
            assert description["DeliveryStreamStatus"] == "ACTIVE"

            # List streams
            result = self.firehose_wrapper.list_delivery_streams()
            assert self.stream_name in result["DeliveryStreamNames"]

            # Tag stream
            tags = [{"Key": "TestKey", "Value": "TestValue"}]
            self.firehose_wrapper.tag_delivery_stream(self.stream_name, tags)

            # List tags
            tag_result = self.firehose_wrapper.list_tags_for_delivery_stream(
                self.stream_name
            )
            tag_keys = [t["Key"] for t in tag_result["Tags"]]
            assert "TestKey" in tag_keys

            # Untag stream
            self.firehose_wrapper.untag_delivery_stream(self.stream_name, ["TestKey"])
            tag_result = self.firehose_wrapper.list_tags_for_delivery_stream(
                self.stream_name
            )
            tag_keys = [t["Key"] for t in tag_result["Tags"]]
            assert "TestKey" not in tag_keys

            # Delete stream
            self.firehose_wrapper.delete_delivery_stream(self.stream_name)
            deleted = self.firehose_wrapper.wait_for_stream_deleted(
                self.stream_name, max_wait_seconds=120
            )
            assert deleted is True
            self.stream_name = None

        finally:
            # Cleanup
            if self.stream_name:
                try:
                    self.firehose_wrapper.delete_delivery_stream(
                        self.stream_name, allow_force_delete=True
                    )
                    self.firehose_wrapper.wait_for_stream_deleted(
                        self.stream_name, max_wait_seconds=60
                    )
                except Exception:
                    pass

            if self.stack_name:
                try:
                    self.cf_client.delete_stack(StackName=self.stack_name)
                    waiter = self.cf_client.get_waiter("stack_delete_complete")
                    waiter.wait(
                        StackName=self.stack_name,
                        WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                    )
                except Exception:
                    pass