# AWS SDK for Rust code examples for Snowball

## Purpose

These examples demonstrate how to perform several AWS Snowball (Snowball) operations using the developer preview version of the AWS SDK for Rust.

Snowball uses physical storage devices to transfer large amounts of data between Amazon Simple Storage Service (Amazon S3) and your onsite data storage location at faster-than-internet speeds.

## Code examples

- [Create an address](src/bin/create-address.rs) (CreateAddress)
- [Lists your addresses](src/bin/describe-addresses.rs) (DescribeAddresses)
- [Lists your jobs](src/bin/list-jobs.rs) (ListJobs)

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

### create-address

This example creates an AWS Snowball address.

`cargo run --bin create-address -- --city CITY --company COMPANY --country COUNTRY --landmark LANDMARK --name NAME --phone-number PHONE-NUMBER --postal-code POSTAL-CODE --prefecture-or-district PREFECTURE-OR-DISTRICT --state STATE --street1 STREET1 --street2 STREET2 --street3 STREET3 [-r REGION] [-v]`

- _CITY_ is the required city portion of the address.
- _COMPANY_ is the company portion of the address.
- _COUNTRY_ is the required country portion of the address.
- _LANDMARK_ is the landmark portion of the address.
- _NAME_ is the required name portion of the address.
- _PHONE-NUMBER_ is the required phone number portion of the address.
- _POSTAL-CODE_ is the required postal code (zip in USA) portion of the address.
- _PREFECTURE-OR-DISTRICT_ is the prefecture or district portion of the address.
- _STATE_ is the required state portion of the address. It must be (two is best) upper-case letters.
- _STREET1_ is the required first street portion of the address.
- _STREET2_ is the second street portion of the address.
- _STREET3_ is the third street portion of the address.
- _REGION_ is the AWS Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### describe-addresses

This example lists your AWS Snowball addresses.

`cargo run --bin describe-addresses -- [-r REGION] [-v]`

- _REGION_ is the AWS Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

### list-jobs

This example lists your AWS Snowball jobs.

`cargo run --bin list-jobs -- [-r REGION] [-v]`

- _REGION_ is the AWS Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.  

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Snowball](https://docs.rs/aws-sdk-snowball)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
