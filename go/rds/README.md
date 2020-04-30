# AWS SDK for Go Code Examples for Amazon RDS

## Purpose

These examples demonstrates how to perform several Amazon RDS operations.

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the code

### CopySnapshotToS3/CopySnapshotToS3.go

This example copies a snapshot of an Amazon RDS cluster to an Amazon S3 bucket.

`go run CopySnapshot.go -a ROLE-ARN -k KMS-KEY -b BUCKET-NAME -s SNAPSHOT-NAME -e EXPORT-NAME`

- _ROLE-ARN_ is the ARN of a role that has permission to write to the bucket.
- _KMS-KEY is the KMS key used to encrypt the snapshot in the bucket.
- _BUCKET-NAME is the name of the bucket.
- _SNAPSHOT-NAME is the name of the snapshot.
- _EXPORT-NAME_ is name given to the snapshot in the bucket.

The unit test mocks the service client and the `DescribeDBSnapshots` and `StartExportTask` functions.

### CreateClusterSnapshot/CreateClusterSnapshot.go

This example creates a snapshot of an Amazon RDS cluster.

`go run CreateClusterSnapshot.go -c CLUSTER-ID`

- _CLUSTER-ID_ is the ID of a cluster.

The unit test mocks the service client and the `CreateClusterSnapshot` function.

### CreateInstanceSnapshot/CreateInstanceSnapshot.go

This example creates a snapshop of an Amazon RDS instance.

`go run CreateInstanceSnapshot.go -i INSTANCE`

- _INSTANCE_ is the name of the instance.

The unit test mocks the service client and the `CreateDBSnapshot` function.

### ListClusterSnapshots/ListClusterSnapshots.go

This example lists your Amazon RDS cluste snapshots.

`go run ListClusterSnapshots.go`

### ListInstances/ListInstances.go

This example lists your Amazon RDS instances.

`go run ListInstances.go`

### ListInstanceSnapshots/ListInstanceSnapshots.go

This example lists your Amazon RDS instance snapshots.

`go run ListInstanceSnapshots.go`

### ListParameterGroups/ListParameterGroups.go

This example lists your Amazon RDS parameter groups.

`go run ListParameterGroups.go`

### ListSecurityGroups/ListSecurityGroups.go

This example lists your Amazon RDS security groups.

`go run ListSecurityGroups.go`

### ListSubnetGroups/ListSubnetGroups.go

This example lists your Amazon RDS subnet groups.

`go run ListSubnetGroups.go`

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running a unit test

Unit tests should delete any resources they create.
However, they might result in charges to your
AWS account.

To run a unit test, enter the following:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```sh
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter the following.

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
