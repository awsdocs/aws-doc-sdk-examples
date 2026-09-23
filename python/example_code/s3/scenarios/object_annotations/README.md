
# Amazon S3 Object Annotations Feature Scenario for the SDK for Python (boto3)

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to work with the Amazon Simple Storage Service (Amazon S3) object annotations feature. The scenario walks through the full lifecycle of annotations: attaching named payloads to an object, retrieving and listing them (with prefix filtering), updating an annotation, and deleting annotations.

[Amazon S3 Object Annotations](https://docs.aws.amazon.com/AmazonS3/latest/userguide/annotations-overview.html) are named payloads of 1 byte to 1 MiB that you can attach to an S3 object without modifying the object itself. Each object can have up to 1,000 annotations.

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

This example uses a feature scenario to demonstrate the S3 object annotations lifecycle. The scenario is divided into these stages:

1. **Setup**: Create a bucket and upload a test object.
2. **Attach**: Attach several annotations to the object, using a period (`.`) as a namespace delimiter (for example, `ml.sentiment-analysis`).
3. **Retrieve and list**: Retrieve a specific annotation, list all annotations, and list annotations filtered by a name prefix.
4. **Update**: Overwrite an existing annotation and verify the change.
5. **Delete**: Delete annotations, confirm removal by handling the `NoSuchAnnotation` error, and verify the object has no remaining annotations.
6. **Clean up**: Delete the object and the bucket.

> **Note:** Annotation names must be 1-512 bytes and may contain only Unicode letters, digits, underscores (`_`), periods (`.`), and hyphens (`-`). They cannot start with `aws` or `s3`, and cannot contain a forward slash (`/`).

#### Running the scenario
To run this feature scenario, run the command below from this directory:

```
python scenario_object_annotations.py
```

## Tests

⚠ Running the tests might result in charges to your AWS account.

To run the integration test for this scenario, run the following command from this directory:

```
python -m pytest -m integ
```

## Additional resources

- [Amazon S3 Object Annotations overview](https://docs.aws.amazon.com/AmazonS3/latest/userguide/annotations-overview.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [boto3 Amazon S3 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---

© Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
