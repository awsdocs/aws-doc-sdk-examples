# SQS code examples for the AWS SDK for Ruby (v3)
## Overview
These examples show how to create and manage Amazon Simple Queue Service (Amazon SQS) queues using the AWS SDK for Ruby (v3).

Amazon Simple Queue Service (Amazon SQS) is a fully managed message queuing service that makes it easy to decouple and scale microservices, distributed systems, and serverless applications. Amazon SQS moves data between distributed application components and helps you decouple these components.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a queue](./sqs-ruby-example-create-queue.rb)

* [Delete a queue](./sqs-ruby-example-delete-queue.rb)

* [Enable a resource](./sqs-ruby-example-get-messages.rb)

* [Enable long polling](./sqs-ruby-example-enable-long-polling.rb)

* [Get messages](./sqs-ruby-example-get-messages-with-long-polling.rb)

* [Get messages with long-polling](./sqs-ruby-example-long-polling.rb)

* [List queues](./sqs-ruby-example-show-queues.rb)

* [List subscriptions](./sqs-ruby-example-enable-resource.rb)

* [Poll messages](./sqs-ruby-example-poll-messages.rb)

* [Redirect a dead-letter queue](./sqs-ruby-example-redirect-queue-deadletters.rb)

* [Send a batch message](./sqs-ruby-example-send-message-batch.rb)

* [Send a message](./sqs-ruby-example-send-message.rb)

* [Send a message to a queue](./sqs-ruby-example-send-receive-messages.rb)

* [Set time messages not visible after being received](./sqs-ruby-example-message-visibility-timeout.rb)

* [Visibility timeout (example 1)](./sqs-ruby-example-visibility-timeout.rb)

* [Visibility timeout (example 2)](./sqs-ruby-example-visibility-timeout2.rb)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Working with dead letter queues](./sqs-ruby-example-dead-letter-queue.rb)

* [Working with messages](./sqs-ruby-example-send-receive-messages.rb)

* [Working with queues](./sqs-ruby-example-using-queues.rb)





## Run the examples


### Prerequisites

1. An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.

1. AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the *AWS SDK for Ruby Developer Guide*.

1. To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Programming Language website.

1. To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.

1. The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the *AWS SDK for Ruby Developer Guide*.



### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your Command Line Interface (CLI). For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!

* To propose a new example, submit an [Enhancement Request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fenhancement&template=enhancement.yaml&title=%5BEnhancement%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~2 min).
* To fix a bug, submit a [Bug Report](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fbug&template=bug.yaml&title=%5BBug%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~5 min).
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)
### Testing
⚠️ Running these tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
