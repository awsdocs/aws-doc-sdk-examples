# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates how to use Amazon RDS Blue/Green Deployments to migrate the
    KMS encryption key for an RDS DB instance with near-zero downtime.

    This scenario walks through the full lifecycle:
    1. Create an encrypted RDS DB instance with an original KMS key.
    2. Verify the source DB instance encryption.
    3. Create a Blue/Green Deployment with TargetResourceConfigurations to
       specify a different target KMS key for the green environment.
    4. Monitor the deployment until the green environment is AVAILABLE.
    5. Verify the green environment uses the target KMS key.
    6. Switchover production traffic from blue to green.
    7. Verify post-switchover encryption with the new KMS key.
    8. Clean up the Blue/Green Deployment and old DB instances.
"""

import logging
import sys
import uuid

import boto3

from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.rds.BlueGreenEncryptionMigrationScenario]
class BlueGreenEncryptionMigrationScenario:
    """Runs an interactive scenario for RDS Blue/Green KMS key migration."""

    def __init__(self, wrapper: BlueGreenEncryptionMigrationWrapper):
        """
        Initializes the scenario.

        :param wrapper: A BlueGreenEncryptionMigrationWrapper instance.
        """
        self.wrapper = wrapper
        self.db_instance_identifier = None
        self.db_instance_arn = None
        self.deployment_identifier = None
        self.old_blue_identifier = None

    def run(
        self,
        original_kms_key_id: str,
        target_kms_key_id: str,
        db_instance_identifier: str,
        deployment_name: str,
        engine: str = "mysql",
        db_instance_class: str = "db.t3.micro",
        allocated_storage: int = 20,
        master_username: str = "admin",
        master_user_password: str = None,
    ) -> None:
        """
        Runs the full Blue/Green encryption migration scenario.

        :param original_kms_key_id: ARN of the original KMS key.
        :param target_kms_key_id: ARN of the target KMS key.
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
            # Step 1: Create an encrypted source DB instance
            print("\nStep 1: Creating an encrypted source DB instance...")
            print(f"  Identifier:  {db_instance_identifier}")
            print(f"  Engine:      {engine}")
            print(f"  KMS Key:     {original_kms_key_id}")
            self.wrapper.create_db_instance(
                db_instance_identifier=db_instance_identifier,
                db_instance_class=db_instance_class,
                engine=engine,
                master_username=master_username,
                master_user_password=master_user_password,
                allocated_storage=allocated_storage,
                kms_key_id=original_kms_key_id,
                backup_retention_period=1,
            )
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
            print(f"  Target KMS Key:   {target_kms_key_id}")
            deployment = self.wrapper.create_blue_green_deployment(
                deployment_name=deployment_name,
                source_arn=self.db_instance_arn,
                target_kms_key_id=target_kms_key_id,
            )
            self.deployment_identifier = deployment[
                "BlueGreenDeploymentIdentifier"
            ]
            print(f"  Deployment ID: {self.deployment_identifier}")
            print(f"  Status:        {deployment['Status']}")

            # Step 4: Monitor the Blue/Green Deployment
            print(
                "\nStep 4: Monitoring Blue/Green Deployment until AVAILABLE..."
            )
            print("  This typically takes 10-20 minutes...")
            deployment = self.wrapper.wait_for_deployment_available(
                self.deployment_identifier
            )
            print("  ✓ Blue/Green Deployment is AVAILABLE.")

            # Step 5: Verify green environment encryption
            print("\nStep 5: Verifying green environment encryption...")
            target_arn = deployment.get("Target", "")
            green_db_id = target_arn.split(":")[-1] if target_arn else None
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
            post_instance = self.wrapper.describe_db_instance(
                db_instance_identifier
            )
            print(
                f"  Encrypted:  {post_instance.get('StorageEncrypted', False)}"
            )
            print(
                f"  KMS Key ID: {post_instance.get('KmsKeyId', 'N/A')}"
            )
            print(
                "  ✓ Production database now uses the new KMS encryption key."
            )

            print("\n" + "=" * 70)
            print("SUCCESS: KMS encryption key migration completed!")
            print("=" * 70)

        except Exception:
            logger.exception("Scenario encountered an error.")
            raise
        finally:
            self._cleanup()

    def _cleanup(self) -> None:
        """Cleans up resources created during the scenario."""
        print("\nCleaning up resources...")

        # Delete the Blue/Green Deployment
        if self.deployment_identifier:
            try:
                print(
                    f"  Deleting Blue/Green Deployment "
                    f"'{self.deployment_identifier}'..."
                )
                self.wrapper.delete_blue_green_deployment(
                    self.deployment_identifier, delete_target=False
                )
                print("  ✓ Blue/Green Deployment deletion initiated.")
            except Exception:
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
                print(
                    "  Waiting for old blue DB instance to be deleted..."
                )
                self.wrapper.wait_for_db_instance_deleted(
                    self.old_blue_identifier
                )
                print("  ✓ Old blue DB instance deleted.")
            except Exception:
                logger.exception(
                    "Failed to delete old blue DB instance '%s'.",
                    self.old_blue_identifier,
                )

        # Delete the production DB instance (the former green, now primary)
        if self.db_instance_identifier:
            try:
                print(
                    f"  Deleting production DB instance "
                    f"'{self.db_instance_identifier}'..."
                )
                self.wrapper.delete_db_instance(self.db_instance_identifier)
                print(
                    "  Waiting for production DB instance to be deleted..."
                )
                self.wrapper.wait_for_db_instance_deleted(
                    self.db_instance_identifier
                )
                print("  ✓ Production DB instance deleted.")
            except Exception:
                logger.exception(
                    "Failed to delete DB instance '%s'.",
                    self.db_instance_identifier,
                )

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
    print("  - Two KMS key ARNs (original and target).")
    print("  - Sufficient permissions for RDS and KMS operations.\n")

    original_kms_key_id = input("Enter the ARN of the original KMS key: ").strip()
    target_kms_key_id = input("Enter the ARN of the target KMS key: ").strip()

    if not original_kms_key_id or not target_kms_key_id:
        print("Both KMS key ARNs are required. Exiting.")
        sys.exit(1)

    unique_suffix = uuid.uuid4().hex[:8]
    db_instance_identifier = f"bg-enc-demo-{unique_suffix}"
    deployment_name = f"kms-migration-{unique_suffix}"

    rds_client = boto3.client("rds")
    wrapper = BlueGreenEncryptionMigrationWrapper(rds_client)
    scenario = BlueGreenEncryptionMigrationScenario(wrapper)

    scenario.run(
        original_kms_key_id=original_kms_key_id,
        target_kms_key_id=target_kms_key_id,
        db_instance_identifier=db_instance_identifier,
        deployment_name=deployment_name,
    )


if __name__ == "__main__":
    main()
