# AWS SDK for Rust code examples for Amazon SES

Amazon Simple Email Service (Amazon SES) is  a reliable, scalable, and cost-effective email service designed to help digital marketers and application developers send marketing, notification, and transactional emails.

## Purpose

These examples demonstrate how to perform several Amazon EC2 operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-contact

This example adds a contact to the contact list in the Region. 

`cargo run --bin create-contact -- -c CONTACT-LIST -e EMAIL-ADDRESS [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _EMAIL-ADDRESS_ is the email address of the contact.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### create-contact-list

This example creates a contact list.

`cargo run --bin create-contact-list -- -c CONTACT-LIST [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### email-contact-list

This example sends a message to the email addresses in the contact list.

`cargo run --bin email-contact-list -- -c CONTACT-LIST -f FROM-ADDRESS -m MESSAGE -s SUBJECT [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _FROM-ADDRESS_ is the email address of the sender.
- _MESSAGE_ is the message sent to each contact in the contact list.
- _SUBJECT_ is the subject of the message.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### is-email-verified

This example determines whether an email address has been verified. 

`cargo run --bin is-email-verified -- -e EMAIL-ADDRESS [-r REGION] [-v]`

- _EMAIL-ADDRESS_ is the email address.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### list-contact-lists

This example lists the names of the contact lists in the Region.

`cargo run --bin list-contact-lists -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### list-contact

This example lists the email addresses of the contacts in a contact list in the Region.

`cargo run --bin ???-??? -- -c CONTACT-LIST [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
