# AWS SDK for Rust code examples for Auto Scaling group

## Purpose

These examples demonstrate how to perform several Amazon Elastic Compute Cloud (Amazon EC2) Auto Scaling group (Auto Scaling group) operations using the developer preview version of the AWS SDK for Rust.

Use Auto Scaling group enables to treat a collection of Amazon EC2 instances as a logical grouping for the purposes of automatic scaling and management.

## Code examples

- [Create an autoscaling group](src/bin/create-autoscaling-group.rs) (CreateAutoscalingGroup)
- [Delete an autoscaling group](src/bin/delete-autoscaling-group.rs) (DeleteAutoscalingGroup)
- [List your autscaling groups](src/bin/list-autoscaling-groups.rs) (DescribeAutoscalingGroups)
- [Update an autoscaling group](src/bin/update-autoscaling-group.rs) (UpdateAutoscalingGroup) 

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

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-autoscaling-group

This example creates an Auto Scaling group with an initial EC2 instance in the Region.

`cargo run --bin create-autoscaling-group -- -a AUTOSCALING-NAME -i INSTANCE-ID [-r REGION] [-v]`

- _AUTOSCALING-NAME_ is the name of the Auto Scaling group.
- _INSTANCE-ID_ is the ID of the EC2 instance to add to the Auto Scaling group.
- _REGION_ is name of the Region where the stacks are located.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-autoscaling-group

This example deletes an Auto Scaling group in the Region.

`cargo run --bin delete-autoscaling-group -- -a AUTOSCALING-NAME [-f] [-r REGION] [-v]`

- _AUTOSCALING-NAME_ is the name of the Auto Scaling group.
- __-f__ forces the deletion.
- _REGION_ is name of the Region where the stacks are located.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-autoscaling-groups

This example lists your Amazon EC2 Auto Scaling groups in the Region.

`cargo run --bin list-autoscaling-groups -- [-r REGION] [-v]`

- _REGION_ is name of the Region where the stacks are located.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### update-autoscaling-group

This example updates an Auto Scaling group in the Region to a new maximum size.

`cargo run --bin update-autoscaling-group -- -a AUTOSCALING-NAME -m MAXIMUM-SiZE [-r REGION] [-v]`

- _AUTOSCALING-NAME_ is the name of the Auto Scaling group.
- _MAXIMUM-SiZE_ is the mazimum size of the Auto Scaling group.
- _REGION_ is name of the Region where the stacks are located.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Auto Scaling grep](https://docs.rs/aws-sdk-autoscaling)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0