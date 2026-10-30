# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates how to use Amazon RDS Blue/Green Deployments to migrate the
    KMS encryption key for an RDS DB instance with near-zero downtime.

    This scenario walks through the full lifecycle:
    Setup: Deploy an AWS CloudFormation stack that creates two KMS keys
       (original and target).
    1. Create an encrypted RDS DB instance with an original KMS key.
    2. Verify the source DB instance encryption.
    3. Create a Blue/Green Deployment with TargetResourceConfigurations to
       specify a different target KMS key for the green environment.
    4. Monitor the deployment until the green environment is AVAILABLE.
    5. Verify the green environment uses the target KMS key.
    6. Switchover production traffic from blue to green.
    7. Verify post-switchover encryption with the new KMS key.
    Cleanup: Optionally delete the RDS resources and the CloudFormation stack.
"""

import logging
import os
import sys
import uuid

import boto3

# Add this folder (for the helper and wrapper modules) and the python folder
# (for demo_tools) to the path, so the script runs from any directory.
_SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.append(_SCRIPT_DIR)
sys.path.append(os.path.abspath(os.path.join(_SCRIPT_DIR, "..", "..", "..")))
import demo_tools.question as q  # noqa: E402

from cloudformation_helper import CloudFormationHelper  # noqa: E402
from rds_blue_green_encryption_migration_wrapper import (  # noqa: E402
    BlueGreenEncryptionMigrationWrapper,
)

logger = logging.getLogger(__name__)

STACK_NAME = "RdsBlueGreenKmsKeysStack"
ORIGINAL_KEY_OUTPUT = "OriginalKmsKeyArn"
TARGET_KEY_OUTPUT = "TargetKmsKeyArn"
KMS_PENDING_WINDOW_DAYS = 7


# snippet-start:[python.example_code.rds.BlueGreenEncryptionMigrationScenario]
class BlueGreenEncryptionMigrationScenario:
    """Runs an interactive scenario for RDS Blue/Green KMS key migration."""

    def __init__(
        self,
        wrapper: BlueGreenEncryptionMigrationWrapper,
        cfn_helper: CloudFormationHelper,
        stack_name: str = STACK_NAME,
    ):
        """
        Initializes the scenario.

        :param wrapper: A BlueGreenEncryptionMigrationWrapper instance.
        :param cfn_helper: A CloudFormationHelper that deploys and deletes the
            stack that creates the KMS keys.
        :param stack_name: The name of the CloudFormation stack.
        """
        self.wrapper = wrapper
        self.cfn_helper = cfn_helper
        self.stack_name = stack_name
        self.stack_deployed = False
        self.original_kms_key_id = None
        self.target_kms_key_id = None
        self.db_instance_identifier = None
        self.db_instance_arn = None
        self.db_instance_created = False
        self.deployment_identifier = None
        self.green_identifier = None
        self.switchover_completed = False
        self.old_blue_identifier = None

    def run(
        self,
        db_instance_identifier: str,
        deployment_name: str,
        engine: str = "mysql",
        db_instance_class: str = "db.t3.micro",
        allocated_storage: int = 20,
        master_username: str = "admin",
        master_user_password: str = None,
    ) -> None:
        """
        Runs the full Blue/Green encryption migration scenario. The KMS keys
        are created by a CloudFormation stack in the Setup section.

        :param db_instance_identifier: Unique name for the DB instance.
        :param deployment_name: Unique name for the Blue/Green Deployment.
        :param engine: Database engine (default: 'mysql').
        :param db_instance_class: DB instance class (default: 'db.t3.micro').
        :param allocated_storage: Storage in GiB (default: 20).
        :param master_username: Master username (default: 'admin').
        :param master_user_password: Master password. Auto-generated if None.
        """
        if master_user_password is None:
            master_user_password = f"Pass-{uuid.uuid4().hex[:16]}"

        self.db_instance_identifier = db_instance_identifier
        print("-" * 70)
        print("Amazon RDS Blue/Green Deployment — KMS Encryption Key Migration")
        print("-" * 70)

        try:
            self._setup()

            # Step 1: Create an encrypted source DB instance
            print("\nStep 1: Creating an encrypted source DB instance...")
            print(f"  Identifier:  {db_instance_identifier}")
            print(f"  Engine:      {engine}")
            print(f"  KMS Key:     {self.original_kms_key_id}")
            self.wrapper.create_db_instance(
                db_instance_identifier=db_instance_identifier,
                db_instance_class=db_instance_class,
                engine=engine,
                master_username=master_username,
                master_user_password=master_user_password,
                allocated_storage=allocated_storage,
                kms_key_id=self.original_kms_key_id,
                backup_retention_period=1,
            )
            self.db_instance_created = True
            print("  Waiting for DB instance to become available...")
            self.wrapper.wait_for_db_instance_available(db_instance_identifier)
            print("  ✓ DB instance is available.")

            # Step 2: Verify source DB instance encryption
            print("\nStep 2: Verifying source DB instance encryption...")
            db_instance = self.wrapper.describe_db_instance(db_instance_identifier)
            self.db_instance_arn = db_instance["DBInstanceArn"]
            print(f"  Encrypted:   {db_instance.get('StorageEncrypted', False)}")
            print(f"  KMS Key ID:  {db_instance.get('KmsKeyId', 'N/A')}")
            print(f"  Instance ARN: {self.db_instance_arn}")
            print("  ✓ Source encryption verified.")

            # Step 3: Create Blue/Green Deployment with encryption migration
            print("\nStep 3: Creating Blue/Green Deployment with new KMS key...")
            print(f"  Deployment Name:  {deployment_name}")
            print(f"  Target KMS Key:   {self.target_kms_key_id}")
            deployment = self.wrapper.create_blue_green_deployment(
                deployment_name=deployment_name,
                source_arn=self.db_instance_arn,
                target_kms_key_id=self.target_kms_key_id,
            )
            self.deployment_identifier = deployment["BlueGreenDeploymentIdentifier"]
            print(f"  Deployment ID: {self.deployment_identifier}")
            print(f"  Status:        {deployment['Status']}")

            # Step 4: Monitor the Blue/Green Deployment
            print("\nStep 4: Monitoring Blue/Green Deployment until AVAILABLE...")
            print("  This typically takes 10-20 minutes...")
            deployment = self.wrapper.wait_for_deployment_available(
                self.deployment_identifier
            )
            print("  ✓ Blue/Green Deployment is AVAILABLE.")

            # Step 5: Verify green environment encryption
            print("\nStep 5: Verifying green environment encryption...")
            target_arn = deployment.get("Target", "")
            green_db_id = target_arn.split(":")[-1] if target_arn else None
            self.green_identifier = green_db_id
            if green_db_id:
                green_instance = self.wrapper.describe_db_instance(green_db_id)
                print(
                    f"  Green Encrypted:  "
                    f"{green_instance.get('StorageEncrypted', False)}"
                )
                print(
                    f"  Green KMS Key ID: "
                    f"{green_instance.get('KmsKeyId', 'N/A')}"
                )
                print("  ✓ Green environment encryption verified.")
            else:
                print("  ⚠ Could not determine green DB instance identifier.")

            # Step 6: Switchover to the green environment
            print("\nStep 6: Switching over to the green environment...")
            self.wrapper.switchover_blue_green_deployment(
                self.deployment_identifier, switchover_timeout=600
            )
            print("  Waiting for switchover to complete...")
            deployment = self.wrapper.wait_for_switchover_completed(
                self.deployment_identifier
            )
            self.switchover_completed = True
            print("  ✓ Switchover completed.")

            # Determine old blue instance identifier from SwitchoverDetails
            switchover_details = deployment.get("SwitchoverDetails", list())
            for detail in switchover_details:
                source_member = detail.get("SourceMember", "")
                if source_member:
                    self.old_blue_identifier = source_member.split(":")[-1]
                    break

            # Step 7: Verify post-switchover encryption
            print("\nStep 7: Verifying post-switchover encryption...")
            post_instance = self.wrapper.describe_db_instance(db_instance_identifier)
            print(f"  Encrypted:  {post_instance.get('StorageEncrypted', False)}")
            print(f"  KMS Key ID: {post_instance.get('KmsKeyId', 'N/A')}")
            print("  ✓ Production database now uses the new KMS encryption key.")

            print("\n" + "=" * 70)
            print("SUCCESS: KMS encryption key migration completed!")
            print("=" * 70)

        except Exception:
            logger.exception("Scenario encountered an error.")
            raise
        finally:
            self._cleanup()

    def _setup(self) -> None:
        """
        Deploys the CloudFormation stack that creates the original and target
        KMS keys, and reads the key ARNs from the stack outputs.
        """
        print("\n" + "-" * 70)
        print("Setup")
        print("-" * 70)
        print(
            f"Deploying CloudFormation stack '{self.stack_name}', which creates\n"
            "two KMS keys (original and target). This can take a few minutes..."
        )
        # Mark the stack as deployed before creating it, so a partially
        # created stack is still offered for cleanup.
        self.stack_deployed = True
        if self.cfn_helper.deploy_stack(self.stack_name):
            print(f"  ✓ Stack '{self.stack_name}' created.")
        else:
            print(f"  Stack '{self.stack_name}' already exists. Using it.")

        outputs = self.cfn_helper.get_stack_outputs(self.stack_name)
        if ORIGINAL_KEY_OUTPUT not in outputs or TARGET_KEY_OUTPUT not in outputs:
            raise RuntimeError(
                f"Stack '{self.stack_name}' is missing the '{ORIGINAL_KEY_OUTPUT}' "
                f"or '{TARGET_KEY_OUTPUT}' output. Delete the stack and run the "
                "scenario again."
            )
        self.original_kms_key_id = outputs[ORIGINAL_KEY_OUTPUT]
        self.target_kms_key_id = outputs[TARGET_KEY_OUTPUT]
        print(f"  Original KMS key ARN: {self.original_kms_key_id}")
        print(f"  Target KMS key ARN:   {self.target_kms_key_id}")
        print("  ✓ Setup complete.")

    def _print_retained_resources(self) -> None:
        """Prints the resources that were not deleted by the scenario."""
        print("\nThe following resources were kept:")
        if self.stack_deployed:
            print(f"  CloudFormation stack:     {self.stack_name}")
        if self.original_kms_key_id:
            print(f"  Original KMS key ARN:     {self.original_kms_key_id}")
        if self.target_kms_key_id:
            print(f"  Target KMS key ARN:       {self.target_kms_key_id}")
        if self.db_instance_created:
            print(f"  DB instance:              {self.db_instance_identifier}")
        if self.old_blue_identifier:
            print(f"  Old blue DB instance:     {self.old_blue_identifier}")
        if self.green_identifier and not self.switchover_completed:
            print(f"  Green DB instance:        {self.green_identifier}")
        if self.deployment_identifier:
            print(f"  Blue/Green Deployment ID: {self.deployment_identifier}")
        print(
            "\nDelete the DB instances and the Blue/Green Deployment before the "
            "stack, because the DB instances use the KMS keys. To delete the "
            "stack later, run:"
        )
        print(f"  aws cloudformation delete-stack --stack-name {self.stack_name}")

    def _cleanup(self) -> None:
        """
        Asks whether to delete the resources created during the scenario. If
        the answer is yes, deletes the RDS resources first and then the
        CloudFormation stack that holds the KMS keys.
        """
        if not self.stack_deployed and not self.db_instance_created:
            return

        print("\n" + "-" * 70)
        print("Cleanup")
        print("-" * 70)
        delete_resources = q.ask(
            "\nDo you want to delete the resources created by this scenario (y/n)? ",
            q.is_yesno,
        )
        if not delete_resources:
            self._print_retained_resources()
            print("Resources were not deleted.")
            return

        print("\nCleaning up resources...")
        rds_cleanup_ok = True

        # Delete the Blue/Green Deployment. Before switchover, this also
        # deletes the green DB instance. After switchover, DeleteTarget must
        # be False.
        if self.deployment_identifier:
            try:
                print(
                    f"  Deleting Blue/Green Deployment "
                    f"'{self.deployment_identifier}'..."
                )
                self.wrapper.delete_blue_green_deployment(
                    self.deployment_identifier,
                    delete_target=not self.switchover_completed,
                )
                print("  ✓ Blue/Green Deployment deletion initiated.")
                if not self.switchover_completed and self.green_identifier:
                    print(
                        f"  Waiting for green DB instance "
                        f"'{self.green_identifier}' to be deleted..."
                    )
                    self.wrapper.wait_for_db_instance_deleted(self.green_identifier)
                    print("  ✓ Green DB instance deleted.")
            except Exception:
                rds_cleanup_ok = False
                logger.exception(
                    "Failed to delete Blue/Green Deployment '%s'.",
                    self.deployment_identifier,
                )

        # Delete the old blue DB instance (renamed during switchover)
        if self.old_blue_identifier:
            try:
                print(
                    f"  Deleting old blue DB instance "
                    f"'{self.old_blue_identifier}'..."
                )
                self.wrapper.delete_db_instance(self.old_blue_identifier)
                print("  Waiting for old blue DB instance to be deleted...")
                self.wrapper.wait_for_db_instance_deleted(self.old_blue_identifier)
                print("  ✓ Old blue DB instance deleted.")
            except Exception:
                rds_cleanup_ok = False
                logger.exception(
                    "Failed to delete old blue DB instance '%s'.",
                    self.old_blue_identifier,
                )

        # Delete the production DB instance (the former green, now primary)
        if self.db_instance_created:
            try:
                print(
                    f"  Deleting production DB instance "
                    f"'{self.db_instance_identifier}'..."
                )
                self.wrapper.delete_db_instance(self.db_instance_identifier)
                print("  Waiting for production DB instance to be deleted...")
                self.wrapper.wait_for_db_instance_deleted(self.db_instance_identifier)
                print("  ✓ Production DB instance deleted.")
            except Exception:
                rds_cleanup_ok = False
                logger.exception(
                    "Failed to delete DB instance '%s'.",
                    self.db_instance_identifier,
                )

        # Delete the CloudFormation stack only after the RDS resources that use
        # its KMS keys are gone.
        if self.stack_deployed:
            if rds_cleanup_ok:
                try:
                    print(f"  Deleting CloudFormation stack '{self.stack_name}'...")
                    self.cfn_helper.destroy_stack(self.stack_name)
                    print(
                        "  ✓ CloudFormation stack deleted. The KMS keys are "
                        "scheduled for deletion and will be permanently deleted "
                        f"after the {KMS_PENDING_WINDOW_DAYS}-day pending window."
                    )
                except Exception:
                    logger.exception(
                        "Failed to delete CloudFormation stack '%s'.",
                        self.stack_name,
                    )
            else:
                print(
                    f"  ⚠ CloudFormation stack '{self.stack_name}' was kept because "
                    "some RDS resources might still use its KMS keys."
                )
                self._print_retained_resources()

        print("Cleanup complete.")


# snippet-end:[python.example_code.rds.BlueGreenEncryptionMigrationScenario]


def main():
    """Entry point for running the scenario from the command line."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("Amazon RDS Blue/Green Deployment — KMS Encryption Key Migration Scenario")
    print("=" * 70)
    print(
        "\nThis scenario demonstrates how to migrate the KMS encryption key\n"
        "for an RDS DB instance using Blue/Green Deployments with the new\n"
        "TargetResourceConfigurations parameter.\n"
    )
    print("Prerequisites:")
    print(
        "  - Sufficient permissions for Amazon RDS, AWS KMS, and AWS "
        "CloudFormation operations."
    )
    print(
        "  - The scenario creates the two KMS keys (original and target) for you\n"
        f"    with a CloudFormation stack ({STACK_NAME}).\n"
    )

    unique_suffix = uuid.uuid4().hex[:8]
    db_instance_identifier = f"bg-enc-demo-{unique_suffix}"
    deployment_name = f"kms-migration-{unique_suffix}"

    rds_client = boto3.client("rds")
    wrapper = BlueGreenEncryptionMigrationWrapper(rds_client)
    cfn_helper = CloudFormationHelper.from_client()
    scenario = BlueGreenEncryptionMigrationScenario(wrapper, cfn_helper)

    scenario.run(
        db_instance_identifier=db_instance_identifier,
        deployment_name=deployment_name,
    )


if __name__ == "__main__":
    main()
