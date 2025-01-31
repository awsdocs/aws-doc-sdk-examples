# S3 Glacier code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon S3 Glacier.

<!--custom.overview.start-->
<!--custom.overview.end-->

_S3 Glacier provides durable and extremely low-cost storage for infrequently used data with security features for data archiving and backup._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateVault](glacier_basics.py#L35)
- [DeleteArchive](glacier_basics.py#L196)
- [DeleteVault](glacier_basics.py#L157)
- [DeleteVaultNotifications](glacier_basics.py#L330)
- [DescribeJob](glacier_basics.py#L215)
- [GetJobOutput](glacier_basics.py#L240)
- [GetVaultNotifications](glacier_basics.py#L305)
- [InitiateJob](glacier_basics.py#L99)
- [ListJobs](glacier_basics.py#L121)
- [ListVaults](glacier_basics.py#L54)
- [SetVaultNotifications](glacier_basics.py#L266)
- [UploadArchive](glacier_basics.py#L68)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Archive a file, get notifications, and initiate a job](glacier_basics.py)
- [Get archive content and delete the archive](glacier_basics.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Archive a file, get notifications, and initiate a job

This example shows you how to do the following:

- Create an Amazon S3 Glacier vault.
- Configure the vault to publish notifications to an Amazon SNS topic.
- Upload an archive file to the vault.
- Initiate an archive retrieval job.

<!--custom.scenario_prereqs.glacier_Usage_UploadNotifyInitiate.start-->
<!--custom.scenario_prereqs.glacier_Usage_UploadNotifyInitiate.end-->

Start the example by running the following at a command prompt:

```
python glacier_basics.py
```


<!--custom.scenarios.glacier_Usage_UploadNotifyInitiate.start-->
<!--custom.scenarios.glacier_Usage_UploadNotifyInitiate.end-->

#### Get archive content and delete the archive

This example shows you how to do the following:

- List jobs for an Amazon S3 Glacier vault and get job status.
- Get the output of a completed archive retrieval job.
- Delete an archive.
- Delete a vault.

<!--custom.scenario_prereqs.glacier_Usage_RetrieveDelete.start-->
<!--custom.scenario_prereqs.glacier_Usage_RetrieveDelete.end-->

Start the example by running the following at a command prompt:

```
python glacier_basics.py
```


<!--custom.scenarios.glacier_Usage_RetrieveDelete.start-->
Because Amazon S3 Glacier is designed for infrequent retrieval, a typical retrieval
job takes 3–5 hours to complete.
<!--custom.scenarios.glacier_Usage_RetrieveDelete.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [S3 Glacier Developer Guide](https://docs.aws.amazon.com/amazonglacier/latest/dev/introduction.html)
- [S3 Glacier API Reference](https://docs.aws.amazon.com/amazonglacier/latest/dev/amazon-glacier-api.html)
- [SDK for Python S3 Glacier reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/glacier.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0