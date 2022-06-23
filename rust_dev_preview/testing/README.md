# Testing AWS SDK for Rust code examples

## Purpose

These examples demonstrate how to create a unit test for a Rust SDK operation.
It uses the Amazon Simple Storage Service (Amazon S3) ListObjectsV2 operation
to accumulate the size, in bytes, of objects in a bucket with a specified prefix,
and shows how to mock the call using traits or an enum.

## Code example

- [Get object sizes mocked by enum](src/enums.rs) (ListObjectsV2)
- [Get object sizes](src/intro.rs) (ListObjectsV2)
- [Get object sizes mocked by trait](src/traits.rs) (ListObjectsV2)

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

## Running the code example

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### intro

This example gets the accumulated size, in bytes, of the objects with a specified prefix in a bucket.

`cargo run -- -b BUCKET -p PREFIX [-r REGION] [-v]`

- _BUCKET_ is the name of the bucket.
- _PREFIX_ is the first part of the bucket name.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Running the unit tests

There are four unit tests, two with the same name in `enums.rs` and two in `traits.rs`.

You can run all of the tests with the following command:

`cargo test`

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon S3](https://docs.rs/aws-sdk-s3)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0