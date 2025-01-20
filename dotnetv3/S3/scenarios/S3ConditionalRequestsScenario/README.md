# Amazon S3 Conditional Requests Feature Scenario for the SDK for .NET

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to work with Amazon Simple Storage Service (Amazon S3) conditional request features. The scenario demonstrates how to add preconditions to S3 operations, and how those operations will succeed or fail based on the conditional requests.

[Amazon S3 Conditional Requests](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html) are used to add preconditions to S3 read, copy, or write requests.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

This example uses a feature scenario to demonstrate various aspects of S3 conditional requests. The scenario is divided into three stages:

1. **Setup**: Create test buckets and objects.
2. **Conditional Reads and Writes**: Explore S3 conditional requests by listing objects, attempting to read or write with conditional requests, and viewing request results.
3. **Clean**: Delete all objects and buckets.

### Prerequisites

For general prerequisites, see the [README](../../../README.md) in the `dotnetv3` folder.

### Resources

The scenario steps create the buckets and objects needed for the example. No additional resources are required.

### Instructions

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .sln file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

This starts an interactive scenario that walks you through exploring conditional requests for read, write, and copy operations.

## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
