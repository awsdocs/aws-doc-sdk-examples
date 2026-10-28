# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Inspector Basics Scenario
---------------------------------
This scenario demonstrates the full lifecycle of Amazon Inspector:
  1. Enable Inspector for EC2 and ECR scanning.
  2. Verify account status.
  3. Review scan configuration.
  4. Update ECR rescan duration configuration.
  5. List finding aggregations by type and account.
  6. Search for a specific vulnerability by CVE ID.
  7. Generate a findings report to S3.
  8. Poll the report generation status.
  9. List account permissions.
  Cleanup: Disable Inspector (if not previously enabled) and delete CloudFormation stack.

Prerequisites:
  - An AWS account with permissions for Inspector2, CloudFormation, S3, and KMS.
  - Python 3.9+ and boto3 installed.
"""

import json
import logging
import time
from typing import Any

import boto3
from botocore.exceptions import ClientError

from inspector2_wrapper import Inspector2Wrapper

logger = logging.getLogger(__name__)

STACK_NAME = "inspector2-basics-resources"

CLOUDFORMATION_TEMPLATE = json.dumps(
    {
        "AWSTemplateFormatVersion": "2010-09-09",
        "Description": "Resources for Amazon Inspector Basics scenario - S3 bucket and KMS key.",
        "Resources": {
            "ReportBucket": {
                "Type": "AWS::S3::Bucket",
                "Properties": {
                    "BucketEncryption": {
                        "ServerSideEncryptionConfiguration": [
                            {
                                "ServerSideEncryptionByDefault": {
                                    "SSEAlgorithm": "AES256"
                                }
                            }
                        ]
                    },
                },
            },
            "ReportBucketPolicy": {
                "Type": "AWS::S3::BucketPolicy",
                "Properties": {
                    "Bucket": {"Ref": "ReportBucket"},
                    "PolicyDocument": {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Sid": "AllowInspectorPutObject",
                                "Effect": "Allow",
                                "Principal": {
                                    "Service": "inspector2.amazonaws.com"
                                },
                                "Action": [
                                    "s3:PutObject",
                                    "s3:PutObjectAcl",
                                ],
                                "Resource": {
                                    "Fn::Sub": "arn:aws:s3:::${ReportBucket}/*"
                                },
                            }
                        ],
                    },
                },
            },
            "ReportKmsKey": {
                "Type": "AWS::KMS::Key",
                "Properties": {
                    "Description": "KMS key for Inspector findings report encryption",
                    "KeyPolicy": {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Sid": "EnableRootAccountAccess",
                                "Effect": "Allow",
                                "Principal": {
                                    "AWS": {
                                        "Fn::Sub": "arn:aws:iam::${AWS::AccountId}:root"
                                    }
                                },
                                "Action": "kms:*",
                                "Resource": "*",
                            },
                            {
                                "Sid": "AllowInspectorToUseKey",
                                "Effect": "Allow",
                                "Principal": {
                                    "Service": "inspector2.amazonaws.com"
                                },
                                "Action": [
                                    "kms:Decrypt",
                                    "kms:GenerateDataKey*",
                                    "kms:DescribeKey",
                                ],
                                "Resource": "*",
                            },
                        ],
                    },
                },
            },
        },
        "Outputs": {
            "BucketName": {
                "Value": {"Ref": "ReportBucket"},
                "Description": "S3 bucket for Inspector findings reports",
            },
            "KmsKeyArn": {
                "Value": {"Fn::GetAtt": ["ReportKmsKey", "Arn"]},
                "Description": "KMS key ARN for report encryption",
            },
        },
    }
)


# snippet-start:[python.example_code.inspector2.Inspector2Scenario]
class Inspector2Scenario:
    """Runs the Amazon Inspector Basics interactive scenario."""

    def __init__(
        self,
        inspector2_wrapper: Inspector2Wrapper,
        cf_client: boto3.client,
        s3_client: boto3.client,
    ) -> None:
        """
        Initializes the scenario.

        :param inspector2_wrapper: An instance of Inspector2Wrapper.
        :param cf_client: A Boto3 CloudFormation client.
        :param s3_client: A Boto3 S3 client.
        """
        self.inspector2_wrapper = inspector2_wrapper
        self.cf_client = cf_client
        self.s3_client = s3_client
        self.stack_name = STACK_NAME
        self.bucket_name = ""
        self.kms_key_arn = ""
        self.was_previously_enabled = False

    # ------------------------------------------------------------------
    # Setup
    # ------------------------------------------------------------------
    def setup(self) -> None:
        """Deploys CloudFormation resources and checks whether Inspector is already enabled."""
        print("\n" + "=" * 68)
        print("Setting up resources for Amazon Inspector Basics scenario...")
        print("=" * 68)

        # Deploy CloudFormation stack
        print(f"\nCreating CloudFormation stack '{self.stack_name}'...")
        self.cf_client.create_stack(
            StackName=self.stack_name,
            TemplateBody=CLOUDFORMATION_TEMPLATE,
            Capabilities=["CAPABILITY_IAM"],
        )
        waiter = self.cf_client.get_waiter("stack_create_complete")
        print("Waiting for stack creation to complete (this may take a few minutes)...")
        waiter.wait(
            StackName=self.stack_name,
            WaiterConfig={"Delay": 10, "MaxAttempts": 60},
        )

        # Extract outputs
        describe_resp = self.cf_client.describe_stacks(StackName=self.stack_name)
        outputs = {
            o["OutputKey"]: o["OutputValue"]
            for o in describe_resp["Stacks"][0]["Outputs"]
        }
        self.bucket_name = outputs["BucketName"]
        self.kms_key_arn = outputs["KmsKeyArn"]
        print(f"\nStack created successfully!")
        print(f"  S3 Bucket : {self.bucket_name}")
        print(f"  KMS Key   : {self.kms_key_arn}")

        # Check if Inspector is already enabled
        print("\nChecking current Inspector status...")
        status_resp = self.inspector2_wrapper.batch_get_account_status()
        if status_resp.get("accounts"):
            state = status_resp["accounts"][0].get("state", dict())
            current_status = state.get("status", "DISABLED")
            self.was_previously_enabled = current_status in ("ENABLED", "ENABLING")
            if self.was_previously_enabled:
                print(
                    f"  Inspector is already {current_status}. "
                    "It will NOT be disabled during cleanup."
                )
            else:
                print(f"  Inspector is currently {current_status}.")

    # ------------------------------------------------------------------
    # Step 1: Enable Amazon Inspector
    # ------------------------------------------------------------------
    def step_enable_inspector(self) -> None:
        """Enables Amazon Inspector for EC2 and ECR scanning."""
        print("\n" + "-" * 68)
        print("Step 1: Enabling Amazon Inspector for EC2 and ECR scanning...")
        print("-" * 68)

        response = self.inspector2_wrapper.enable(resource_types=["EC2", "ECR"])
        accounts = response.get("accounts", list())
        failed = response.get("failedAccounts", list())

        if accounts:
            for acct in accounts:
                print(f"\n  Account: {acct.get('accountId', 'N/A')}")
                rs = acct.get("resourceStatus", dict())
                print(f"    EC2 scanning: {rs.get('ec2', 'N/A')}")
                print(f"    ECR scanning: {rs.get('ecr', 'N/A')}")
        if failed:
            for fa in failed:
                print(
                    f"\n  Failed account: {fa.get('accountId', 'N/A')} - "
                    f"{fa.get('errorCode', 'N/A')}: {fa.get('errorMessage', 'N/A')}"
                )

        if self.was_previously_enabled:
            print(
                "\n  Note: Inspector was already enabled. The Enable call is "
                "idempotent — enabling already-enabled resource types is a no-op."
            )

    # ------------------------------------------------------------------
    # Step 2: Verify Account Status
    # ------------------------------------------------------------------
    def step_verify_account_status(self) -> None:
        """Verifies the Amazon Inspector account status."""
        print("\n" + "-" * 68)
        print("Step 2: Verifying Amazon Inspector account status...")
        print("-" * 68)

        response = self.inspector2_wrapper.batch_get_account_status()
        for acct in response.get("accounts", list()):
            account_id = acct.get("accountId", "N/A")
            state = acct.get("state", dict())
            print(f"\n  Account Status for {account_id}:")
            print(f"    Overall State: {state.get('status', 'UNKNOWN')}")

            resource_state = acct.get("resourceState", dict())
            print("    Resource Scan Status:")
            for resource_key in ("ec2", "ecr", "lambda", "lambdaCode"):
                res = resource_state.get(resource_key, dict())
                status = res.get("status", "UNKNOWN") if isinstance(res, dict) else "UNKNOWN"
                label = resource_key.upper() if resource_key != "lambdaCode" else "Lambda Code"
                print(f"      {label:12s}: {status}")

    # ------------------------------------------------------------------
    # Step 3: Review Scan Configuration
    # ------------------------------------------------------------------
    def step_review_configuration(self) -> None:
        """Reviews the current Inspector scan configuration."""
        print("\n" + "-" * 68)
        print("Step 3: Reviewing current scan configuration...")
        print("-" * 68)

        config = self.inspector2_wrapper.get_configuration()

        ec2_config = config.get("ec2Configuration", dict())
        scan_mode_state = ec2_config.get("scanModeState", dict())
        print("\n  EC2 Configuration:")
        print(f"    Scan Mode: {scan_mode_state.get('scanMode', 'N/A')}")
        print(f"    Status   : {scan_mode_state.get('scanModeStatus', 'N/A')}")

        ecr_config = config.get("ecrConfiguration", dict())
        rescan_state = ecr_config.get("rescanDurationState", dict())
        print("\n  ECR Configuration:")
        print(f"    Rescan Duration          : {rescan_state.get('rescanDuration', 'N/A')}")
        print(
            f"    Pull Date Rescan Duration: "
            f"{rescan_state.get('pullDateRescanDuration', 'N/A')}"
        )
        print(f"    Status                   : {rescan_state.get('status', 'N/A')}")
        updated_at = rescan_state.get("updatedAt", None)
        if updated_at is not None:
            print(f"    Last Updated             : {updated_at}")

    # ------------------------------------------------------------------
    # Step 4: Update Scan Configuration
    # ------------------------------------------------------------------
    def step_update_configuration(self) -> None:
        """Updates the ECR rescan duration configuration."""
        print("\n" + "-" * 68)
        print("Step 4: Updating ECR rescan duration to DAYS_90...")
        print("-" * 68)

        self.inspector2_wrapper.update_configuration(ecr_rescan_duration="DAYS_90")
        print("  Configuration updated successfully.")

        print("\n  Verifying updated configuration:")
        config = self.inspector2_wrapper.get_configuration()
        ecr_config = config.get("ecrConfiguration", dict())
        rescan_state = ecr_config.get("rescanDurationState", dict())
        print(f"    ECR Rescan Duration: {rescan_state.get('rescanDuration', 'N/A')}")
        print(
            f"    Pull Date Rescan Duration: "
            f"{rescan_state.get('pullDateRescanDuration', 'N/A')}"
        )
        print(f"    Status: {rescan_state.get('status', 'N/A')}")

    # ------------------------------------------------------------------
    # Step 5: List Finding Aggregations
    # ------------------------------------------------------------------
    def step_list_finding_aggregations(self) -> None:
        """Lists finding aggregations by type and account."""
        print("\n" + "-" * 68)
        print("Step 5: Listing finding aggregations...")
        print("-" * 68)

        # By FINDING_TYPE
        print("\n  Findings by Type:")
        type_aggs = self.inspector2_wrapper.list_finding_aggregations("FINDING_TYPE")
        if type_aggs:
            for agg in type_aggs:
                finding_type_agg = agg.get("findingTypeAggregation", dict())
                acct_id = finding_type_agg.get("accountId", "N/A")
                severity_counts = finding_type_agg.get("severityCounts", dict())
                total = sum(severity_counts.values()) if severity_counts else 0
                print(
                    f"    Account {acct_id}: "
                    f"Critical={severity_counts.get('critical', 0)}, "
                    f"High={severity_counts.get('high', 0)}, "
                    f"Medium={severity_counts.get('medium', 0)}, "
                    f"All={severity_counts.get('all', total)}"
                )
        else:
            print("    No findings found. This is common for new accounts.")

        # By ACCOUNT
        print("\n  Findings by Account:")
        acct_aggs = self.inspector2_wrapper.list_finding_aggregations("ACCOUNT")
        if acct_aggs:
            for agg in acct_aggs:
                acct_agg = agg.get("accountAggregation", dict())
                acct_id = acct_agg.get("accountId", "N/A")
                severity_counts = acct_agg.get("severityCounts", dict())
                print(
                    f"    Account {acct_id}: "
                    f"Critical={severity_counts.get('critical', 0)}, "
                    f"High={severity_counts.get('high', 0)}, "
                    f"Medium={severity_counts.get('medium', 0)}, "
                    f"All={severity_counts.get('all', 0)}"
                )
        else:
            print("    No findings found.")

    # ------------------------------------------------------------------
    # Step 6: Search Vulnerabilities
    # ------------------------------------------------------------------
    def step_search_vulnerabilities(self) -> None:
        """Searches for a well-known vulnerability (CVE-2021-44228 / Log4Shell)."""
        print("\n" + "-" * 68)
        print("Step 6: Searching for vulnerability CVE-2021-44228 (Log4Shell)...")
        print("-" * 68)

        cve_id = "CVE-2021-44228"
        vulnerabilities = self.inspector2_wrapper.search_vulnerabilities([cve_id])

        if vulnerabilities:
            for vuln in vulnerabilities:
                print(f"\n  Vulnerability Details:")
                print(f"    ID         : {vuln.get('id', 'N/A')}")
                description = vuln.get("description", "N/A")
                # Truncate long descriptions for readability
                if len(description) > 200:
                    description = description[:200] + "..."
                print(f"    Description: {description}")

                cvss3 = vuln.get("cvss3", dict())
                if cvss3:
                    print(
                        f"    CVSS v3    : {cvss3.get('baseScore', 'N/A')} "
                        f"({vuln.get('vendorSeverity', 'N/A')})"
                    )

                related = vuln.get("relatedVulnerabilities", list())
                if related:
                    print(f"    Related    : {', '.join(related[:5])}")

                source_url = vuln.get("sourceUrl", None)
                if source_url is not None:
                    print(f"    Source URL  : {source_url}")
        else:
            print(f"\n  CVE {cve_id} was not found in the Inspector database.")

    # ------------------------------------------------------------------
    # Step 7: Generate Findings Report
    # ------------------------------------------------------------------
    def step_create_findings_report(self) -> str:
        """
        Creates a findings report and exports it to S3.

        :return: The report ID.
        """
        print("\n" + "-" * 68)
        print("Step 7: Generating findings report...")
        print("-" * 68)

        report_id = self.inspector2_wrapper.create_findings_report(
            report_format="CSV",
            bucket_name=self.bucket_name,
            key_prefix="inspector-reports/",
            kms_key_arn=self.kms_key_arn,
        )
        print(f"  Report generation initiated.")
        print(f"    Report ID  : {report_id}")
        print(f"    Format     : CSV")
        print(f"    Destination: s3://{self.bucket_name}/inspector-reports/")
        return report_id

    # ------------------------------------------------------------------
    # Step 8: Check Report Status
    # ------------------------------------------------------------------
    def step_check_report_status(self, report_id: str) -> None:
        """
        Polls for the findings report to complete.

        :param report_id: The report ID to check.
        """
        print("\n" + "-" * 68)
        print("Step 8: Checking findings report status...")
        print("-" * 68)

        max_polls = 24
        poll_interval = 5

        for i in range(max_polls):
            status_resp = self.inspector2_wrapper.get_findings_report_status(
                report_id
            )
            status = status_resp.get("status", "UNKNOWN")
            print(f"  Status: {status}", end="")

            if status == "COMPLETE":
                print()
                dest = status_resp.get("destination", dict())
                bucket = dest.get("bucketName", "N/A")
                prefix = dest.get("keyPrefix", "")
                print(f"\n  Report generated successfully!")
                print(f"    Report available at: s3://{bucket}/{prefix}")
                return
            elif status == "FAILED":
                print()
                error_code = status_resp.get("errorCode", "UNKNOWN")
                error_msg = status_resp.get("errorMessage", "No details available.")
                print(f"\n  Report generation failed: {error_code} - {error_msg}")
                return
            else:
                print(f"... waiting {poll_interval} seconds.")
                time.sleep(poll_interval)

        print(
            f"\n  Report did not complete within {max_polls * poll_interval} seconds. "
            "Check the AWS console for the latest status."
        )

    # ------------------------------------------------------------------
    # Step 9: List Account Permissions
    # ------------------------------------------------------------------
    def step_list_account_permissions(self) -> None:
        """Lists account permissions for Amazon Inspector."""
        print("\n" + "-" * 68)
        print("Step 9: Listing account permissions for Amazon Inspector...")
        print("-" * 68)

        permissions = self.inspector2_wrapper.list_account_permissions()
        if permissions:
            print("\n  Account Permissions:")
            for idx, perm in enumerate(permissions, 1):
                operation = perm.get("operation", "UNKNOWN")
                service = perm.get("service", "UNKNOWN")
                print(f"    {idx}. {operation} - {service}")
            print(f"\n  Total permissions: {len(permissions)}")
        else:
            print("  No permissions found.")

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------
    def cleanup(self) -> None:
        """Disables Inspector (if appropriate) and deletes the CloudFormation stack."""
        print("\n" + "=" * 68)
        print("Cleanup")
        print("=" * 68)

        # Disable Inspector if it was not previously enabled
        if not self.was_previously_enabled:
            print("\n  Inspector was not enabled before this scenario. Disabling...")
            try:
                self.inspector2_wrapper.disable(resource_types=["EC2", "ECR"])
                print("  EC2 and ECR scanning disabled successfully.")
            except ClientError as err:
                logger.error("Error disabling Inspector: %s", err)
                print(f"  Warning: Could not disable Inspector: {err}")
        else:
            print(
                "\n  Inspector was already enabled before this scenario. "
                "Skipping disable to preserve your existing configuration."
            )

        # Empty and delete the S3 bucket, then delete the stack
        if self.bucket_name:
            print(f"\n  Emptying S3 bucket '{self.bucket_name}'...")
            try:
                objects_resp = self.s3_client.list_objects_v2(
                    Bucket=self.bucket_name
                )
                objects = objects_resp.get("Contents", list())
                if objects:
                    delete_objects = [{"Key": obj["Key"]} for obj in objects]
                    self.s3_client.delete_objects(
                        Bucket=self.bucket_name,
                        Delete={"Objects": delete_objects},
                    )
                    print(f"    Deleted {len(delete_objects)} objects.")
                else:
                    print("    Bucket is already empty.")
            except ClientError as err:
                logger.error("Error emptying bucket: %s", err)
                print(f"    Warning: Could not empty bucket: {err}")

        print(f"\n  Deleting CloudFormation stack '{self.stack_name}'...")
        try:
            self.cf_client.delete_stack(StackName=self.stack_name)
            waiter = self.cf_client.get_waiter("stack_delete_complete")
            print("  Waiting for stack deletion to complete...")
            waiter.wait(
                StackName=self.stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
            print("  Stack deleted successfully.")
        except ClientError as err:
            logger.error("Error deleting stack: %s", err)
            print(f"  Warning: Could not delete stack: {err}")

        print("\n  Cleanup completed.")

    # ------------------------------------------------------------------
    # Run the scenario
    # ------------------------------------------------------------------
    def run_scenario(self) -> None:
        """Runs all steps of the Amazon Inspector Basics scenario."""
        print("\n" + "=" * 68)
        print("  Welcome to the Amazon Inspector Basics Scenario")
        print("=" * 68)
        print(
            "\nThis scenario demonstrates the full lifecycle of Amazon Inspector:\n"
            "  - Enable scanning for EC2 and ECR\n"
            "  - Verify account status and scan configuration\n"
            "  - Update scan configuration\n"
            "  - View aggregated findings\n"
            "  - Search for specific vulnerabilities\n"
            "  - Generate and export findings reports\n"
            "  - List account permissions\n"
            "  - Clean up resources\n"
        )

        try:
            self.setup()

            self.step_enable_inspector()

            # Brief wait for enable to propagate
            print("\n  Waiting a moment for Inspector to fully activate...")
            time.sleep(5)

            self.step_verify_account_status()
            self.step_review_configuration()
            self.step_update_configuration()
            self.step_list_finding_aggregations()
            self.step_search_vulnerabilities()

            report_id = self.step_create_findings_report()
            if report_id:
                self.step_check_report_status(report_id)

            self.step_list_account_permissions()

        finally:
            self.cleanup()

        print("\n" + "=" * 68)
        print("  Amazon Inspector Basics scenario completed successfully!")
        print("=" * 68)


# snippet-end:[python.example_code.inspector2.Inspector2Scenario]


def main() -> None:
    """Entry point for the Amazon Inspector Basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    inspector2_client = boto3.client("inspector2")
    cf_client = boto3.client("cloudformation")
    s3_client = boto3.client("s3")

    wrapper = Inspector2Wrapper(inspector2_client)
    scenario = Inspector2Scenario(wrapper, cf_client, s3_client)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
