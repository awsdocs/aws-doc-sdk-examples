# AWS SDK for Rust code examples for Secrets Manager

## Purpose

These examples demonstrate how to perform several AWS Secrets Manager (Secrets Manager) operations using the developer preview version of the AWS SDK for Rust.

Secrets Manager helps you to securely encrypt, store, and retrieve credentials for your databases and other services.

## Code examples

- [Creates a secret] (src/bin/create-secret.rs) (CreateSecret)
- [Gets the value of a secret] (src/bin/get-secret-value.rs) (GetSecretValue)
- [Lists your secrets] (src/bin/list-secrets.rs) (ListSecrets)

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

### create-secret

This example creates a Secrets Manager secret in the Region.

`cargo run --bin create-secret -- -n NAME -s SECRET-VALUE [-r REGION] [-v]`

- _NAME_ is the name of the secret.
- _SECRET-VALUE_ is the value of the secret.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### get-secret-value

Displays the value of a Secrets Manager secret in the Region.

`cargo run --bin get-secret-value -- -n NAME [-r REGION] [-v]`

- _NAME_ is the name of the secret.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-secrets

This example lists the names of the Secrets Manager secrets in the Region.

`cargo run --bin list-secrets -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Secrets Manager](https://docs.rs/aws-sdk-secretsmanager)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
