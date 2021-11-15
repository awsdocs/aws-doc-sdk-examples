# Amazon SES C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Simple Email Service (Amazon SES)
using the AWS SDK for C++.

Amazon Simple Email Service (Amazon SES) is a reliable, scalable, and cost-effective email service designed to 
help digital marketers and application developers send marketing, notification, and transactional emails.

## Code examples

### API examples
- [Create an Amazon SES receipt filter](./create_receipt_filter.cpp) (CreateReceiptFilter)
- [Create an Amazon SES receipt rule](./create_receipt_rule.cpp) (CreateReceiptRule)
- [Create an Amazon SES receipt rule set](./create_receipt_rule_set.cpp) (CreateReceiptRuleSet)
- [Create an Amazon SES template](./create_template.cpp) (CreateTemplate)
- [Delete an Amazon SES identity](./delete_identity.cpp) (DeleteIdentity)
- [Delete an Amazon SES receipt filter](./delete_receipt_filter.cpp) (DeleteReceiptFilter)
- [Delete an Amazon SES receipt filter rule](./delete_receipt_rule.cpp) (DeleteReceiptFilterRule)
- [Delete an Amazon SES receipt filter rule set](./delete_receipt_rule_set.cpp) (DeleteReceiptFilterRuleSet)
- [Delete an Amazon SES template](./delete_template.cpp) (DeleteTemplate)
- [Get an Amazon SES template](./get_template.cpp) (GetTemplate)
- [List your Amazon SES identities](./list_identities.cpp) (ListIdentities)
- [List your Amazon SES receipt filters](./list_receipt_filters.cpp) (ListReceiptFilters)
- [Send an email using Amazon SES](./send_email.cpp) (SendEmail)
- [Send a templated email using Amazon SES](./send_templated_email.cpp) (SendTemplatedEmail)
- [Update an email template using Amazon SES](./update_template.cpp) (UpdateTemplate)
- [Verify an Amazon SES email identity](./verify_email_identity.cpp) (VerifyEmailIdentity)




## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- Complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 
- See [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

To run these code examples, your AWS user must have permissions to perform these actions with Amazon SES.  
The AWS managed policy named "AmazonSESFullAccess" may be used to bulk-grant the necessary permissions.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon Simple Email Service Documentation](https://docs.aws.amazon.com/ses/)
