# AWS SDK for Rust code examples for Amazon Cognito identity pools

## Purpose

These examples demonstrate how to perform several Amazon Cognito identity pool operations using the developer preview version of the AWS SDK for Rust.

Amazon Cognito identity pools (federated identities) enable you to create unique identities for your users and federate them with identity providers. With an identity pool, you can obtain temporary, limited-privilege AWS credentials to access other AWS services.

## Code examples

- [Describe an identity pool](src/bin/describe-identity-pool.rs) (DescribeIdentityPool)
- [List your identity pools](src/bin/list-identity-pools.rs) (ListIdentityPools)
- [List the identities in an identity pool](src/bin/list-pool-identities) (ListIdentities)

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

### describe-identity-pool

This example displays some information about an Amazon Cognito identity pool.

`cargo run --bin describe-identity-pool -- -i IDENTITY-POOL-ID [-r REGION] [-v]`

- _IDENTITY-POOL-ID_ is the ID of the identity pool.
- _DEFAULT-REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-identity-pools

This example lists your Amazon Cognito identity pools in the Region.

`cargo run --bin list-identity-pools -- [-r REGION] [-v]`

- _DEFAULT-REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-pool-identities

This example lists the identities in an Amazon Cognito identity pool.

`cargo run --bin list-pool-identities -- -i IDENTITY-POOL-ID [-r REGION] [-v]`

- _IDENTITY-POOL-ID_ is the ID of the identity pool.
- _DEFAULT-REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon Cognito Identity Pools](https://docs.rs/aws-sdk-cognitoidentity)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
