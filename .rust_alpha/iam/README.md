# AWS SDK for Rust code examples for IAM

## Purpose

These examples demonstrate how to perform several AWS Identity and Access Management (IAM) operations using the alpha version of the AWS SDK for Rust.

IAM is a web service for securely controlling access to AWS services.

## Code examples

- [Create a role](src/bin/create-role.rs) (CreateRole)

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

### create-role.rs

This example creates an IAM role in the Region.

`cargo run --bin create-role -- -a ACCOUNT-ID -b BUCKET -n NAME -p POLICY-NAME [-r REGION] [-v]`

- _ACCOUNT-ID_ is the ID of an instance to stop.
- _BUCKET_ is the name of the bucket where Config stores information about resources.
- _NAME_ is the name of the role.
- _POLICY-NAME_ is the name of the JSON file containing the policy document.
- _REGION_ the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

If the example succeeds, it displays the ARN of the new role.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
