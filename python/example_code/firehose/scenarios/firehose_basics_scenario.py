# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Data Firehose Basics Scenario

This scenario demonstrates a complete lifecycle of an Amazon Data Firehose
delivery stream delivering data to Amazon S3. It walks through:
1. Deploying prerequisite infrastructure via CloudFormation
2. Creating a Firehose delivery stream
3. Waiting for the stream to become active
4. Listing delivery streams
5. Tagging the delivery stream
6. Listing tags
7. Enabling server-side encryption
8. Putting a single record
9. Putting a batch of records
10. Disabling encryption
11. Cleaning up all resources

IMPORTANT: This scenario is fully self-contained. It does NOT import
demo_tools or any external repo-specific modules. All dependencies
are available via pip install.
"""

import json
import logging
import time
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError

from firehose_wrapper import FirehoseWrapper

logger = logging.getLogger(__name__)

# CloudFormation template for prerequisite resources
CFN_TEMPLATE = """
AWSTemplateFormatVersion: '2010-09-09'
Description: Firehose basics scenario - S3 bucket and IAM role

Parameters:
  Suffix:
    Type: String
    Description: Unique suffix for resource names

Resources:
  FirehoseBucket:
    Type: AWS::S3::Bucket

  FirehoseRole:
    Type: AWS::IAM::Role
    Properties:
      AssumeRolePolicyDocument:
        Version: '2012-10-17'
        Statement:
          - Effect: Allow
            Principal:
              Service: firehose.amazonaws.com
            Action: 'sts:AssumeRole'
      Policies:
        - PolicyName: FirehoseS3Access
          PolicyDocument:
            Version: '2012-10-17'
            Statement:
              - Effect: Allow
                Action:
                  - 's3:PutObject'
                  - 's3:GetObject'
                  - 's3:ListBucket'
                  - 's3:AbortMultipartUpload'
                Resource:
                  - !GetAtt FirehoseBucket.Arn
                  - !Sub '${FirehoseBucket.Arn}/*'

Outputs:
  BucketArn:
    Value: !GetAtt FirehoseBucket.Arn
    Description: ARN of the S3 bucket
  RoleArn:
    Value: !GetAtt FirehoseRole.Arn
    Description: ARN of the IAM role
