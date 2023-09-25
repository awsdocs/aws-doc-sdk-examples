# AWS SDK for Rust code example for AWS Auto Scaling plans

## Purpose

These examples demonstrate how to perform several Auto Scaling group operations using the developer preview version of the AWS SDK for Rust.

Use scaling plans to set up scaling policies across a collection of supported resources from services including Aurora, DynamoDB, EC2 Spot, and ECS.

## Code examples

- [List plans](src/bin/describe-scaling-plans.rs) (DescribeScalingPlans)

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

### describe-scaling-plans

This example lists your auto scaling plans in the Region.

`cargo run --bin describe-scaling-plans -- [-r REGION] [-v]`

- _REGION_ is name of the Region where the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for AWS Auto Scaling Plans](https://docs.rs/aws-sdk-autoscalingplans/latest/aws_sdk_autoscalingplans)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
