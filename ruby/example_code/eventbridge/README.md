# AWS SDK for Ruby code examples for Amazon EventBridge

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate Amazon EventBridge.

Amazon EventBridge (formerly Amazon CloudWatch Events) is a serverless event bus service that you 
can use to connect your applications with data from a variety of sources.

## Code examples

###  Usage scenarios
- [Create and trigger a rule in Amazon CloudWatch Events](./cw-ruby-example-send-events-ec2.rb)

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `cw-ruby-example-send-events-ec2.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby cw-ruby-example-send-events-ec2.rb
```
## Additional information

- [Amazon EventBridge Documentation](https://docs.aws.amazon.com/eventbridge)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
