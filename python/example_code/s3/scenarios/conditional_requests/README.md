
# Amazon S3 Conditional Requests Feature Scenario for the SDK for Python (boto3)

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to work with Amazon Simple Storage Service (Amazon S3) conditional request features. The scenario demonstrates how to add preconditions to S3 operations, and how those operations will succeed or fail based on the conditional requests.

[Amazon S3 Conditional Requests](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html) are used to add preconditions to S3 read, copy, or write requests.

## ⚠ Important

- Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Prerequisites

To run these examples, you need:

- Python 3.x installed.
- Run `python pip install -r requirements.txt`
- AWS credentials configured. For more information, see [Configuring the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html).

### Scenario

This example uses a feature scenario to demonstrate various aspects of S3 conditional requests. The scenario is divided into three stages:

1. **Setup**: Create test buckets and objects.
2. **Conditional Reads and Writes**: Explore S3 conditional requests by listing objects, attempting to read or write with conditional requests, and viewing request results.
3. **Clean**: Delete all objects and buckets.

#### Running the scenario
To run this feature scenario, run the command below from this directory:

```
python scenario.py
```


## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-requests.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [boto3 Amazon S3 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---

© Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
