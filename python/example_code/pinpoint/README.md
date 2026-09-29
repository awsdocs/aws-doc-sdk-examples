# Amazon Pinpoint code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Pinpoint.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Pinpoint helps you engage your customers by sending them email, SMS and voice messages, and push notifications._

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

- [Hello Amazon Pinpoint](pinpoint_hello.py#L20) (`GetApps`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn Amazon Pinpoint basics](scenarios/pinpoint_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateApp](pinpoint_wrapper.py#L59)
- [CreateCampaign](pinpoint_wrapper.py#L175)
- [CreateSegment](pinpoint_wrapper.py#L133)
- [DeleteApp](pinpoint_wrapper.py#L429)
- [DeleteCampaign](pinpoint_wrapper.py#L371)
- [DeleteSegment](pinpoint_wrapper.py#L400)
- [GetCampaign](pinpoint_wrapper.py#L304)
- [GetCampaignActivities](pinpoint_wrapper.py#L337)
- [SendMessages](pinpoint_send_email_message_api.py#L11)
- [UpdateEmailChannel](pinpoint_wrapper.py#L89)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
Each file can be run separately at a command prompt. For example, send an email message
by running the following at a command prompt:

```
python pinpoint_send_email_message_api.py
```  
<!--custom.instructions.end-->

#### Hello Amazon Pinpoint

This example shows you how to get started using Amazon Pinpoint.

```
python pinpoint_hello.py
```

#### Learn Amazon Pinpoint basics

This example shows you how to learn Amazon Pinpoint basics.

- Create an Amazon Pinpoint application.
- Configure the email channel with a verified SES identity.
- Create a segment targeting email subscribers.
- Create and launch an email campaign.
- Send a direct transactional email via SendMessages.
- Retrieve campaign details and activities.
- Clean up all resources.

<!--custom.basic_prereqs.pinpoint_Scenario.start-->
<!--custom.basic_prereqs.pinpoint_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/pinpoint_basics_scenario.py
```


<!--custom.basics.pinpoint_Scenario.start-->
<!--custom.basics.pinpoint_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Pinpoint Developer Guide](https://docs.aws.amazon.com/pinpoint/latest/developerguide/welcome.html)
- [Amazon Pinpoint API Reference](https://docs.aws.amazon.com/pinpoint/latest/apireference/welcome.html)
- [SDK for Python (Boto3) Amazon Pinpoint reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/pinpoint.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
