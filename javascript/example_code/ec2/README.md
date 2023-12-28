# Amazon EC2 code examples for the SDK for JavaScript (v2)

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for JavaScript (v2) to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascript` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](None) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Allocate an Elastic IP address](None) (`AllocateAddress`)
- [Associate an Elastic IP address with an instance](None) (`AssociateAddress`)
- [Create a launch template](None) (`CreateLaunchTemplate`)
- [Create a security group](None) (`CreateSecurityGroup`)
- [Create a security key pair](None) (`CreateKeyPair`)
- [Create and run an instance](None) (`RunInstances`)
- [Delete a launch template](None) (`DeleteLaunchTemplate`)
- [Delete a security group](None) (`DeleteSecurityGroup`)
- [Delete a security key pair](None) (`DeleteKeyPair`)
- [Describe Regions](None) (`DescribeRegions`)
- [Describe instances](None) (`DescribeInstances`)
- [Disable detailed monitoring](None) (`UnmonitorInstances`)
- [Disassociate an Elastic IP address from an instance](None) (`DisassociateAddress`)
- [Enable monitoring](None) (`MonitorInstances`)
- [Get data about Amazon Machine Images](None) (`DescribeImages`)
- [Get data about a security group](None) (`DescribeSecurityGroups`)
- [Get data about instance types](None) (`DescribeInstanceTypes`)
- [Get data about the instance profile associated with an instance](None) (`DescribeIamInstanceProfileAssociations`)
- [Get details about Elastic IP addresses](None) (`DescribeAddresses`)
- [Get the default VPC](None) (`DescribeVpcs`)
- [Get the default subnets for a VPC](None) (`DescribeSubnets`)
- [List security key pairs](None) (`DescribeKeyPairs`)
- [Reboot an instance](None) (`RebootInstances`)
- [Release an Elastic IP address](None) (`ReleaseAddress`)
- [Replace the instance profile associated with an instance](None) (`ReplaceIamInstanceProfileAssociation`)
- [Set inbound rules for a security group](None) (`AuthorizeSecurityGroupIngress`)
- [Start an instance](None) (`StartInstances`)
- [Stop an instance](None) (`StopInstances`)
- [Terminate an instance](None) (`TerminateInstances`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascript` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v2) Amazon EC2 reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Ec2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0