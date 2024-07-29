# Amazon SES v2 API code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Simple Email Service v2 API.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SES v2 API is a reliable, scalable, and cost-effective email service._

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

- [CreateContact](newsletter.py#L155)
- [CreateContactList](newsletter.py#L105)
- [CreateEmailIdentity](newsletter.py#L92)
- [CreateEmailTemplate](newsletter.py#L118)
- [DeleteContactList](newsletter.py#L258)
- [DeleteEmailIdentity](newsletter.py#L286)
- [DeleteEmailTemplate](newsletter.py#L271)
- [ListContacts](newsletter.py#L198)
- [SendEmail](newsletter.py#L164)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Newsletter workflow](newsletter.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### SESv2 Newsletter Workflow

Review the usage instructions in [`workflows/sesv2_weekly_mailer/README.md`](../../../workflows/sesv2_weekly_mailer/README.md).

To run the Newsletter example, copy the files from workflows/sesv2_weekly_mailer/resources into this folder.

<!--custom.instructions.end-->



#### Newsletter workflow

This example shows you how to run the Amazon SES v2 API newsletter workflow.


<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.start-->
<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.end-->

Start the example by running the following at a command prompt:

```
python newsletter.py
```


<!--custom.scenarios.sesv2_NewsletterWorkflow.start-->
<!--custom.scenarios.sesv2_NewsletterWorkflow.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES v2 API Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES v2 API API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [SDK for Python Amazon SES v2 API reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0