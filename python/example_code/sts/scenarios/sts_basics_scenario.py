# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: AWS STS Basics Scenario

This scenario demonstrates a real-world workflow where an administrator explores
the AWS Security Token Service to understand credential management, role
assumption, and identity verification.

The scenario covers these steps:
  1. Verify current identity (GetCallerIdentity)
  2. Assume a role (AssumeRole)
  3. Verify assumed-role identity (GetCallerIdentity)
  4. Get access key info for temporary credentials (GetAccessKeyInfo)
  5. Get a session token (GetSessionToken)
  6. Get a federation token (GetFederationToken)
  7. Verify federated user identity (GetCallerIdentity)
  8. Demonstrate DecodeAuthorizationMessage (error-handling path)
  9. Get access key info for original key (GetAccessKeyInfo)
 10. Final identity verification (GetCallerIdentity)

Setup deploys a CloudFormation stack that creates an IAM role with a trust
policy allowing the current account to assume it.
Cleanup deletes the CloudFormation stack.
"""

import json
import logging
import time
import uuid
from typing import Any, Dict, Optional

import boto3
from botocore.exceptions import ClientError

from sts_wrapper import STSWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sts.STSScenario]
class STSScenario:
    """Runs the AWS STS Basics scenario."""

    STACK_NAME_PREFIX = "sts-basics-scenario-stack"

    def __init__(
        self,
        sts_wrapper: STSWrapper,
        cf_client: boto3.client,
    ) -> None:
        """
        Initializes the scenario.

        :param sts_wrapper: An STSWrapper instance for STS operations.
        :param cf_client: A Boto3 CloudFormation client.
        """
        self.sts_wrapper = sts_wrapper
        self.cf_client = cf_client
        self.stack_name = None
        self.role_arn = None
        self.original_identity = None
        self.original_access_key_id = None
        self.assumed_role_credentials = None

    def _get_cfn_template(self, account_id: str) -> str:
        """
        Returns the CloudFormation template JSON string that creates an
        IAM role for use with AssumeRole.

        :param account_id: The current AWS account ID.
        :return: A JSON string representing the CloudFormation template.
        """
        unique_suffix = uuid.uuid4().hex[:8]
        role_name = f"sts-basics-scenario-role-{unique_suffix}"
        template = {
            "AWSTemplateFormatVersion": "2010-09-09",
            "Description": "Creates an IAM role for the STS Basics scenario.",
            "Resources": {
                "STSBasicsRole": {
                    "Type": "AWS::IAM::Role",
                    "Properties": {
                        "RoleName": role_name,
                        "AssumeRolePolicyDocument": {
                            "Version": "2012-10-17",
                            "Statement": [
                                {
                                    "Effect": "Allow",
                                    "Principal": {
                                        "AWS": f"arn:aws:iam::{account_id}:root"
                                    },
                                    "Action": "sts:AssumeRole",
                                }
                            ],
                        },
                        "Policies": [
                            {
                                "PolicyName": "STSBasicsPolicy",
                                "PolicyDocument": {
                                    "Version": "2012-10-17",
                                    "Statement": [
                                        {
                                            "Effect": "Allow",
                                            "Action": [
                                                "sts:GetCallerIdentity",
                                                "sts:GetAccessKeyInfo",
                                                "sts:DecodeAuthorizationMessage",
                                            ],
                                            "Resource": "*",
                                        }
                                    ],
                                },
                            }
                        ],
                    },
                }
            },
            "Outputs": {
                "RoleArn": {
                    "Value": {"Fn::GetAtt": ["STSBasicsRole", "Arn"]},
                    "Description": "ARN of the STS basics scenario role.",
                }
            },
        }
        return json.dumps(template)

    def setup(self) -> None:
        """
        Deploys a CloudFormation stack that creates an IAM role for the scenario.
        Waits for stack creation to complete and extracts the Role ARN from outputs.
        """
        print("\n" + "=" * 72)
        print("  Setting up the STS Basics Scenario")
        print("=" * 72)

        # Get the current account ID for the trust policy.
        identity = self.sts_wrapper.get_caller_identity()
        account_id = identity["Account"]
        self.original_access_key_id = None

        template_body = self._get_cfn_template(account_id)
        unique_suffix = uuid.uuid4().hex[:8]
        self.stack_name = f"{self.STACK_NAME_PREFIX}-{unique_suffix}"

        print(f"\n  Deploying CloudFormation stack: {self.stack_name}...")
        try:
            self.cf_client.create_stack(
                StackName=self.stack_name,
                TemplateBody=template_body,
                Capabilities=["CAPABILITY_NAMED_IAM"],
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "AlreadyExistsException":
                logger.error(
                    "Stack %s already exists. Delete it or use a different name.",
                    self.stack_name,
                )
            raise

        # Wait for stack creation to complete.
        print("  Waiting for stack creation to complete...")
        waiter = self.cf_client.get_waiter("stack_create_complete")
        waiter.wait(
            StackName=self.stack_name,
            WaiterConfig={"Delay": 10, "MaxAttempts": 60},
        )

        # Extract Role ARN from outputs.
        response = self.cf_client.describe_stacks(StackName=self.stack_name)
        outputs = response["Stacks"][0].get("Outputs", list())
        for output in outputs:
            if output["OutputKey"] == "RoleArn":
                self.role_arn = output["OutputValue"]
                break

        if self.role_arn is None:
            raise RuntimeError(
                f"Could not find RoleArn output in stack {self.stack_name}"
            )

        print(f"  Setup complete. Created IAM role: {self.role_arn}")

        # Allow time for IAM role propagation.
        print("  Waiting for IAM role to propagate...")
        time.sleep(10)

    def step_1_verify_identity(self) -> None:
        """
        Step 1: Verify Current Identity using GetCallerIdentity.
        """
        print("\n" + "-" * 72)
        print("Step 1: Verifying your current identity...")
        print("-" * 72)

        self.original_identity = self.sts_wrapper.get_caller_identity()

        print(f"  Account: {self.original_identity['Account']}")
        print(f"  ARN:     {self.original_identity['Arn']}")
        print(f"  User ID: {self.original_identity['UserId']}")
        print(
            "\nGetCallerIdentity requires no special permissions and always works,\n"
            "even if an explicit deny policy is attached to the caller."
        )

        # Extract the access key ID from the current session credentials.
        session = boto3.Session()
        credentials = session.get_credentials()
        if credentials is not None:
            frozen = credentials.get_frozen_credentials()
            self.original_access_key_id = frozen.access_key

        print("Your identity has been confirmed.")

    def step_2_assume_role(self) -> None:
        """
        Step 2: Assume the IAM role created during setup.
        """
        print("\n" + "-" * 72)
        print("Step 2: Assuming role...")
        print("-" * 72)

        result = self.sts_wrapper.assume_role(
            role_arn=self.role_arn,
            role_session_name="sts-basics-session",
            duration_seconds=900,
        )

        self.assumed_role_credentials = result["Credentials"]
        assumed_role_user = result["AssumedRoleUser"]

        print(f"  Assumed Role ARN: {assumed_role_user['Arn']}")
        print(f"  Credentials expire at: {self.assumed_role_credentials['Expiration']}")
        print(
            f"  Access Key ID (temporary): {self.assumed_role_credentials['AccessKeyId']}"
        )
        print(
            "\nAssumeRole is the most common way to obtain temporary credentials\n"
            "for cross-account or delegated access."
        )
        print("Role assumption successful. You now have temporary credentials.")

    def step_3_verify_assumed_identity(self) -> None:
        """
        Step 3: Verify the assumed-role identity using a new STS client
        with the temporary credentials.
        """
        print("\n" + "-" * 72)
        print("Step 3: Verifying assumed-role identity...")
        print("-" * 72)

        assumed_sts_client = boto3.client(
            "sts",
            aws_access_key_id=self.assumed_role_credentials["AccessKeyId"],
            aws_secret_access_key=self.assumed_role_credentials["SecretAccessKey"],
            aws_session_token=self.assumed_role_credentials["SessionToken"],
        )
        assumed_wrapper = STSWrapper(assumed_sts_client)
        assumed_identity = assumed_wrapper.get_caller_identity()

        print(f"  Account: {assumed_identity['Account']}")
        print(f"  ARN:     {assumed_identity['Arn']}")
        print(f"  User ID: {assumed_identity['UserId']}")
        print(
            "\nNotice how the ARN changed from an IAM user to an assumed-role principal.\n"
            "The format is: arn:aws:sts::{account}:assumed-role/{role-name}/{session-name}"
        )

    def step_4_get_access_key_info(self) -> None:
        """
        Step 4: Inspect access key ownership using GetAccessKeyInfo.
        """
        print("\n" + "-" * 72)
        print("Step 4: Inspecting access key ownership...")
        print("-" * 72)

        # Check the temporary access key.
        temp_key = self.assumed_role_credentials["AccessKeyId"]
        temp_account = self.sts_wrapper.get_access_key_info(temp_key)
        print(
            f"  Temporary access key {temp_key} belongs to account: {temp_account}"
        )

        # Check the original access key if available.
        if self.original_access_key_id is not None:
            orig_account = self.sts_wrapper.get_access_key_info(
                self.original_access_key_id
            )
            print(
                f"  Original access key  {self.original_access_key_id} "
                f"belongs to account: {orig_account}"
            )

        print(
            "\nAKIA-prefixed keys are long-term credentials.\n"
            "ASIA-prefixed keys are temporary STS credentials."
        )

    def step_5_get_session_token(self) -> None:
        """
        Step 5: Get a session token using the original IAM user credentials.
        """
        print("\n" + "-" * 72)
        print("Step 5: Getting a session token...")
        print("-" * 72)

        result = self.sts_wrapper.get_session_token(duration_seconds=900)
        session_creds = result["Credentials"]

        print("  Session token credentials:")
        print(f"    Access Key ID: {session_creds['AccessKeyId']}")
        print(f"    Expires at:    {session_creds['Expiration']}")
        print(
            "\nSession tokens are useful for adding MFA protection to "
            "programmatic API calls."
        )

    def step_6_get_federation_token(self) -> Dict[str, Any]:
        """
        Step 6: Get a federation token with scoped-down permissions.

        :return: The federation token response with Credentials and FederatedUser.
        """
        print("\n" + "-" * 72)
        print("Step 6: Getting a federation token...")
        print("-" * 72)

        policy = json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": "sts:GetCallerIdentity",
                        "Resource": "*",
                    }
                ],
            }
        )

        result = self.sts_wrapper.get_federation_token(
            name="sts-basics-federated-user",
            duration_seconds=900,
            policy=policy,
        )

        fed_user = result["FederatedUser"]
        fed_creds = result["Credentials"]

        print(f"  Federated User ARN: {fed_user['Arn']}")
        print(f"  Federated User ID:  {fed_user['FederatedUserId']}")
        print(f"  Credentials expire at: {fed_creds['Expiration']}")
        print(
            "\nFederation tokens are ideal for proxy applications that grant\n"
            "temporary access to users without AWS credentials."
        )
        return result

    def step_7_verify_federated_identity(
        self, fed_credentials: Dict[str, Any]
    ) -> None:
        """
        Step 7: Verify federated user identity using the federation token credentials.

        :param fed_credentials: The Credentials dict from GetFederationToken.
        """
        print("\n" + "-" * 72)
        print("Step 7: Verifying federated user identity...")
        print("-" * 72)

        fed_sts_client = boto3.client(
            "sts",
            aws_access_key_id=fed_credentials["AccessKeyId"],
            aws_secret_access_key=fed_credentials["SecretAccessKey"],
            aws_session_token=fed_credentials["SessionToken"],
        )
        fed_wrapper = STSWrapper(fed_sts_client)
        fed_identity = fed_wrapper.get_caller_identity()

        print(f"  Account: {fed_identity['Account']}")
        print(f"  ARN:     {fed_identity['Arn']}")
        print(f"  User ID: {fed_identity['UserId']}")

        print("\nSummary of identity types observed:")
        if self.original_identity is not None:
            print(f"  Original:  {self.original_identity['Arn']}")
        print(
            f"  Assumed:   (from Step 3 — assumed-role principal)"
        )
        print(f"  Federated: {fed_identity['Arn']}")

    def step_8_decode_authorization_message(self) -> None:
        """
        Step 8: Demonstrate DecodeAuthorizationMessage (error-handling path).
        """
        print("\n" + "-" * 72)
        print("Step 8: Demonstrating DecodeAuthorizationMessage...")
        print("-" * 72)
        print(
            "  In production, when an AWS API call is denied, some services return an\n"
            "  encoded authorization message. DecodeAuthorizationMessage decodes it to\n"
            "  reveal:\n"
            "    - Whether the deny was explicit or implicit\n"
            "    - The principal who made the request\n"
            "    - The requested action and resource\n"
            "    - Condition key values"
        )

        print("\n  Attempting to decode a sample message...")
        try:
            self.sts_wrapper.decode_authorization_message(
                encoded_message="This-is-not-a-valid-encoded-message"
            )
        except ClientError as error:
            error_code = error.response["Error"]["Code"]
            print(f"  Caught expected error: {error_code}")
            print(
                "  This is expected — a valid encoded message is required from an\n"
                "  actual authorization failure."
            )

    def step_9_get_access_key_info_original(self) -> None:
        """
        Step 9: Get access key info for the original long-term key.
        """
        print("\n" + "-" * 72)
        print("Step 9: Looking up your original access key's account...")
        print("-" * 72)

        if self.original_access_key_id is not None:
            account = self.sts_wrapper.get_access_key_info(
                self.original_access_key_id
            )
            print(
                f"  Access key {self.original_access_key_id} "
                f"belongs to account: {account}"
            )
        else:
            print("  Original access key ID not available (e.g. running with role credentials).")

        print(
            "\n  GetAccessKeyInfo is useful for security auditing — you can identify the\n"
            "  account that owns any access key without needing the secret key."
        )

    def step_10_final_identity_check(self) -> None:
        """
        Step 10: Final identity verification with original credentials.
        """
        print("\n" + "-" * 72)
        print("Step 10: Final identity check with original credentials...")
        print("-" * 72)

        identity = self.sts_wrapper.get_caller_identity()

        print(f"  Account: {identity['Account']}")
        print(f"  ARN:     {identity['Arn']}")
        print(f"  User ID: {identity['UserId']}")
        print(
            "\nYour original identity is intact. Temporary credentials do not affect\n"
            "your primary identity."
        )

        print("\n=== Scenario Summary ===")
        print("  Operations demonstrated:")
        print("    1.  GetCallerIdentity     — Verify current identity")
        print("    2.  AssumeRole            — Obtain temporary credentials for a role")
        print("    3.  GetCallerIdentity     — Verify assumed-role identity")
        print("    4.  GetAccessKeyInfo      — Identify account from access key")
        print("    5.  GetSessionToken       — Obtain session tokens for MFA workflows")
        print("    6.  GetFederationToken    — Create federated user credentials")
        print("    7.  GetCallerIdentity     — Verify federated user identity")
        print("    8.  DecodeAuthorizationMessage — Decode authorization failures")
        print("    9.  GetAccessKeyInfo      — Audit access key ownership")
        print("   10.  GetCallerIdentity     — Confirm original identity is unchanged")

    def cleanup(self) -> None:
        """
        Deletes the CloudFormation stack created during setup.
        """
        print("\n" + "=" * 72)
        print("  Cleaning up resources...")
        print("=" * 72)

        if self.stack_name is None:
            print("  No stack to clean up.")
            return

        try:
            print(f"  Deleting CloudFormation stack: {self.stack_name}...")
            self.cf_client.delete_stack(StackName=self.stack_name)
            print("  Waiting for stack deletion to complete...")
            waiter = self.cf_client.get_waiter("stack_delete_complete")
            waiter.wait(
                StackName=self.stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
            print("  CloudFormation stack deleted successfully.")
        except ClientError as error:
            logger.error(
                "Stack deletion failed: %s. Please delete stack '%s' manually.",
                error.response["Error"]["Message"],
                self.stack_name,
            )

        print("All resources created by this scenario have been cleaned up.")

    def run_scenario(self) -> None:
        """
        Runs the full STS Basics scenario end-to-end.
        """
        print("\n" + "=" * 72)
        print("  Welcome to the AWS STS Basics Scenario!")
        print("=" * 72)
        print(
            "\nThis demo walks you through the key STS operations for managing\n"
            "temporary security credentials, assuming roles, and verifying identities.\n"
        )

        try:
            self.setup()
            self.step_1_verify_identity()
            self.step_2_assume_role()
            self.step_3_verify_assumed_identity()
            self.step_4_get_access_key_info()
            self.step_5_get_session_token()
            fed_result = self.step_6_get_federation_token()
            self.step_7_verify_federated_identity(fed_result["Credentials"])
            self.step_8_decode_authorization_message()
            self.step_9_get_access_key_info_original()
            self.step_10_final_identity_check()
        finally:
            self.cleanup()


# snippet-end:[python.example_code.sts.STSScenario]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    try:
        sts_wrapper = STSWrapper.from_client()
        cf_client = boto3.client("cloudformation")
        scenario = STSScenario(sts_wrapper, cf_client)
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong with the STS Basics scenario.")
