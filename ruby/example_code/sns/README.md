# SNS code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon Simple Notification Service (Amazon SNS) topics and subscriptions using the SDK for Ruby.

SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a subscription](./sns-ruby-example-create-subscription.rb) (`CreateSubscription`)

* [Create a topic](./sns-ruby-example-create-topic.rb) (`CreateTopic`)

* [Enable a resource](./sns-ruby-example-enable-resource.rb) (`Topic.set_attributes`)

* [Send a message](./sns-ruby-example-send-message.rb) (`Publish`)

* [List subscriptions](./sns-ruby-example-show-subscriptions.rb) (`ListSubscriptions`)

* [List topics](./sns-ruby-example-show-topics.rb) (`ListTopics`)






## Run the examples

### Prerequisites

See the [Ruby README.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md) for prerequisites.

### Instructions
The easiest way to interact with this example code is by invoking [Single Actions](#single-actions) from your command line. This may require some modification to override hard-coded values, and some actions also expect runtime parameters. For example, `ruby some_action.rb ARG1 ARG2` will invoke `some_action.rb` with two arguments.

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
