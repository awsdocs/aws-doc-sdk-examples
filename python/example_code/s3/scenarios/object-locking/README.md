
# Amazon S3 Object Lock Workflow for the SDK for Python (boto3)

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to work with Amazon Simple Storage Service (Amazon S3) object locking features. The workflow shows how to create, update, view, and modify object locks, as well as how locked objects behave regarding requests to delete and overwrite.

[Amazon S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html) can help prevent Amazon S3 objects from being deleted or overwritten for a fixed amount of time or indefinitely. Object Lock can help meet regulatory requirements or protect against object changes or deletion.

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

### Scenarios

This example uses a workflow approach to demonstrate various aspects of S3 Object Locking. The workflow is divided into three stages:

1. **Deploy**: Create buckets with different object locking configurations, populate buckets with objects, and set object lock and retention policies.
2. **Demo**: Explore S3 locking features by listing objects, attempting to delete or overwrite locked objects, and viewing retention and legal hold settings.
3. **Clean**: Remove object locks and retention periods, delete all objects and buckets.

#### Running the workflow
To run this workflow, pull AWS tokens and run the command below:

```bash
python main.py
```


## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [boto3 Amazon S3 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---

© Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
