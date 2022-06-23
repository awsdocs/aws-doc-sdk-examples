# AWS SDK for Ruby code examples for Amazon EC2

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate 
Amazon Elastic Compute Cloud (Amazon EC2).

Amazon EC2 is a web service that provides resizable computing capacity—literally, 
servers in Amazon's data centers—that you use to build and host your software systems.

## Code examples 

## Usage examples
- [Create an Internet gateway and attach it to a VPC](./ec2-ruby-example-attach-igw-vpc.rb)
- [Manage addresses](./ec2-ruby-example-elastic-ips.rb)
- [Manage key pairs](./ec2-ruby-example-key-pairs.rb)
- [Manage security groups](./ec2-ruby-example-security-group.rb)
- [Manage instances](./ec2-ruby-example-manage-instances.rb)

### API examples
- [Create an instance](./ec2-ruby-example-create-instance.rb)
- [Create a key pair](./ec2-ruby-example-create-key-pair.rb)
- [Create a route table](./ec2-ruby-example-create-route-table.rb)
- [Create a security group](./ec2-ruby-example-create-security-group.rb)
- [Create a subnet](./ec2-ruby-example-create-subnet.rb)
- [Create a VPC](./ec2-ruby-example-create-vpc.rb)
- [Get instance information by tag](./ec2-ruby-example-get-instance-info-by-tag.rb)
- [List state instances](./ec2-ruby-example-list-state-instance-i-123abc.rb)
- [Reboot an instance](./ec2-ruby-example-reboot-instance-i-123abc.rb)
- [Display list of available AWS Regions](./ec2-ruby-example-regions-availability-zones.rb)
- [Start an instance](./ec2-ruby-example-start-instance-i-123abc.rb)
- [Stop an instance](./ec2-ruby-example-stop-instance-i-123abc.rb)
- [Terminate an instance](./ec2-ruby-example-terminate-instance-i-123abc.rb)

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `ec2-ruby-example-create-instance.rb` file, you can run the file along with the correct command-line arguments. Or you can replace the equivalent hard-coded values in the file with your own values, save the file, and then run the file without any command-line arguments. For example:

```
# Option 1: Run the file with the correct command-line arguments.

ruby ec2-ruby-example-create-instance.rb ami-0947d2ba12EXAMPLE my-key-pair my-key my-value t2.micro us-west-2 my-user-data.txt

# Option 2: Replace the equivalent hard-coded values in the file with your own values, save the file, and then run the file without any command-line arguments.

ruby ec2-ruby-example-create-instance.rb
```

To display the list of correct command-line arguments, run the file along with the `-h` or `--help` option, for example:

```
# Running one of the following:

ruby ec2-ruby-example-create-instance.rb -h
ruby ec2-ruby-example-create-instance.rb --help

# Displays:

Usage:   ruby ec2-ruby-example-create-instance.rb IMAGE_ID KEY_PAIR_NAME TAG_KEY TAG_VALUE INSTANCE_TYPE REGION [USER_DATA_FILE]
Example: ruby ec2-ruby-example-create-instance.rb ami-0947d2ba12EXAMPLE my-key-pair my-key my-value t2.micro us-west-2 my-user-data.txt
```

Most of these files have been refactored into reusable functions that can be copied into your own code. You can then call those functions directly from your own code without modifying the copied function code itself. For example, you could copy the `instance_created?` function code from the `ec2-ruby-example-create-instance.rb` file into your own code. You could then adapt the code in the `run_me` function in that same file as a basis to write your own code to call the copied `instance_created?` function.

## Running the tests

Most of these code example files have accompanying tests that are written to work with RSpec. These tests are in the `tests` folder and contain the same file name as the corresponding code example file, for example `tests/test_ec2-ruby-example-create-instance.rb` contains tests for `ec2-ruby-example-create-instance.rb`.

To use RSpec to run all tests within a file, specify the path to that file, for example:

```
rspec tests/test_ec2-ruby-example-create-instance.rb
```

To explore additional options for using RSpec to run tests, run the `rspec --help` command. 

Most of these tests are designed to use stubs, to avoid generating unnecessary costs in an AWS account. For more information, see [Stubbing Client Responses and Errors](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/stubbing.html) in the *AWS SDK for Ruby Developer Guide*.


## Additional information

- [Amazon Elastic Compute Cloud Documentation](https://docs.aws.amazon.com/ec2)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
