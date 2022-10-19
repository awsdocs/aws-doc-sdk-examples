# SQS code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon Simple Queue Service (Amazon SQS) queues using the SDK for Ruby.

SQS is a fully managed message queuing service that makes it easy to decouple and scale microservices, distributed systems, and serverless applications.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a queue](./sqs-ruby-example-create-queue.rb) (`CreateQueue`)

* [Delete a queue](./sqs-ruby-example-delete-queue.rb) (`DeleteQueue`)

* [Enable a resource](./sqs-ruby-example-get-messages.rb) (`SetQueueAttributes`)

* [Enable long polling](./sqs-ruby-example-enable-long-polling.rb) (`SetQueueAttributes`)

* [Get messages](./sqs-ruby-example-get-messages-with-long-polling.rb) (`ReceiveMessage`)

* [Get messages with long-polling](./sqs-ruby-example-long-polling.rb) (`QueuePoller.poll`)

* [List queues](./sqs-ruby-example-show-queues.rb) (`ListQueues`)

* [List subscriptions](./sqs-ruby-example-enable-resource.rb) (`ListSubscriptions`)

* [Poll messages](./sqs-ruby-example-poll-messages.rb) (`QueuePoller.poll`)

* [Redirect a dead-letter queue](./sqs-ruby-example-redirect-queue-deadletters.rb) (`SetQueueAttributes`)

* [Send a batch message](./sqs-ruby-example-send-message-batch.rb) (`SendMessageBatch`)

* [Send a message](./sqs-ruby-example-send-message.rb) (`SendMessage`)

* [Send a message to a queue](./sqs-ruby-example-send-receive-messages.rb) (`SendMessage`)

* [Set time messages not visible after being received](./sqs-ruby-example-message-visibility-timeout.rb) (`ChangeMessageVisibility`)

* [Visibility timeout (example 1)](./sqs-ruby-example-visibility-timeout.rb) (`ChangeMessageVisibility`)

* [Visibility timeout (example 2)](./sqs-ruby-example-visibility-timeout2.rb) (`ChangeMessageVisibility`)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Working with dead letter queues](./sqs-ruby-example-dead-letter-queue.rb)

* [Working with messages](./sqs-ruby-example-send-receive-messages.rb)

* [Working with queues](./sqs-ruby-example-using-queues.rb)





## Run the examples

### Prerequisites

See the [Ruby README.md(https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md) for pre-requisites.

### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!

* To propose a new example, submit an [Enhancement Request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fenhancement&template=enhancement.yaml&title=%5BEnhancement%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~2 min).
* To fix a bug, submit a [Bug Report](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fbug&template=bug.yaml&title=%5BBug%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~5 min).
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)
### Testing
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
