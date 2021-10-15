# AWS SDK for Ruby code examples for Amazon RDS

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate Amazon Relational Database Service (Amazon RDS).

Amazon Relational Database Service (Amazon RDS) makes it easy to set up, operate, and scale a relational database in the cloud. It provides 
cost-efficient and resizable capacity while automating time-consuming administration tasks such as hardware provisioning, database setup, 
patching and backups. It frees you to focus on your applications so you can give them the fast performance, high availability, security and 
compatibility they need.

## Code examples
This is a workspace where you can find the following AWS SDK for Ruby version 3 (v3) Amazon RDS examples:

### API examples
- [Create a cluster snapshot](./rds-ruby-example-create-cluster-snapshot.rb)
- [Create a snapshot](./rds-ruby-example-create-snapshot.rb)
- [List all instances](./rds-ruby-example-list-all-instances.rb)
- [List a cluster's snapshots](./rds-ruby-example-list-cluster-snapshots.rb)
- [List instance snapshots](./rds-ruby-example-list-instance-snapshots.rb)
- [List parameter groups](./rds-ruby-example-list-parameter-groups.rb)
- [List subnet groups](./rds-ruby-example-list-security-groups.rb)
- [List security groups](./rds-ruby-example-list-subnet-groups.rb)

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `rds-ruby-example-create-cluster-snapshot.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby rds-ruby-example-create-cluster-snapshot.rb
```

## Additional information

- [Amazon Relational Database Service Documentation](https://docs.aws.amazon.com/rds/)
- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs)
- [RSpec Documentation](https://rspec.info/documentation)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
