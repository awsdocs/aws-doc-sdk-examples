# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates how to use the AWS SDK for Python (Boto3) with Amazon RDS
    to manage Blue/Green Deployments for KMS encryption key migration.

    This wrapper class encapsulates Amazon RDS Blue/Green Deployment operations
    used for migrating the encryption (KMS) key of an RDS DB instance with
    near-zero downtime.
"""

import logging
import time
from typing import Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.rds.BlueGreenEncryptionMigrationWrapper.decl]
class BlueGreenEncryptionMigrationWrapper:
    """Encapsulates Amazon RDS Blue/Green Deployment operations for KMS key migration."""

    def __init__(self, rds_client):
        """
        Initializes the BlueGreenEncryptionMigrationWrapper with an Amazon RDS client.

        :param rds_client: A Boto3 Amazon RDS client.
        """
        self.rds_client = rds_client

    @classmethod
    def from_client(cls) -> "BlueGreenEncryptionMigrationWrapper":
        """
        Creates a BlueGreenEncryptionMigrationWrapper using a default RDS client.

        :return: A new BlueGreenEncryptionMigrationWrapper instance.
        """
        rds_client = boto3.client("rds")
        return cls(rds_client)

    # snippet-end:[python.example_code.rds.BlueGreenEncryptionMigrationWrapper.decl]

    def create_db_instance(
        self,
        db_instance_identifier: str,
        db_instance_class: str,
        engine: str,
        master_username: str,
        master_user_password: str,
        allocated_storage: int,
        kms_key_id: str,
        backup_retention_period: int = 1,
    ) -> dict:
        """
        Creates an encrypted RDS DB instance.

        :param db_instance_identifier: A unique identifier for the DB instance.
        :param db_instance_class: The compute and memory capacity (e.g., 'db.t3.micro').
        :param engine: The database engine (e.g., 'mysql').
        :param master_username: The master user name.
        :param master_user_password: The master user password.
        :param allocated_storage: The size of the database in GiB.
        :param kms_key_id: The ARN of the KMS key for encryption.
        :param backup_retention_period: Number of days to retain automated backups.
        :return: A dictionary with DB instance details.
        """
        try:
            response = self.rds_client.create_db_instance(
                DBInstanceIdentifier=db_instance_identifier,
                DBInstanceClass=db_instance_class,
                Engine=engine,
                MasterUsername=master_username,
                MasterUserPassword=master_user_password,
                AllocatedStorage=allocated_storage,
                StorageEncrypted=True,
                KmsKeyId=kms_key_id,
                BackupRetentionPeriod=backup_retention_period,
            )
            db_instance = response["DBInstance"]
            logger.info(
                "Created DB instance '%s' with status '%s'.",
                db_instance["DBInstanceIdentifier"],
                db_instance["DBInstanceStatus"],
            )
            return db_instance
        except ClientError as err:
            if err.response["Error"]["Code"] == "DBInstanceAlreadyExistsFault":
                logger.error(
                    "DB instance '%s' already exists. Choose a different identifier.",
                    db_instance_identifier,
                )
            raise

    def describe_db_instance(self, db_instance_identifier: str) -> dict:
        """
        Describes an RDS DB instance.

        :param db_instance_identifier: The identifier of the DB instance.
        :return: A dictionary with DB instance details.
        """
        try:
            response = self.rds_client.describe_db_instances(
                DBInstanceIdentifier=db_instance_identifier
            )
            db_instance = response["DBInstances"][0]
            logger.info(
                "DB instance '%s' status: '%s', encrypted: %s, KMS key: '%s'.",
                db_instance["DBInstanceIdentifier"],
                db_instance["DBInstanceStatus"],
                db_instance.get("StorageEncrypted", False),
                db_instance.get("KmsKeyId", "N/A"),
            )
            return db_instance
        except ClientError as err:
            if err.response["Error"]["Code"] == "DBInstanceNotFoundFault":
                logger.error(
                    "DB instance '%s' not found. Verify the identifier.",
                    db_instance_identifier,
                )
            raise

    def wait_for_db_instance_available(
        self, db_instance_identifier: str, wait_seconds: int = 30, max_wait: int = 1200
    ) -> dict:
        """
        Polls DescribeDBInstances until the DB instance is available.

        :param db_instance_identifier: The identifier of the DB instance.
        :param wait_seconds: Seconds between polls.
        :param max_wait: Maximum total seconds to wait.
        :return: The DB instance description when available.
        """
        elapsed = 0
        while elapsed < max_wait:
            db_instance = self.describe_db_instance(db_instance_identifier)
            status = db_instance["DBInstanceStatus"]
            if status == "available":
                logger.info("DB instance '%s' is now available.", db_instance_identifier)
                return db_instance
            logger.info(
                "DB instance '%s' status: '%s'. Waiting %d seconds...",
                db_instance_identifier,
                status,
                wait_seconds,
            )
            time.sleep(wait_seconds)
            elapsed += wait_seconds
        raise TimeoutError(
            f"DB instance '{db_instance_identifier}' did not become available "
            f"within {max_wait} seconds."
        )

    # snippet-start:[python.example_code.rds.CreateBlueGreenDeployment]
    def create_blue_green_deployment(
        self,
        deployment_name: str,
        source_arn: str,
        target_kms_key_id: str,
    ) -> dict:
        """
        Creates a Blue/Green Deployment with a new target KMS key using
        TargetResourceConfigurations.

        :param deployment_name: A unique name for the deployment (max 60 chars).
        :param source_arn: The ARN of the source (blue) DB instance.
        :param target_kms_key_id: The ARN of the target KMS key for the green
            environment.
        :return: A dictionary with Blue/Green Deployment details.
        """
        try:
            response = self.rds_client.create_blue_green_deployment(
                BlueGreenDeploymentName=deployment_name,
                Source=source_arn,
                TargetResourceConfigurations=[
                    {
                        "SourceArn": source_arn,
                        "TargetKmsKeyId": target_kms_key_id,
                    }
                ],
            )
            deployment = response["BlueGreenDeployment"]
            logger.info(
                "Created Blue/Green Deployment '%s' (ID: '%s') with status '%s'.",
                deployment["BlueGreenDeploymentName"],
                deployment["BlueGreenDeploymentIdentifier"],
                deployment["Status"],
            )
            return deployment
        except ClientError as err:
            if err.response["Error"]["Code"] == "SourceDatabaseNotSupportedFault":
                logger.error(
                    "Source DB instance '%s' is not supported for Blue/Green "
                    "Deployments. Ensure automated backups are enabled and the "
                    "engine version is supported.",
                    source_arn,
                )
            raise

    # snippet-end:[python.example_code.rds.CreateBlueGreenDeployment]

    # snippet-start:[python.example_code.rds.DescribeBlueGreenDeployments]
    def describe_blue_green_deployment(
        self, deployment_identifier: str
    ) -> dict:
        """
        Describes a Blue/Green Deployment by its identifier.

        :param deployment_identifier: The identifier of the Blue/Green Deployment
            (e.g., 'bgd-abcdef1234567890').
        :return: A dictionary with Blue/Green Deployment details.
        """
        try:
            paginator = self.rds_client.get_paginator(
                "describe_blue_green_deployments"
            )
            deployments = list()
            for page in paginator.paginate(
                BlueGreenDeploymentIdentifier=deployment_identifier
            ):
                deployments.extend(page.get("BlueGreenDeployments", list()))
            if not deployments:
                logger.warning(
                    "No Blue/Green Deployment found with identifier '%s'.",
                    deployment_identifier,
                )
                return dict()
            deployment = deployments[0]
            logger.info(
                "Blue/Green Deployment '%s' status: '%s'.",
                deployment["BlueGreenDeploymentIdentifier"],
                deployment["Status"],
            )
            return deployment
        except ClientError as err:
            if err.response["Error"]["Code"] == "BlueGreenDeploymentNotFoundFault":
                logger.error(
                    "Blue/Green Deployment '%s' not found.",
                    deployment_identifier,
                )
            raise

    # snippet-end:[python.example_code.rds.DescribeBlueGreenDeployments]

    def wait_for_deployment_available(
        self,
        deployment_identifier: str,
        wait_seconds: int = 60,
        max_wait: int = 1800,
    ) -> dict:
        """
        Polls DescribeBlueGreenDeployments until the deployment status is AVAILABLE.

        :param deployment_identifier: The identifier of the Blue/Green Deployment.
        :param wait_seconds: Seconds between polls.
        :param max_wait: Maximum total seconds to wait.
        :return: The Blue/Green Deployment description when available.
        """
        elapsed = 0
        while elapsed < max_wait:
            deployment = self.describe_blue_green_deployment(deployment_identifier)
            status = deployment.get("Status", "UNKNOWN")
            if status == "AVAILABLE":
                logger.info(
                    "Blue/Green Deployment '%s' is now AVAILABLE.",
                    deployment_identifier,
                )
                return deployment
            if status == "INVALID_CONFIGURATION":
                raise RuntimeError(
                    f"Blue/Green Deployment '{deployment_identifier}' has "
                    f"INVALID_CONFIGURATION status. Check the green environment."
                )
            logger.info(
                "Blue/Green Deployment '%s' status: '%s'. Waiting %d seconds...",
                deployment_identifier,
                status,
                wait_seconds,
            )
            time.sleep(wait_seconds)
            elapsed += wait_seconds
        raise TimeoutError(
            f"Blue/Green Deployment '{deployment_identifier}' did not reach "
            f"AVAILABLE within {max_wait} seconds."
        )

    def wait_for_switchover_completed(
        self,
        deployment_identifier: str,
        wait_seconds: int = 30,
        max_wait: int = 600,
    ) -> dict:
        """
        Polls DescribeBlueGreenDeployments until switchover is completed.

        :param deployment_identifier: The identifier of the Blue/Green Deployment.
        :param wait_seconds: Seconds between polls.
        :param max_wait: Maximum total seconds to wait.
        :return: The Blue/Green Deployment description after switchover completes.
        """
        elapsed = 0
        while elapsed < max_wait:
            deployment = self.describe_blue_green_deployment(deployment_identifier)
            status = deployment.get("Status", "UNKNOWN")
            if status == "SWITCHOVER_COMPLETED":
                logger.info(
                    "Blue/Green Deployment '%s' switchover completed.",
                    deployment_identifier,
                )
                return deployment
            logger.info(
                "Blue/Green Deployment '%s' status: '%s'. Waiting %d seconds...",
                deployment_identifier,
                status,
                wait_seconds,
            )
            time.sleep(wait_seconds)
            elapsed += wait_seconds
        raise TimeoutError(
            f"Blue/Green Deployment '{deployment_identifier}' switchover did not "
            f"complete within {max_wait} seconds."
        )

    # snippet-start:[python.example_code.rds.SwitchoverBlueGreenDeployment]
    def switchover_blue_green_deployment(
        self, deployment_identifier: str, switchover_timeout: int = 300
    ) -> dict:
        """
        Switches over a Blue/Green Deployment, promoting the green environment
        to production.

        :param deployment_identifier: The identifier of the Blue/Green Deployment.
        :param switchover_timeout: Maximum time in seconds for the switchover
            (default: 300, minimum: 30).
        :return: A dictionary with the Blue/Green Deployment details after switchover.
        """
        try:
            response = self.rds_client.switchover_blue_green_deployment(
                BlueGreenDeploymentIdentifier=deployment_identifier,
                SwitchoverTimeout=switchover_timeout,
            )
            deployment = response["BlueGreenDeployment"]
            logger.info(
                "Initiated switchover for Blue/Green Deployment '%s'. Status: '%s'.",
                deployment["BlueGreenDeploymentIdentifier"],
                deployment["Status"],
            )
            return deployment
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidBlueGreenDeploymentStateFault":
                logger.error(
                    "Cannot switch over Blue/Green Deployment '%s'. "
                    "Verify the green environment is in AVAILABLE status.",
                    deployment_identifier,
                )
            raise

    # snippet-end:[python.example_code.rds.SwitchoverBlueGreenDeployment]

    # snippet-start:[python.example_code.rds.DeleteBlueGreenDeployment]
    def delete_blue_green_deployment(
        self, deployment_identifier: str, delete_target: bool = False
    ) -> dict:
        """
        Deletes a Blue/Green Deployment.

        :param deployment_identifier: The identifier of the Blue/Green Deployment.
        :param delete_target: Whether to delete the green environment resources.
            Cannot be True when status is SWITCHOVER_COMPLETED.
        :return: A dictionary with the deleted Blue/Green Deployment details.
        """
        try:
            response = self.rds_client.delete_blue_green_deployment(
                BlueGreenDeploymentIdentifier=deployment_identifier,
                DeleteTarget=delete_target,
            )
            deployment = response["BlueGreenDeployment"]
            logger.info(
                "Deleting Blue/Green Deployment '%s'. Status: '%s'.",
                deployment["BlueGreenDeploymentIdentifier"],
                deployment["Status"],
            )
            return deployment
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidBlueGreenDeploymentStateFault":
                logger.error(
                    "Cannot delete Blue/Green Deployment '%s'. "
                    "Ensure the deployment is in a terminal state.",
                    deployment_identifier,
                )
            raise

    # snippet-end:[python.example_code.rds.DeleteBlueGreenDeployment]

    def delete_db_instance(
        self,
        db_instance_identifier: str,
        skip_final_snapshot: bool = True,
        delete_automated_backups: bool = True,
    ) -> dict:
        """
        Deletes an RDS DB instance.

        :param db_instance_identifier: The identifier of the DB instance to delete.
        :param skip_final_snapshot: Whether to skip the final snapshot.
        :param delete_automated_backups: Whether to delete automated backups.
        :return: A dictionary with DB instance details.
        """
        try:
            response = self.rds_client.delete_db_instance(
                DBInstanceIdentifier=db_instance_identifier,
                SkipFinalSnapshot=skip_final_snapshot,
                DeleteAutomatedBackups=delete_automated_backups,
            )
            db_instance = response["DBInstance"]
            logger.info(
                "Deleting DB instance '%s'. Status: '%s'.",
                db_instance["DBInstanceIdentifier"],
                db_instance["DBInstanceStatus"],
            )
            return db_instance
        except ClientError as err:
            if err.response["Error"]["Code"] == "DBInstanceNotFoundFault":
                logger.error(
                    "DB instance '%s' not found. It may have already been deleted.",
                    db_instance_identifier,
                )
            raise

    def wait_for_db_instance_deleted(
        self,
        db_instance_identifier: str,
        wait_seconds: int = 30,
        max_wait: int = 1200,
    ) -> None:
        """
        Polls DescribeDBInstances until the DB instance is deleted.

        :param db_instance_identifier: The identifier of the DB instance.
        :param wait_seconds: Seconds between polls.
        :param max_wait: Maximum total seconds to wait.
        """
        elapsed = 0
        while elapsed < max_wait:
            try:
                db_instance = self.describe_db_instance(db_instance_identifier)
                status = db_instance["DBInstanceStatus"]
                logger.info(
                    "DB instance '%s' status: '%s'. Waiting %d seconds...",
                    db_instance_identifier,
                    status,
                    wait_seconds,
                )
                time.sleep(wait_seconds)
                elapsed += wait_seconds
            except ClientError as err:
                if err.response["Error"]["Code"] == "DBInstanceNotFoundFault":
                    logger.info(
                        "DB instance '%s' has been deleted.", db_instance_identifier
                    )
                    return
                raise
        raise TimeoutError(
            f"DB instance '{db_instance_identifier}' was not deleted "
            f"within {max_wait} seconds."
        )
