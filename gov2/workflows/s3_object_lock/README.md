# Amazon S3 Object Lock Workflow

## Overview

This example shows how to use AWS SDKs to work with Amazon Simple Storage Service (Amazon S3) object locking features. The workflow demonstrates how to create, update, view, and modify object locks, as well as how locked objects behave regarding requests to delete and overwrite.

[Amazon S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html) can help prevent Amazon S3 objects from being deleted or overwritten for a fixed amount of time or indefinitely. Object Lock can help meet regulatory requirements or protect against object changes or deletion.

![Object Lock Features](../../../workflows/s3_object_lock/resources/Diagram_Amazon-S3-Object-Lock.png)

This workflow demonstrates the following steps and tasks:
1. Add object lock settings to both new and existing S3 buckets.
   1. Add objects to buckets with optional object lock or retention period settings.
2. Attempt to delete or overwrite locked objects.
3. Retrieve and view the object lock and retention period settings of buckets and objects.
4. Delete the objects and buckets.
   1. Remove any object locks and use the BypassGovernanceRetention setting.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

For general prerequisites, see the [README](../../README.md) in the `gov2` folder.

### Resources

The workflow scenario steps create the buckets and objects needed for the example. No additional resources are required.

This workflow includes an optional step to add a governance mode retention period of one day to objects in an S3 bucket. In order to delete these objects, you must have the `s3:BypassGovernanceRetention` permission. If you do not have this permission, you will be unable to delete these objects until the retention period has expired.

### Instructions

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the go.mod file and run the following command:

```
go run ./cmd
```

This starts an interactive scenario that walks you through creating, exploring, and deleting S3 buckets and objects with various object lock settings.

## Additional resources

- [S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
