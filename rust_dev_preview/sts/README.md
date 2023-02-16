# AWS SDK for Rust code examples for Security Token Service

## Purpose

These examples demonstrate how to perform several AWS STS (Security Token Service) operations using the developer preview version of the AWS SDK for Rust.

Use Security Token Service to request temporary, limited-privilege credentials for AWS Identity and Access Management (IAM) users or for users you authenticate (federated users).

## Code examples

- [Get caller identity](src/bin/get-caller-identity.rs) (GetCallerIdentity)
- [Assume role](src/bin/assume-role.rs) (AssumeRole)

## ⚠ Important

- We recommend that you grant this code least privilege, 
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).


## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### assume-role

This example assumes a role and displays the user ID, Account and Arn. 

`cargo run --bin assume-role -- --role-arn ROLE-ARN --role-session-name SESSION-NAME [-r Region] [-v]`

Where:

- _ROLE-ARN_ is the ARN of the role to assume.
- _SESSION-NAME_ is the name of session.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### get-caller-identity

This example assumes a role and displays the user ID, Account and Arn. 

`cargo run --bin get-caller-identity -- [-r Region] [-v]`

Where:

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Secure Token Service](https://docs.rs/aws-sdk-sts)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0