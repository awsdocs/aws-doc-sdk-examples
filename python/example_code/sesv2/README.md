# Amazon SES v2 API code examples for the SDK for Python (Boto3)

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

### Get started

- [Hello Amazon SES v2 API](attachments_scenario/sesv2_hello.py#L18) (`ListEmailIdentities`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateConfigurationSet](search_filter_scenario/sesv2_wrapper.py#L199)
- [CreateContact](newsletter_scenario/newsletter.py#L155)
- [CreateContactList](newsletter_scenario/newsletter.py#L105)
- [CreateEmailIdentity](attachments_scenario/sesv2_wrapper.py#L73)
- [CreateEmailTemplate](attachments_scenario/sesv2_wrapper.py#L109)
- [CreateTenant](search_filter_scenario/sesv2_wrapper.py#L260)
- [DeleteConfigurationSet](search_filter_scenario/sesv2_wrapper.py#L290)
- [DeleteContactList](newsletter_scenario/newsletter.py#L258)
- [DeleteEmailIdentity](attachments_scenario/sesv2_wrapper.py#L321)
- [DeleteEmailTemplate](attachments_scenario/sesv2_wrapper.py#L291)
- [DeleteTenant](search_filter_scenario/sesv2_wrapper.py#L342)
- [GetEmailIdentity](attachments_scenario/sesv2_wrapper.py#L42)
- [ListConfigurationSets](search_filter_scenario/sesv2_wrapper.py#L44)
- [ListContacts](newsletter_scenario/newsletter.py#L198)
- [ListEmailIdentities](search_filter_scenario/sesv2_wrapper.py#L95)
- [ListTenants](search_filter_scenario/sesv2_wrapper.py#L146)
- [SendBulkEmail](attachments_scenario/sesv2_wrapper.py#L227)
- [SendEmail](attachments_scenario/sesv2_wrapper.py#L155)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Email Attachments Scenario](attachments_scenario/scenario_sesv2_email_attachments.py)
- [Newsletter scenario](newsletter_scenario/newsletter.py)
- [Search and filter SES resources](search_filter_scenario/scenario_sesv2_search_filter_basics.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

#### SESv2 Newsletter Workflow

Review the usage instructions in [`workflows/sesv2_weekly_mailer/README.md`](../../../scenarios/features/sesv2_weekly_mailer/README.md).

To run the Newsletter example, copy the files from workflows/sesv2_weekly_mailer/resources into this folder.

<!--custom.instructions.end-->

#### Hello Amazon SES v2 API

This example shows you how to get started using Amazon SES v2 API.

```
python attachments_scenario/sesv2_hello.py
```


#### Email Attachments Scenario

This example shows you how to send emails with attachments using Amazon SES v2 API.

- Create an email template for bulk sends.
- Send a simple email with a file attachment.
- Send a simple email with an inline image.
- Send bulk templated emails with attachments.
- Clean up resources.

<!--custom.scenario_prereqs.sesv2_Scenario_EmailAttachments.start-->
<!--custom.scenario_prereqs.sesv2_Scenario_EmailAttachments.end-->

Start the example by running the following at a command prompt:

```
python attachments_scenario/scenario_sesv2_email_attachments.py
```


<!--custom.scenarios.sesv2_Scenario_EmailAttachments.start-->
<!--custom.scenarios.sesv2_Scenario_EmailAttachments.end-->

#### Newsletter scenario

This example shows you how to run the Amazon SES v2 API newsletter scenario.


<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.start-->
<!--custom.scenario_prereqs.sesv2_NewsletterWorkflow.end-->

Start the example by running the following at a command prompt:

```
python newsletter_scenario/newsletter.py
```


<!--custom.scenarios.sesv2_NewsletterWorkflow.start-->
<!--custom.scenarios.sesv2_NewsletterWorkflow.end-->

#### Search and filter SES resources

This example shows you how to do the following:

- Create multiple configuration sets, email identities, and tenants.
- List all configuration sets (unfiltered).
- Filter configuration sets by name substring.
- List all email identities (unfiltered).
- Filter email identities by name substring.
- List all tenants (unfiltered).
- Filter tenants by name substring.
- Demonstrate pagination with filters.
- Clean up all created resources.

<!--custom.scenario_prereqs.sesv2_SearchFilterBasics.start-->
<!--custom.scenario_prereqs.sesv2_SearchFilterBasics.end-->

Start the example by running the following at a command prompt:

```
python search_filter_scenario/scenario_sesv2_search_filter_basics.py
```


<!--custom.scenarios.sesv2_SearchFilterBasics.start-->
<!--custom.scenarios.sesv2_SearchFilterBasics.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SES v2 API Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES v2 API API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [SDK for Python (Boto3) Amazon SES v2 API reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
