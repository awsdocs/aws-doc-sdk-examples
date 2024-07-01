# Amazon S3 Object Lock Workflow

## Overview

This example shows how to use AWS SDKs to work with Amazon Simple Storage Service (Amazon S3) object locking features. The workflow demonstrates how to create, update, view, and modify object locks, as well as how locked objects behave regarding requests to delete and overwrite.

[Amazon S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html) can help prevent Amazon S3 objects from being deleted or overwritten for a fixed amount of time or indefinitely. Object Lock can help meet regulatory requirements or protect against object changes or deletion.

![Object Lock Features](resources/Diagram_Amazon-S3-Object-Lock.png)

This workflow demonstrates the following steps and tasks:
1. Add object lock settings to both new and existing S3 buckets.
   1. Add objects to buckets with optional object lock or retention period settings.
2. Attempt to delete or overwrite locked objects.
3. Retrieve and view the object lock and retention period settings of buckets and objects.
4. Delete the objects and buckets.
   1. Remove any object locks and use the BypassGovernanceRetention setting.

### Resources

The workflow scenario steps create the buckets and objects needed for the example. No additional resources are required.

## Implementations

This example is implemented in the following languages:

- [.NET](../../dotnetv3/S3/scenarios/S3ObjectLockScenario/README.md)
- [Go (v2)](../../gov2/workflows/s3_object_lock/README.md)
- [JavaScript (v3)](../../javascriptv3/example_code/s3/scenarios/object-locking/README.md)

## Additional reading

- [S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
