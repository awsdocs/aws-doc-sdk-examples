# AWS SDK for Rust code examples for Amazon EC2

## Purpose

These examples demonstrate how to perform several Amazon EC2 operations using the developer preview version of the AWS SDK for Rust.

Amazon Elastic Compute Cloud (Amazon EC2) is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems.

## Code examples

- [Lists the state of one or all of your instances](src/bin/describe-instances.rs) (DescribeInstances)
- [Lists the regions enabled for your account] (src/bin/ec2-helloworld) (ListRegions)
- [Lists the scheduled events for your instances](src/bin/list-all-instance-events.rs) (ListInstanceEvents)
- [Enables monitoring on an instance](src/bin/monitor-instance.rs) (MonitorInstance)
- [Reboots an instance](src/bin/reboot-instance.rs) (RebootInstance)
- [Starts an instance](src/bin/start-instance.rs) (StartInstance)
- [Stops an instance](src/bin/stop-instance.rs) (StopInstance)

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

### describe-instances

This example lists the state of one or all of your Amazon EC2 instances

`cargo run --bin describe-instances -- [-i INSTANCE-ID] [-d DEFAULT-REGION] [-v]`

- _INSTANCE-ID_ is the ID of an instance to describe.
  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### ec2-helloworld

This example describes the AWS Regions that are enabled for your account.

`cargo run --bin ec2-helloworld -- [-d DEFAULT-REGION] [-v]`

  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### list-all-instance-events

This example shows the scheduled events for the Amazon Elastic Compute Cloud (Amazon EC2) instances in the Region.

`cargo run --bin list-all-instance-events -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### monitor-instance

This example enables monitoring on an Amazon EC2 instance.

`cargo run --bin monitor-instance -- -i INSTANCE-ID [-d DEFAULT-REGION] [-v]`

- _INSTANCE-ID_ is the ID of an instance to monitor.
  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### reboot-instance

This example reboots an Amazon EC2 instance.

`cargo run --bin reboot-instance -- -i INSTANCE-ID [-d DEFAULT-REGION] [-v]`

- _INSTANCE-ID_ is the ID of an instance to reboot.
  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### start-instance

This example starts an Amazon EC2 instance.

`cargo run --bin start-instance -- -i INSTANCE-ID [-d DEFAULT-REGION] [-v]`

- _INSTANCE-ID_ is the ID of an instance to start.
  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### stop-instance

This example stops an Amazon EC2 instance.

`cargo run --bin stop-instance -- -i INSTANCE-ID [-d DEFAULT-REGION] [-v]`

- _INSTANCE-ID_ is the ID of an instance to stop.
  If this argument is not supplied, the state of all instances is shown.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon EC2](https://docs.rs/aws-sdk-ec2)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
