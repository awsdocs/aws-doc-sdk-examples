# AWS SDK for Rust code examples for AWS Config

## Purpose

These examples demonstrate how to perform several AWS Config operations using the develoer preview version of the AWS SDK for Rust.

AWS Config provides a detailed view of the resources associated with your AWS account, including how they are configured, how they are related to one another, and how the configurations and their relationships have changed over time.

## Code examples

- [Get resource configuration history](src/bin/config-helloworld.rs) (GetResourceConfigHistory)
- [Delete configuration recorder](src/bin/delete-configuration-recorder.rs) (DeleteConfigurationRecorder)
- [Delete delivery channel](src/bin/delete-delivery-channel.rs) (DeleteDeliveryChannel)
- [Enable configuration](src/bin/enable-config.rs) (DescribeConfigurationRecorders, DescribeDeliveryChannels, PutConfigurationRecorder, PutDeliveryChannel)
- [List configuration recorders](src/bin/list-configuration-recorders.rs) (DescribeConfigurationRecorders)
- [List delivery channels](src/bin/list-delivery-channels.rs) (DescribeDeliveryChannels)
- [List resources](src/bin/list-resources.rs) (ListDiscoveredResources)
- [Show resource history](src/bin/show-resource-history.rs) (GetResourceConfigHistory)

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

### config-helloworld

This example lists the configuration history for an AWS Config resource.

`cargo run --bin config-helloworld -- -resource_id RESOURCE-ID -resource_type RESOURCE-TYPE NAME [-r REGION] [-v]`

- _RESOURCE-ID_ is the ID of the AWS Config resource.
- _RESOURCE-TYPE_ is the type of the AWS Config resource, such as __AWS::EC2::SecurityGroup__.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-configuration-recorder

This example deletes an AWS Config configuration recorder.

`cargo run --bin delete-configuration-recorder -- -n NAME [-r REGION] [-v]`

- _NAME_ is the name of the configuration recorder to delete.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-delivery-channel

This example deletes an AWS Config delivery channel.

`cargo run --bin delete-delivery-channel -- -c CHANNEL [-r REGION] [-v]`

- _CHANNEL_ is the name of the channel to delete.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### enable-config

This example enables AWS Config for a resource type, in the Region.

`cargo run --bin enable-config -- -b BUCKET -i IAM-ARN -k KMS-ARN -n NAME -p PREFIX -s SNS-ARN -t TYPE [-r REGION] [-v]`

- _BUCKET_ is the name of the Amazon bucket to which AWS Config delivers configuration snapshots and configuration history files.
- _IAM-ARN_ is the ARN of the IAM role that used to describe the AWS resources associated with the account.
- _KMS-ARN_ is the ARN of the KMS key that used to encrypt the data in the bucket.
- _NAME_ is the name of the configuration.
- _PREFIX_ is the  prefix for the bucket.
- _SNS-ARN_ is the  ARN of the Amazon SNS topic to which AWS Config sends notifications about configuration changes.
- _TYPE_ is the type of resource for AWS Config to support.
  If not supplied, defaults to `AWS::DynamoDB::Table` (DynamoDB tables).
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-configuration-recorders

This example lists the AWS Config configuration recorders in the Region.

`cargo run --bin list-configuration-recorders -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-delivery-channels

This example lists the AWS Config delivery channels in the Region.

`cargo run --bin list-delivery-channels -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-resources

This example lists your AWS Config resources, by resource type, in the Region.

`cargo run --bin list-resources -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### show-resource-history

This example displays the configuration history for a resource.

`cargo run --bin show-resource-history -- -i ID --resource-type RESOURCE-TYPE [-r REGION] [-v]`

- _ID_ is the ID of the resource.
- _RESOURCE-TYPE_ is the resource type, such as `AWS::EC2::SecurityGroup`.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for AWS Config](https://docs.rs/aws-sdk-config)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)
- 
## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0