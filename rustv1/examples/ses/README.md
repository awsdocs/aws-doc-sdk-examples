# Amazon SES v2 API code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Simple Email Service v2 API.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateContact](src/bin/create-contact.rs#L30)
- [CreateContactList](src/bin/create-contact-list.rs#L26)
- [CreateEmailIdentity](src/newsletter.rs#L57)
- [CreateEmailTemplate](src/newsletter.rs#L100)
- [DeleteContactList](src/newsletter.rs#L347)
- [DeleteEmailIdentity](src/newsletter.rs#L385)
- [DeleteEmailTemplate](src/newsletter.rs#L360)
- [GetEmailIdentity](src/bin/is-email-verified.rs#L26)
- [ListContactLists](src/bin/list-contact-lists.rs#L22)
- [ListContacts](src/bin/list-contacts.rs#L26)
- [SendEmail](src/bin/send-email.rs#L39)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Newsletter scenario](src/newsletter.rs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### SESv2 Newsletter Workflow

Review the usage instructions in [`workflows/sesv2_weekly_mailer/README.md`](../../../scenarios/features/sesv2_weekly_mailer/README.md).

To run the Newsletter example, copy the files from workflows/sesv2_weekly_mailer/resources into a new folder, rustv1/examples/ses/resources/newsletter.

<!--custom.instructions.end-->



#### Newsletter scenario

This example shows you how to run the Amazon SES v2 API newsletter scenario.


<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.start-->
<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.end-->


<!--custom.scenarios.sesv2_NewsletterWorkflow.start-->
<!--custom.scenarios.sesv2_NewsletterWorkflow.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES v2 API Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES v2 API API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [SDK for Rust Amazon SES v2 API reference](https://docs.rs/aws-sdk-ses/latest/aws_sdk_ses/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
