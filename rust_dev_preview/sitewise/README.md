# AWS SDK for Rust code examples for AWS IoT SiteWise

## Purpose

These examples demonstrate how to perform several AWS IoT SiteWise operations using the developer preview version of the AWS SDK for Rust.

AWS IoT SiteWise is a managed service that simplifies collecting, organizing, and analyzing industrial equipment data.

## Code examples

- [List assets](src/bin/list-assets.rs) (ListAssets)

## ⚠ Important

- Running this code might result in charges to your AWS account.
- We recommend that you grant this code least privilege, 
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Running the code examples

### Prerequisites

You must have an AWS account with your default credentials and AWS Region configured as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### list-assets.rs

This example lists AWS IoT SiteWise assets in the Region.

`cargo run --bin list-assets -- -f FILTER [-a ASSET-MODEL-ID] [-r REGION] [-v]`

- _FILTER_ is the type of filter. It must be one of the following:
  - ALL - The list includes all assets for a given asset model ID. The _ASSET-MODEL-ID_ parameter is required if you filter by ALL.
  - TOP_LEVEL - The list includes only top-level assets in the asset hierarchy tree.
- _ASSET-MODEL-ID_ The ID of the asset model by which to filter the list of assets. This parameter is required if you choose ALL for _FILTER_.
- _REGION_ The Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

If the example succeeds, it displays the Amazon Resource Name (ARN) of the new role.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- - [AWS SDK for Rust API Reference for AWS IoT SiteWise](https://docs.rs/aws-sdk-iotsitewise)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
