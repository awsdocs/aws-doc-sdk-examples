# AWS SDK for Ruby code examples for AWS Key Management Service

## Purpose

This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate AWS Key Management Service.

## Code examples
This is a workspace where you can find the following AWS SDK for Ruby version 3 (v3) AWS Key Management Service examples:

### API examples
- [Create a key](./aws-ruby-sdk-kms-example-create-key.rb)
- [Decrypt a blob](./aws-ruby-sdk-kms-example-decrypt-blob.rb)
- [Encrypt data](./aws-ruby-sdk-kms-example-encrypt-data.rb)
- [Re-encrypt data](./aws-ruby-sdk-kms-example-re-encrypt-data.rb)

## Prerequisites

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

## Cautions

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `cw-ruby-example-create-alarm.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby cw-ruby-example-create-alarm.rb
```

Most of these files have been refactored into reusable functions that can be copied into your own code. You can then call those functions directly from your own code without modifying the copied function code itself. For example, you could copy the `alarm_created_or_updated?` function code from the `cw-ruby-example-create-alarm.rb` file into your own code. You could then adapt the code in the `run_me` function in that same file as a basis to write your own code to call the copied `alarm_created_or_updated?` function.

## Running the tests

Most of these code example files have accompanying tests that are written to work with RSpec. These tests are in the `tests` folder and contain the same file name as the corresponding code example file, for example `tests/test_cw-ruby-example-create-alarm.rb` contains tests for `cw-ruby-example-create-alarm.rb`.

To use RSpec to run all tests within a file, specify the path to that file, for example:

```
rspec tests/test_cw-ruby-example-create-alarm.rb
```

To explore additional options for using RSpec to run tests, run the `rspec --help` command. 

Most of these tests are designed to use stubs, to avoid generating unnecessary costs in an AWS account. For more information, see [Stubbing Client Responses and Errors](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/stubbing.html) in the *AWS SDK for Ruby Developer Guide*.


## Additional information

- [AWS Key Management Service Documentation](https://docs.aws.amazon.com/Elastic TransCoder)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
