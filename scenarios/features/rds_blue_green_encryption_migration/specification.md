# Amazon RDS Blue/Green Deployment — Encryption Migration Specification

> **DRAFT** — This specification describes a scenario that demonstrates how to use Amazon RDS Blue/Green Deployments to migrate the encryption (KMS) key for an RDS DB instance with near-zero downtime. It leverages the **new `TargetResourceConfigurations` parameter** on `CreateBlueGreenDeployment`, which lets you specify a target KMS key for each resource in the green environment. This eliminates the previous multi-step snapshot-copy-restore workflow for KMS key rotation or migration.

Previously, changing the KMS encryption key on an RDS DB instance required creating a snapshot, copying the snapshot with a new KMS key, and restoring the copy — a process that incurred significant downtime. With the new `TargetResourceConfigurations` parameter, customers can now specify a different KMS key directly when creating a Blue/Green Deployment, and the green environment is automatically provisioned with the new encryption key. After switchover, the production database uses the new KMS key with minimal disruption.

This scenario walks through the full lifecycle: creating an encrypted RDS DB instance, creating a Blue/Green Deployment with a new target KMS key via `TargetResourceConfigurations`, monitoring the deployment, performing the switchover, verifying the encryption change, and cleaning up.

### Resources

- An AWS account with permissions for Amazon RDS and AWS KMS operations.
- Two AWS KMS keys (one for the original encryption, one for the target encryption in the green environment). These can be AWS managed keys or customer managed keys.
- A VPC with at least two subnets in different Availability Zones for the DB subnet group.

### Relevant documentation

