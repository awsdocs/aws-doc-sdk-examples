# AWS SDK for Rust code examples for Amazon SES

Amazon Simple Email Service (Amazon SES) is  a reliable, scalable, and cost-effective email service designed to help digital marketers and application developers send marketing, notification, and transactional emails.

## Purpose

These examples demonstrate how to perform several Amazon EC2 operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-contact

This example adds a contact to the contact list. 

`cargo run --bin create-contact -- -c CONTACT-LIST -e EMAIL-ADDRESS [-d DEFAULT-REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _EMAIL-ADDRESS_ is the email address of the contact.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### create-contact-list

This example creates a contact list.

`cargo run --bin create-contact-list -- -c CONTACT-LIST [-d DEFAULT-REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### email-contact-list

This example sends a message to the email addresses in the contact list.

`cargo run --bin email-contact-list -- -c CONTACT-LIST -f FROM-ADDRESS -m MESSAGE -s SUBJECT [-d DEFAULT-REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _FROM-ADDRESS_ is the email address of the sender.
- _MESSAGE_ is the message sent to each contact in the contact list.
- _SUBJECT_ is the subject of the message.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### list-contact-lists

This example lists the names of your contact lists.

`cargo run --bin list-contact-lists -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### list-contact

This example lists the email addresses of the contacts in a contact list.

`cargo run --bin ???-??? -- -c CONTACT-LIST [-d DEFAULT-REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

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
