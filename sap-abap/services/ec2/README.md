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

- [AllocateAddress](zcl_aws1_ec2_actions.clas.abap#L98)
- [AssociateAddress](zcl_aws1_ec2_actions.clas.abap#L116)
- [CreateKeyPair](zcl_aws1_ec2_actions.clas.abap#L175)
- [CreateSecurityGroup](zcl_aws1_ec2_actions.clas.abap#L193)
- [DeleteKeyPair](zcl_aws1_ec2_actions.clas.abap#L215)
- [DeleteSecurityGroup](zcl_aws1_ec2_actions.clas.abap#L233)
- [DescribeAddresses](zcl_aws1_ec2_actions.clas.abap#L251)
- [DescribeAvailabilityZones](zcl_aws1_ec2_actions.clas.abap#L270)
- [DescribeInstances](zcl_aws1_ec2_actions.clas.abap#L291)
- [DescribeKeyPairs](zcl_aws1_ec2_actions.clas.abap#L323)
- [DescribeRegions](zcl_aws1_ec2_actions.clas.abap#L342)
- [DescribeSecurityGroups](zcl_aws1_ec2_actions.clas.abap#L362)
- [MonitorInstances](zcl_aws1_ec2_actions.clas.abap#L383)
- [RebootInstances](zcl_aws1_ec2_actions.clas.abap#L423)
- [ReleaseAddress](zcl_aws1_ec2_actions.clas.abap#L462)
- [RunInstances](zcl_aws1_ec2_actions.clas.abap#L137)
- [StartInstances](zcl_aws1_ec2_actions.clas.abap#L480)
- [StopInstances](zcl_aws1_ec2_actions.clas.abap#L520)


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