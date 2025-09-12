# Amazon S3 Batch for the SDK for Python (boto3)

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to work with Amazon Simple Storage Service (Amazon S3) Batch Scenario. The scenario covers various operations such as creating an AWS Batch compute environment, creating a job queue, creating a job defination, and submitting a job, and so on.

Here are the top six service operations this scenario covers.

1. **Create an AWS Batch computer environment**: Creates an AWS Batch computer environment.

2. **Sets up a job queue**: Creates a job queue that will manage the submission of jobs.

3. **Creates a job definition**: Creates a job definition that specifies how the jobs should be executed.

4. **Registers a Job Definition**: Registers a job definition making it available for job submissions.

5. **Submits a Batch Job**: Submits a job.

6. **Checks the status of the job**: Checks the status of the job.

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

#### Running the workflow

To run this workflow, pull AWS tokens and run the command below:

```bash
python s3_batch_scenario.py
```

## Additional resources

- [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/batch-ops-create-job.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [boto3 Amazon S3 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/s3.html)

---

© Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
