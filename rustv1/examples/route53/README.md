# AWS SDK for Rust code examples for Amazon Route 53

## Purpose

These examples demonstrate how to perform several Amazon Route 53 operations using the developer preview version of the AWS SDK for Rust.

Amazon Route 53 is a highly available and scalable Domain Name System (DNS) web service.

## Code examples

- [Displays the IDs and names of the hosted zones](src/bin/route53-helloworld.rs) (GetHostedZoneCount, ListHostedZones)

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

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Rust](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/getting-started.html).

### route53-helloworld

This example displays the IDs and names of the hosted zones in the Region.

`cargo run --bin route53-helloworld -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon Route 53](https://docs.rs/aws-sdk-route53)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
