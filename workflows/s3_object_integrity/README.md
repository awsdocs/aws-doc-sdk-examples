# Amazon S3 Object Integrity Workflow

## Overview

This example shows how to use AWS SDKs to check the object integrity of data uploaded to Amazon Simple Storage Service (Amazon S3) buckets.

[Amazon S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html) can help prevent Amazon S3 objects from being deleted or overwritten for a fixed amount of time or indefinitely. Object Lock can help meet regulatory requirements or protect against object changes or deletion.


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

## Additional reading

- [S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