"""


# snippet-start:[python.example_code.firehose.FirehoseScenario]
class FirehoseScenario:
    """Runs an interactive scenario demonstrating Firehose basics."""

    def __init__(self, firehose_wrapper: FirehoseWrapper, cf_client=None):
        """
        :param firehose_wrapper: An instance of FirehoseWrapper.
        :param cf_client: A Boto3 CloudFormation client (created if not provided).
        """
        self.firehose_wrapper = firehose_wrapper
        self.cf_client = cf_client or boto3.client("cloudformation")
        self.stream_name = None
        self.stack_name = None

    def run_scenario(self):
        """Runs all steps of the Firehose basics scenario."""
        print("\n" + "=" * 70)
        print("Welcome to the Amazon Data Firehose Basics Scenario!")
        print("=" * 70)

        timestamp = datetime.now(timezone.utc).strftime("%Y%m%d%H%M%S")
        self.stream_name = f"firehose-basics-stream-{timestamp}"
        self.stack_name = f"firehose-basics-stack-{timestamp}"

        try:
            # Setup: Deploy CloudFormation stack
            bucket_arn, role_arn = self._setup_infrastructure()

            # Step 1: Create a Firehose Delivery Stream
            self._step1_create_stream(role_arn, bucket_arn)

            # Step 2: Wait for the Stream to Become Active
            self._step2_wait_for_active()

            # Step 3: List Delivery Streams
            self._step3_list_streams()

            # Step 4: Tag the Delivery Stream
            self._step4_tag_stream()

            # Step 5: List Tags for the Delivery Stream
            self._step5_list_tags()

            # Step 6: Enable Server-Side Encryption
            self._step6_enable_encryption()

            # Step 7: Put a Single Record
            self._step7_put_record()

            # Step 8: Put a Batch of Records
            self._step8_put_record_batch()

            # Step 9: Disable Server-Side Encryption
            self._step9_disable_encryption()

            print("\n" + "=" * 70)
            print("Amazon Data Firehose Basics Scenario completed successfully!")
            print("=" * 70)
        finally:
            # Cleanup always runs
            self._cleanup()

    def _setup_infrastructure(self):
        """Deploys prerequisite infrastructure via CloudFormation."""
        print("\nSetting up prerequisite resources via CloudFormation...")
        print(f"Stack '{self.stack_name}' creation initiated.")

        timestamp = self.stack_name.split("-")[-1]  # Extract timestamp from stack name
        self.cf_client.create_stack(
            StackName=self.stack_name,
            TemplateBody=CFN_TEMPLATE,
            Parameters=[
                {"ParameterKey": "Suffix", "ParameterValue": timestamp},
            ],
            Capabilities=["CAPABILITY_IAM"],
        )

        print("Waiting for stack creation to complete...")
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

        print("Stack created successfully!")
        print(f"  S3 Bucket ARN: {bucket_arn}")
        print(f"  IAM Role ARN: {role_arn}")

        # Allow IAM role to propagate
        time.sleep(10)

        return bucket_arn, role_arn

    def _step1_create_stream(self, role_arn, bucket_arn):
        """Step 1: Create a Firehose delivery stream."""
        print("\nStep 1: Creating Firehose delivery stream...")
        stream_arn = self.firehose_wrapper.create_delivery_stream(
            stream_name=self.stream_name,
            role_arn=role_arn,
            bucket_arn=bucket_arn,
            buffer_size_mb=1,
            buffer_interval_seconds=60,
        )
        print(f"  Stream ARN: {stream_arn}")

    def _step2_wait_for_active(self):
        """Step 2: Wait for the stream to become ACTIVE."""
        print("\nStep 2: Waiting for stream to become ACTIVE...")
        description = self.firehose_wrapper.wait_for_stream_active(
            self.stream_name, timeout=120, interval=5
        )
        status = description.get("DeliveryStreamStatus")
        create_time = description.get("CreateTimestamp", "N/A")
        stream_type = description.get("DeliveryStreamType", "N/A")
        print(f"  Status: {status} ✓")
        print(f"  Created: {create_time}")
        print(f"  Type: {stream_type}")

    def _step3_list_streams(self):
        """Step 3: List delivery streams."""
        print("\nStep 3: Listing delivery streams...")
        stream_names = self.firehose_wrapper.list_delivery_streams(
            stream_type="DirectPut"
        )
        print(f"  Found {len(stream_names)} DirectPut stream(s):")
        for name in stream_names:
            marker = " ← (our stream)" if name == self.stream_name else ""
            print(f"    - {name}{marker}")

    def _step4_tag_stream(self):
        """Step 4: Tag the delivery stream."""
        print("\nStep 4: Tagging delivery stream...")
        tags = [
            {"Key": "Environment", "Value": "Development"},
            {"Key": "Project", "Value": "FirehoseBasicsScenario"},
            {"Key": "CreatedBy", "Value": "SDK-Example"},
        ]
        self.firehose_wrapper.tag_delivery_stream(self.stream_name, tags)
        tag_str = ", ".join(f"{t['Key']}={t['Value']}" for t in tags)
        print(f"  Tags applied successfully: {tag_str}")

    def _step5_list_tags(self):
        """Step 5: List tags for the delivery stream."""
        print("\nStep 5: Listing tags for delivery stream...")
        tags = self.firehose_wrapper.list_tags_for_delivery_stream(self.stream_name)
        print("  Tags:")
        for tag in tags:
            print(f"    - {tag['Key']}: {tag['Value']}")

    def _step6_enable_encryption(self):
        """Step 6: Enable server-side encryption."""
        print("\nStep 6: Enabling server-side encryption...")
        self.firehose_wrapper.start_delivery_stream_encryption(self.stream_name)
        self.firehose_wrapper.wait_for_encryption_status(
            self.stream_name, "ENABLED", timeout=120, interval=5
        )
        print("  Encryption status: ENABLED ✓ (KeyType: AWS_OWNED_CMK)")

    def _step7_put_record(self):
        """Step 7: Put a single record."""
        print("\nStep 7: Putting a single record...")
        record_data = (
            json.dumps(
                {
                    "sensor_id": "sensor-001",
                    "temperature": 72.5,
                    "timestamp": datetime.now(timezone.utc).isoformat(),
                }
            )
            + "\n"
        )
        response = self.firehose_wrapper.put_record(self.stream_name, record_data)
        record_id = response.get("RecordId", "N/A")
        encrypted = response.get("Encrypted", False)
        print(f"  Record sent successfully! RecordId: {record_id[:20]}...")
        print(f"  Encrypted: {encrypted}")

    def _step8_put_record_batch(self):
        """Step 8: Put a batch of records."""
        print("\nStep 8: Putting a batch of records...")
        now = datetime.now(timezone.utc).isoformat()
        records = [
            json.dumps(
                {"sensor_id": "sensor-001", "temperature": 73.2, "timestamp": now}
            )
            + "\n",
            json.dumps(
                {"sensor_id": "sensor-002", "temperature": 68.9, "timestamp": now}
            )
            + "\n",
            json.dumps(
                {"sensor_id": "sensor-003", "temperature": 75.1, "timestamp": now}
            )
            + "\n",
            json.dumps(
                {"sensor_id": "sensor-001", "temperature": 72.8, "timestamp": now}
            )
            + "\n",
            json.dumps(
                {"sensor_id": "sensor-002", "temperature": 69.3, "timestamp": now}
            )
            + "\n",
        ]
        response = self.firehose_wrapper.put_record_batch(self.stream_name, records)
        failed_count = response.get("FailedPutCount", 0)
        print(f"  Sent {len(records)} records in batch.")
        print(f"  FailedPutCount: {failed_count}")
        if failed_count == 0:
            print("  All records delivered successfully!")
        else:
            request_responses = response.get("RequestResponses", list())
            for i, resp in enumerate(request_responses):
                if resp.get("ErrorCode"):
                    print(
                        f"    Record {i}: Error {resp['ErrorCode']} - {resp.get('ErrorMessage', '')}"
                    )

    def _step9_disable_encryption(self):
        """Step 9: Disable server-side encryption."""
        print("\nStep 9: Disabling server-side encryption...")
        self.firehose_wrapper.stop_delivery_stream_encryption(self.stream_name)
        self.firehose_wrapper.wait_for_encryption_status(
            self.stream_name, "DISABLED", timeout=120, interval=5
        )
        print("  Encryption status: DISABLED ✓")

    def _cleanup(self):
        """Cleanup: Delete stream and CloudFormation stack."""
        print("\nCleanup: Deleting delivery stream...")
        if self.stream_name:
            try:
                self.firehose_wrapper.delete_delivery_stream(self.stream_name)
                print("  Stream deletion initiated.")
            except ClientError as err:
                logger.error("Error deleting stream: %s", err)

        if self.stack_name:
            print("Cleanup: Emptying S3 bucket and deleting CloudFormation stack...")
            try:
                # Get the bucket name from stack resources to empty it before deletion
                response = self.cf_client.describe_stack_resources(
                    StackName=self.stack_name
                )
                for resource in response.get("StackResources", []):
                    if resource["ResourceType"] == "AWS::S3::Bucket":
                        bucket_name = resource.get("PhysicalResourceId")
                        if bucket_name:
                            self._empty_bucket(bucket_name)
            except ClientError:
                pass  # Stack may already be gone

            try:
                self.cf_client.delete_stack(StackName=self.stack_name)
                print("  Stack deletion initiated.")
                waiter = self.cf_client.get_waiter("stack_delete_complete")
                waiter.wait(
                    StackName=self.stack_name,
                    WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                )
                print("  Stack deleted successfully.")
            except ClientError as err:
                logger.error("Error deleting stack: %s", err)

    def _empty_bucket(self, bucket_name):
        """Remove all objects from a bucket so CloudFormation can delete it."""
        try:
            s3 = boto3.resource("s3")
            bucket = s3.Bucket(bucket_name)
            bucket.objects.all().delete()
            print(f"  Emptied bucket '{bucket_name}'.")
        except ClientError as err:
            logger.error("Error emptying bucket: %s", err)


# snippet-end:[python.example_code.firehose.FirehoseScenario]


def main():
    """Entry point for running the scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    firehose_wrapper = FirehoseWrapper.from_client()
    scenario = FirehoseScenario(firehose_wrapper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
