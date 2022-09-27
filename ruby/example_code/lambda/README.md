# Lambda code examples for the SDK for Ruby

## Overview

These code examples demonstrate how to use the AWS SDK for Ruby (v3) to manage and invoke
AWS Lambda functions.

With AWS Lambda, you can run code without provisioning or managing servers. You pay only
for the compute time that you consume—there's no charge when your code isn't running.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum 
permissions required to perform the task. For more information, see 
[Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see
[AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action
Code excerpts that show you how to call individual service functions.

* [Create a function](lambda_basics.rb)
  (`CreateFunction`)
* [Delete a function](lambda_basics.rb)
  (`DeleteFunction`)
* [Get a function](lambda_basics.rb)
  (`GetFunction`)
* [Invoke a function](lambda_basics.rb)
  (`Invoke`)
* [List functions](lambda_basics.rb)
  (`ListFunctions`)
* [Update function code](lambda_basics.rb)
  (`UpdateFunctionCode`)
* [Update function configuration](lambda_basics.rb)
  (`UpdateFunctionConfiguration`)

### Scenario
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Get started with functions](scenario_getting_started_functions.rb)

## Run the examples

### Prerequisites

### Get started with functions

This interactive scenario runs at a command prompt and shows you how to use
Lambda to do the following:

1. Create an AWS Identity and Access Management (IAM) role role that grants Lambda
   permission to write to logs.
1. Create a Lambda function and upload handler code.
1. Invoke the function with a single parameter and get results.
1. Update the function code and configure its Lambda environment with an environment
   variable.
1. Invoke the function with new parameters and get results. Display the execution
   log that's returned from the invocation.
1. List the functions for your account.
1. Delete the IAM role and the Lambda function.

Start the scenario at a command prompt.

```
ruby scenario_getting_started_functions.rb
```

### Prerequisites

Prerequisites for running the examples for this service can be found in the
[README](../../README.md#Prerequisites) in the Ruby folder.

## Tests

Instructions for running the tests for this service can be found in the
[README](../../README.md#Tests) in the Ruby folder.

## Additional resources
* [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
* [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
* [AWS SDK for Ruby Lambda Client](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Lambda/Client.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0







# AWS SDK for Ruby code examples for AWS Lambda

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate AWS Lambda.

With AWS Lambda, you can run code without provisioning or managing servers.

## Code examples

### API examples
- [Configure a Lambda function for notifications](./aws-ruby-sdk-lambda-example-configure-function-for-notification.rb)
- [Run a Lambda function](./aws-ruby-sdk-lambda-example-create-function.rb)
- [List Lambda functions](./aws-ruby-sdk-lambda-example-run-function.rb)
- [Show alarms](./aws-ruby-sdk-lambda-example-show-functions.rb)

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `aws-ruby-sdk-lambda-example-configure-function-for-notification.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby aws-ruby-sdk-lambda-example-configure-function-for-notification.rb
```

## Additional information

- [AWS Lambda Documentation](https://docs.aws.amazon.com/lambda/)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
