# Amazon SDK for Ruby SDK code examples for Amazon WorkDocs

## Purpose
This folder contains code examples that demonstrate how to use the AWS SDK for Ruby to automate Amazon WorkDocs.

Amazon WorkDocs is a fully managed, secure content creation, storage, and collaboration service. With Amazon WorkDocs, 
you can easily create, edit, and share content, and because it’s stored centrally on AWS, access it from anywhere on any device. 

## Code examples

### API examples
- [List a user's documents](./wd_list_user_docs.rb)
- [List users](./wd_list_users.rb)

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

Most of these code example files can be run with very little to no modification. For example, to use Ruby to run the `wd_list_user_docs.rb` file, replace the hard-coded values in the file with your own values, save the file, and then run the file. For example:

```
ruby wd_list_user_docs.rb
```


## Resources
- [Amazon WorkDocs Documentation](https://docs.aws.amazon.com/workdocs/)
- [AWS SDK for Ruby repo](https://github.com/aws/aws-sdk-ruby) . 
- [AWS SDK for Ruby Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
- [AWS SDK for Ruby v3 API Reference Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/) 