- [Using Amazon RDS Blue/Green Deployments for database updates](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/blue-green-deployments.html)
- [Creating a Blue/Green Deployment](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/blue-green-deployments-creating.html)
- [Switching a Blue/Green Deployment](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/blue-green-deployments-switching.html)
- [Deleting a Blue/Green Deployment](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/blue-green-deployments-deleting.html)
- [Encrypting Amazon RDS resources](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Overview.Encryption.html)
- [Options for changing AWS KMS encryption key for Amazon RDS databases](https://aws.amazon.com/blogs/database/options-for-changing-aws-kms-encryption-key-for-amazon-rds-databases/)
- [CreateBlueGreenDeployment API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_CreateBlueGreenDeployment.html)
- [DescribeBlueGreenDeployments API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DescribeBlueGreenDeployments.html)
- [SwitchoverBlueGreenDeployment API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_SwitchoverBlueGreenDeployment.html)
- [DeleteBlueGreenDeployment API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DeleteBlueGreenDeployment.html)
- [CreateDBInstance API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_CreateDBInstance.html)
- [DescribeDBInstances API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DescribeDBInstances.html)
- [DeleteDBInstance API Reference](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DeleteDBInstance.html)

### API Actions Used

- [CreateDBInstance](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_CreateDBInstance.html) — Creates a new encrypted RDS DB instance (the blue environment source).
- [DescribeDBInstances](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DescribeDBInstances.html) — Checks the status and encryption details of DB instances.
- [CreateBlueGreenDeployment](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_CreateBlueGreenDeployment.html) — Creates a Blue/Green Deployment with the new `TargetResourceConfigurations` parameter to specify the target KMS key.
- [DescribeBlueGreenDeployments](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DescribeBlueGreenDeployments.html) — Monitors the status of the Blue/Green Deployment.
- [SwitchoverBlueGreenDeployment](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_SwitchoverBlueGreenDeployment.html) — Switches production traffic from the blue environment to the green environment.
- [DeleteBlueGreenDeployment](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DeleteBlueGreenDeployment.html) — Deletes the Blue/Green Deployment after switchover.
- [DeleteDBInstance](https://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_DeleteDBInstance.html) — Cleans up the old blue DB instance and any remaining resources.

## Hello Amazon RDS

A simple introductory call to verify connectivity with the Amazon RDS service.

- Call `DescribeDBInstances` with no parameters (or with a non-existent identifier to test error handling).
- The successful response returns a list of DB instances in the account and Region, confirming that the RDS client is configured correctly.
- If no DB instances exist, the response contains an empty list.

**Parameters:**
- None required for a basic hello call.

**Response:**
- A list of `DBInstance` objects, each containing: `DBInstanceIdentifier`, `DBInstanceStatus`, `Engine`, `EngineVersion`, `StorageEncrypted`, `KmsKeyId`, `Endpoint`, and other metadata.

## Scenario

This scenario demonstrates the complete lifecycle of migrating the KMS encryption key for an Amazon RDS DB instance using Blue/Green Deployments with the new `TargetResourceConfigurations` parameter.

### Setup

1. **Prerequisites:** Ensure two KMS keys are available — an "original" key for the source DB instance and a "target" key that will be used in the green environment. Record both KMS key ARNs.
2. **Create an encrypted RDS DB instance** to serve as the blue (production) environment source.

### Step 1: Create an encrypted source DB instance

Call `CreateDBInstance` to provision an encrypted RDS DB instance that will serve as the blue (source) environment.

**Key parameters:**
- `DBInstanceIdentifier` (string, required) — A unique name for the DB instance (e.g., `"my-encrypted-db"`).
- `DBInstanceClass` (string, required) — The compute and memory capacity (e.g., `"db.t3.micro"`).
- `Engine` (string, required) — The database engine (e.g., `"mysql"`, `"postgres"`).
- `MasterUsername` (string, required) — The master user name for the DB instance.
- `MasterUserPassword` (string, required) — The master user password.
- `AllocatedStorage` (integer, required for non-Aurora) — The size of the database in GiB (e.g., `20`).
- `StorageEncrypted` (boolean) — Set to `true` to enable encryption at rest.
- `KmsKeyId` (string) — The ARN of the original KMS key for encryption. If not specified, the AWS managed key for RDS is used.
- `BackupRetentionPeriod` (integer) — Set to at least `1` to enable automated backups (required for Blue/Green Deployments).

**Response:**
- Returns a `DBInstance` object with status `"creating"`.

**Wait:** Poll `DescribeDBInstances` until the DB instance status transitions to `"available"`. This typically takes 5–15 minutes.

### Step 2: Verify the source DB instance encryption

Call `DescribeDBInstances` with the `DBInstanceIdentifier` of the source instance.

**Key parameters:**
- `DBInstanceIdentifier` (string) — The identifier of the source DB instance.

**Response fields to verify:**
- `StorageEncrypted` — Should be `true`.
- `KmsKeyId` — Should match the original KMS key ARN.
- `DBInstanceStatus` — Should be `"available"`.

Record the DB instance ARN from the response; this is needed as the `Source` parameter for the Blue/Green Deployment.

### Step 3: Create a Blue/Green Deployment with encryption migration

Call `CreateBlueGreenDeployment` to create a Blue/Green Deployment. Use the new `TargetResourceConfigurations` parameter to specify a different KMS key for the green environment, enabling encryption key migration as part of the deployment.

**Key parameters:**
- `BlueGreenDeploymentName` (string, required) — A unique name for the deployment (e.g., `"my-kms-migration"`). Maximum 60 characters, alphanumeric pattern.
- `Source` (string, required) — The ARN of the source (blue) DB instance or DB cluster.
- `TargetResourceConfigurations` (structure, optional — **NEW**) — Specifies target resource configurations for the green environment. Use this to set a different KMS key for each resource in the green environment. Provide the ARN of the target KMS key that the green environment DB instance should use for encryption.
- `Tags` (array of Tag objects, optional) — Tags to assign to the deployment.

> **Note:** The `TargetResourceConfigurations` parameter is the new feature described in the release notes. It allows specifying a target KMS key for each resource in the green environment directly during deployment creation, eliminating the need for the traditional snapshot-copy-restore workflow to change encryption keys.

**Response:**
- Returns a `BlueGreenDeployment` object with:
  - `BlueGreenDeploymentIdentifier` — The system-generated identifier (e.g., `"bgd-abcdef1234567890"`).
  - `Status` — Initially `"PROVISIONING"`.
  - `Source` — The ARN of the blue (source) DB instance.
  - `SwitchoverDetails` — Details about source and target resource mappings.
  - `Tasks` — A list of tasks such as `CREATING_READ_REPLICA_OF_SOURCE` and `CONFIGURE_BACKUPS`.

Record the `BlueGreenDeploymentIdentifier` for subsequent operations.

### Step 4: Monitor the Blue/Green Deployment

Poll `DescribeBlueGreenDeployments` to monitor the deployment until it reaches `"AVAILABLE"` status.

**Key parameters:**
- `BlueGreenDeploymentIdentifier` (string) — The identifier returned in Step 3.

**Response fields to monitor:**
- `Status` — Valid values during provisioning: `"PROVISIONING"` → `"AVAILABLE"`. If `"INVALID_CONFIGURATION"`, investigate and resolve the issue.
- `Tasks` — Each task has a `Name` and `Status` (`"PENDING"`, `"IN_PROGRESS"`, `"COMPLETED"`, `"FAILED"`).
- `SwitchoverDetails` — Contains `SourceMember`, `TargetMember`, and `Status` for each resource pair.
- `Target` — The ARN of the green environment DB instance.

**Polling pattern:** Check every 30–60 seconds. The green environment typically takes 10–20 minutes to provision, depending on database size. Wait until `Status` is `"AVAILABLE"` before proceeding.

### Step 5: Verify green environment encryption

Once the deployment is `"AVAILABLE"`, call `DescribeDBInstances` with the green environment DB instance identifier (extracted from the `Target` ARN in the `DescribeBlueGreenDeployments` response).

**Verify:**
- `StorageEncrypted` is `true`.
- `KmsKeyId` matches the target KMS key ARN (the new key specified in `TargetResourceConfigurations`).
- `DBInstanceStatus` is `"available"`.

This confirms the green environment was provisioned with the new encryption key.

### Step 6: Switchover to the green environment

Call `SwitchoverBlueGreenDeployment` to promote the green environment to production.

**Key parameters:**
- `BlueGreenDeploymentIdentifier` (string, required) — The deployment identifier from Step 3.
- `SwitchoverTimeout` (integer, optional) — The maximum time in seconds for the switchover. Default: `300`. Minimum: `30`. If the switchover exceeds this duration, it is rolled back automatically.

**Behavior during switchover:**
- Writes are blocked on both the blue and green environments until synchronization is complete.
- Guardrails check instance health, replication status, and long-running transactions.
- The green environment's endpoints are renamed to match the blue environment's endpoints, so no application configuration changes are needed.

**Response:**
- Returns a `BlueGreenDeployment` object with `Status` set to `"SWITCHOVER_IN_PROGRESS"`.

**Wait:** Poll `DescribeBlueGreenDeployments` until `Status` transitions to `"SWITCHOVER_COMPLETED"`. Switchover typically completes in under a minute.

### Step 7: Verify post-switchover encryption

After switchover, the green environment is now the production database. Call `DescribeDBInstances` with the original DB instance identifier (which now points to the former green instance).

**Verify:**
- `KmsKeyId` matches the target KMS key ARN — confirming the production database now uses the new encryption key.
- `StorageEncrypted` is `true`.
- `DBInstanceStatus` is `"available"`.
- The `Endpoint` address and port are unchanged (the switchover renames endpoints automatically).

### Cleanup

1. **Delete the Blue/Green Deployment:** Call `DeleteBlueGreenDeployment` with:
   - `BlueGreenDeploymentIdentifier` (string, required) — The deployment identifier.
   - `DeleteTarget` (boolean, optional) — Set to `false` since the switchover is completed (cannot be `true` when status is `"SWITCHOVER_COMPLETED"`). The old blue instance is retained for rollback validation.
   
   **Response:** Returns a `BlueGreenDeployment` object with `Status` set to `"DELETING"`.

2. **Delete the old blue DB instance:** After confirming the production database operates correctly with the new KMS key, call `DeleteDBInstance` to remove the old blue DB instance (which was renamed during switchover):
   - `DBInstanceIdentifier` (string, required) — The identifier of the old blue instance (check `SwitchoverDetails` for the renamed identifier).
   - `SkipFinalSnapshot` (boolean) — Set to `true` for cleanup, or `false` to create a final snapshot.
   - `DeleteAutomatedBackups` (boolean) — Set to `true` to remove automated backups.

3. **Clean up the original source DB instance** (created in Step 1) if it was only for testing purposes: Call `DeleteDBInstance` with `SkipFinalSnapshot` set to `true`.

### Outcome

After completing this scenario, you will have:

1. **Created** an encrypted RDS DB instance using an original KMS key.
2. **Created** a Blue/Green Deployment with the new `TargetResourceConfigurations` parameter to specify a different target KMS key for the green environment.
3. **Monitored** the deployment provisioning until the green environment was available with the new encryption key.
4. **Verified** the green environment used the target KMS key.
5. **Switched over** production traffic from the blue to the green environment with near-zero downtime.
6. **Confirmed** the production database now uses the new KMS encryption key.
7. **Cleaned up** the Blue/Green Deployment and old resources.

This demonstrates how the new `TargetResourceConfigurations` parameter simplifies KMS key migration by eliminating the need for the traditional snapshot-copy-restore workflow, providing a safer, simpler, and faster approach to encryption key changes with minimal downtime.

## Errors

| Action | Exception | Handling |
|--------|-----------|----------|
| CreateDBInstance | DBInstanceAlreadyExistsFault | The specified DB instance identifier already exists. Choose a different identifier or delete the existing instance before retrying. |
| DescribeDBInstances | DBInstanceNotFoundFault | The specified DB instance identifier does not refer to an existing instance. Verify the identifier is correct and the instance has been created. |
| CreateBlueGreenDeployment | SourceDatabaseNotSupportedFault | The source DB instance is not supported for Blue/Green Deployments. Ensure automated backups are enabled and the engine version is supported. Check that the instance is not a read replica or part of an unsupported configuration. |
| DescribeBlueGreenDeployments | BlueGreenDeploymentNotFoundFault | The specified Blue/Green Deployment identifier does not exist. Verify the identifier returned from CreateBlueGreenDeployment is used correctly. |
| SwitchoverBlueGreenDeployment | InvalidBlueGreenDeploymentStateFault | The deployment cannot be switched over because of an invalid configuration in the green environment. Verify the green environment is in AVAILABLE status, check for replication lag, and ensure there are no blocking issues before retrying. |
| DeleteBlueGreenDeployment | InvalidBlueGreenDeploymentStateFault | The deployment cannot be deleted due to an invalid state. Ensure the deployment is in a terminal state (AVAILABLE or SWITCHOVER_COMPLETED) before attempting deletion. |
| DeleteDBInstance | DBInstanceNotFoundFault | The specified DB instance does not exist. It may have already been deleted. Verify the instance identifier. |

## Metadata

| Action/Scenario | Metadata File | Metadata Key |
|----------------|---------------|--------------|
| Amazon RDS Hello | rds_metadata.yaml | rds_Hello |
| Amazon RDS Blue/Green Encryption Migration Scenario | rds_metadata.yaml | rds_BlueGreenEncryptionMigration |
| CreateDBInstance | rds_metadata.yaml | rds_CreateDBInstance |
| DescribeDBInstances | rds_metadata.yaml | rds_DescribeDBInstances |
| CreateBlueGreenDeployment | rds_metadata.yaml | rds_CreateBlueGreenDeployment |
| DescribeBlueGreenDeployments | rds_metadata.yaml | rds_DescribeBlueGreenDeployments |
| SwitchoverBlueGreenDeployment | rds_metadata.yaml | rds_SwitchoverBlueGreenDeployment |
| DeleteBlueGreenDeployment | rds_metadata.yaml | rds_DeleteBlueGreenDeployment |
| DeleteDBInstance | rds_metadata.yaml | rds_DeleteDBInstance |
