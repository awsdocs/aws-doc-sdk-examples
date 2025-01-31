# Amazon S3 Conditional Requests Feature Scenario for the SDK for JavaScript (v3)

## Overview

This example demonstrates how to use the AWS SDK for JavaScript (v3) to work with Amazon Simple Storage Service (Amazon S3) conditional request features. The scenario demonstrates how to add preconditions to S3 operations, and how those operations will succeed or fail based on the conditional requests.

[Amazon S3 Conditional Requests](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html) are used to add preconditions to S3 read, copy, or write requests.

## ⚠ Important

- Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Prerequisites

For prerequisites, see the [README](../../../../README.md#prerequisites) in the `javascriptv3` folder.

### Scenarios

This example uses a feature scenario to demonstrate various aspects of S3 conditional requests. The scenario is divided into three stages:

1. **Deploy**: Create test buckets and objects.
2. **Demo**: Explore S3 conditional requests by listing objects, attempting to read or write with conditional requests, and viewing request results.
3. **Clean**: Delete all objects and buckets.

#### Deploy Stage

```bash
node index.js -s deploy
```

#### Demo Stage

```bash
node index.js -s demo
```

#### Clean Stage

```bash
node index.js -s clean
```

## Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../../../README.md#tests) in the `javascriptv3` folder.

## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for JavaScript (v3) Amazon S3 reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-s3/index.html)

---

Copyright Amazon.com, Inc. or its cd ..affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0