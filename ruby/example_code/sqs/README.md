# Amazon SDK for Ruby code examples for Amazon SQS

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate Amazon Simple Queue Service (Amazon SQS).

Amazon SQS is a fully managed message queuing service that makes it easy to 
decouple and scale microservices, distributed systems, and serverless applications. 

## Code examples

###  Usage examples
- [Working with dead letter queues](./sqs-ruby-example-dead-letter-queue.rb)
- [Working with messages](./sqs-ruby-example-send-receive-messages.rb)
- [Working with queues](./sqs-ruby-example-using-queues.rb)

### API examples
- [Create a queue](./sqs-ruby-example-create-queue.rb)
- [Delete a queue](./sqs-ruby-example-delete-queue.rb)
- [Enable a resource](./sqs-ruby-example-get-messages.rb)
- [Enable long polling](./sqs-ruby-example-enable-long-polling.rb)
- [Get messages](./sqs-ruby-example-get-messages-with-long-polling.rb)
- [Get messages with long-polling](./sqs-ruby-example-long-polling.rb)
- [List queues](./sqs-ruby-example-show-queues.rb)
- [List subscriptions](./sqs-ruby-example-enable-resource.rb)
- [Poll messages](./sqs-ruby-example-poll-messages.rb)
- [Redirect a dead-letter queue](./sqs-ruby-example-redirect-queue-deadletters.rb)
- [Send a batch message](./sqs-ruby-example-send-message-batch.rb)
- [Send a message](./sqs-ruby-example-send-message.rb)
- [Send a message to a queue](./sqs-ruby-example-send-receive-messages.rb)
- [Set time messages not visible after being received](./sqs-ruby-example-message-visibility-timeout.rb)
- [Visibility timeout (example 1)](./sqs-ruby-example-visibility-timeout.rb)
- [Visibility timeout (example 2)](./sqs-ruby-example-visibility-timeout2.rb)

## Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific AWS Regions. For more information, see the 
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see 
  [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the 
  *AWS SDK for Ruby Developer Guide*.
- To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see 
  [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Progamming Language website.
- To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.
- The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see 
  [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the 
  *AWS SDK for Ruby Developer Guide*.

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `sqs-ruby-example-create-queue.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby sqs-ruby-example-create-queue.rb
```

Most of these files have been refactored into reusable functions that can be copied into your own code. You can then call those functions directly from your own code without modifying the copied function code itself. For example, you could copy the `queue_created?` function code from the `sqs-ruby-example-create-queue.rb` file into your own code. You could then adapt the code in the `run_me` function in that same file as a basis to write your own code to call the copied `queue_created?` function.

## Running the tests

Most of these code example files have accompanying tests that are written to work with RSpec. These tests are in the `tests` folder and contain the same file name as the corresponding code example file, for example `tests/test_sqs-ruby-example-create-queue.rb` contains tests for `sqs-ruby-example-create-queue.rb`.

To use RSpec to run all tests within a file, specify the path to that file, for example:

```
rspec tests/test_sqs-ruby-example-create-queue.rb
```

To explore additional options for using RSpec to run tests, run the `rspec --help` command. 

Most of these tests are designed to use stubs, to avoid generating unnecessary costs in an AWS account. For more information, see [Stubbing Client Responses and Errors](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/stubbing.html) in the *AWS SDK for Ruby Developer Guide*.



## Resources
- [Amazon SQS Documentation](https://docs.aws.amazon.com/sqs/)
- [AWS SDK for Ruby repo](https://github.com/aws/aws-sdk-ruby) 
- [AWS SDK for Ruby Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
- [AWS SDK for Ruby v3 API Reference Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/) 

