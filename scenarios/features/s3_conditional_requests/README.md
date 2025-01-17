# Amazon S3 Conditional Requests Feature Scenario

## Overview

This example shows how to use AWS SDKs to work with Amazon Simple Storage Service (Amazon S3) conditional request features. The scenario demonstrates how to add preconditions to S3 operations, and how those operations will succeed or fail based on the conditional requests.

[Amazon S3 Conditional Requests](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html) are used to add preconditions to S3 read, copy, or write requests.

This scenario demonstrates the following steps and tasks:
1. Create test buckets and objects.
2. Use preconditions with S3 read and copy operations.
3. Use preconditions with S3 write operations to prevent overwrites.
4. Delete the objects and buckets.

### Resources

The scenario steps create the buckets and objects needed for the example. No additional resources are required.

## Implementations

This example is implemented in the following languages:

- [Python](../../../python/example_code/s3/scenarios/conditional_requests/README.md)
- [.NET](../../../dotnetv3/S3/scenarios/S3ConditionalRequestsScenario/README.md)

## Additional reading

- [S3 Conditional Reads](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-reads.html)
- [S3 Conditional Writes](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-writes.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
