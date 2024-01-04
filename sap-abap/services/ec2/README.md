# Amazon EC2 code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Allocate an Elastic IP address](zcl_aws1_ec2_actions.clas.abap#L101) (`AllocateAddress`)
- [Associate an Elastic IP address with an instance](zcl_aws1_ec2_actions.clas.abap#L119) (`AssociateAddress`)
- [Create a security group](zcl_aws1_ec2_actions.clas.abap#L196) (`CreateSecurityGroup`)
- [Create a security key pair](zcl_aws1_ec2_actions.clas.abap#L178) (`CreateKeyPair`)
- [Create and run an instance](zcl_aws1_ec2_actions.clas.abap#L140) (`RunInstances`)
- [Delete a security group](zcl_aws1_ec2_actions.clas.abap#L236) (`DeleteSecurityGroup`)
- [Delete a security key pair](zcl_aws1_ec2_actions.clas.abap#L218) (`DeleteKeyPair`)
- [Describe Availability Zones](zcl_aws1_ec2_actions.clas.abap#L273) (`DescribeAvailabilityZones`)
- [Describe Regions](zcl_aws1_ec2_actions.clas.abap#L345) (`DescribeRegions`)
- [Describe instances](zcl_aws1_ec2_actions.clas.abap#L294) (`DescribeInstances`)
- [Enable monitoring](zcl_aws1_ec2_actions.clas.abap#L386) (`MonitorInstances`)
- [Get data about a security group](zcl_aws1_ec2_actions.clas.abap#L365) (`DescribeSecurityGroups`)
- [Get details about Elastic IP addresses](zcl_aws1_ec2_actions.clas.abap#L254) (`DescribeAddresses`)
- [List security key pairs](zcl_aws1_ec2_actions.clas.abap#L326) (`DescribeKeyPairs`)
- [Reboot an instance](zcl_aws1_ec2_actions.clas.abap#L426) (`RebootInstances`)
- [Release an Elastic IP address](zcl_aws1_ec2_actions.clas.abap#L465) (`ReleaseAddress`)
- [Start an instance](zcl_aws1_ec2_actions.clas.abap#L483) (`StartInstances`)
- [Stop an instance](zcl_aws1_ec2_actions.clas.abap#L523) (`StopInstances`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP Amazon EC2 reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/ec2/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0