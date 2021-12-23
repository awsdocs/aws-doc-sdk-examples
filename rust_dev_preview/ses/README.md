# AWS SDK for Rust code examples for Amazon SES

## Purpose

These examples demonstrate how to perform several Amazon Simple Email Service (Amazon SES) operations using the developer preview version of the AWS SDK for Rust.

Amazon SES is  a reliable, scalable, and cost-effective email service designed to help digital marketers and application developers send marketing, notification, and transactional emails.

## Code examples

- [Add a contact to a contact list](src/bin/create-contact.rs) (CreateContact)
- [Create contact list](src/bin/create-contact-list.rs) (CreateContactList)
- [Is email address verified?](src/bin/is-email-verified.rs) (GetEmailIdentity)
- [Lists your contact lists](src/bin/list-contact-lists.rs) (ListContactLists)
- [Lists the email addresses of the contacts in a contact list](src/bin/list-contacts.rs) (ListContacts)
- [Send message to all contacts](src/bin/send-email.rs) (ListContacts, SendEmail)

## ⚠ Important

- We recommend that you grant this code least privilege, 
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

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

### list-contacts

This example lists the email addresses of the contacts in a contact list in the Region.

`cargo run --bin list-contact -- -c CONTACT-LIST [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### send-email

This example sends a message to the email addresses in the contact list.

`cargo run --bin send-email -- -c CONTACT-LIST -f FROM-ADDRESS -m MESSAGE -s SUBJECT [-r REGION] [-v]`

- _CONTACT-LIST_ is the name of the contact list.
- _FROM-ADDRESS_ is the email address of the sender.
- _MESSAGE_ is the message sent to each contact in the contact list.
- _SUBJECT_ is the subject of the message.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon SES](https://docs.rs/aws-sdk-ses)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
