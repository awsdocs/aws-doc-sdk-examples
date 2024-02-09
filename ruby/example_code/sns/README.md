# Amazon SNS code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon Simple Notification Service (Amazon SNS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a topic](create_topic.rb#L3) (`CreateTopic`)
- [List the subscribers of a topic](None) (`ListSubscriptions`)
- [List topics](show_topics.rb#L11) (`ListTopics`)
- [Publish to a topic](send_message.rb#L4) (`Publish`)
- [Set topic attributes](enable_resource.rb#L4) (`SetTopicAttributes`)
- [Subscribe an email address to a topic](create_subscription.rb#L4) (`Subscribe`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for Ruby Amazon SNS reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Sns.html)

<!--custom.resources.start-->
* [More Ruby Amazon SNS code examples](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/ruby_sns_code_examples.html)
* [SDK for Ruby Developer Guide](https://aws.amazon.com/developer/language/ruby/)
* [SDK for Ruby Amazon SNS Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/SNS.html)
* [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
* [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0