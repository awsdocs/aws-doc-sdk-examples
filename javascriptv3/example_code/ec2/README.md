# Amazon EC2 code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Elastic Compute Cloud (Amazon EC2).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](hello.js#L8) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Allocate an Elastic IP address](actions/allocate-address.js#L8) (`AllocateAddress`)
- [Associate an Elastic IP address with an instance](actions/associate-address.js#L8) (`AssociateAddress`)
- [Create a launch template](../cross-services/wkflw-resilient-service/steps-deploy.js#L280) (`CreateLaunchTemplate`)
- [Create a security group](actions/create-security-group.js#L8) (`CreateSecurityGroup`)
- [Create a security key pair](actions/create-key-pair.js#L8) (`CreateKeyPair`)
- [Create and run an instance](actions/run-instances.js#L8) (`RunInstances`)
- [Delete a launch template](../cross-services/wkflw-resilient-service/steps-destroy.js#L244) (`DeleteLaunchTemplate`)
- [Delete a security group](actions/delete-security-group.js#L8) (`DeleteSecurityGroup`)
- [Delete a security key pair](actions/delete-key-pair.js#L8) (`DeleteKeyPair`)
- [Describe Regions](actions/describe-regions.js#L8) (`DescribeRegions`)
- [Describe instances](actions/describe-instances.js#L8) (`DescribeInstances`)
- [Disable detailed monitoring](actions/unmonitor-instances.js#L8) (`UnmonitorInstances`)
- [Disassociate an Elastic IP address from an instance](actions/disassociate-address.js#L8) (`DisassociateAddress`)
- [Enable monitoring](actions/monitor-instances.js#L8) (`MonitorInstances`)
- [Get data about Amazon Machine Images](actions/describe-images.js#L8) (`DescribeImages`)
- [Get data about a security group](actions/describe-security-groups.js#L8) (`DescribeSecurityGroups`)
- [Get data about instance types](actions/describe-instance-types.js#L8) (`DescribeInstanceTypes`)
- [Get data about the instance profile associated with an instance](../cross-services/wkflw-resilient-service/steps-demo.js#L243) (`DescribeIamInstanceProfileAssociations`)
- [Get details about Elastic IP addresses](actions/describe-addresses.js#L8) (`DescribeAddresses`)
- [Get the default VPC](../cross-services/wkflw-resilient-service/steps-deploy.js#L359) (`DescribeVpcs`)
- [Get the default subnets for a VPC](../cross-services/wkflw-resilient-service/steps-deploy.js#L374) (`DescribeSubnets`)
- [List security key pairs](actions/describe-key-pairs.js#L8) (`DescribeKeyPairs`)
- [Reboot an instance](actions/reboot-instances.js#L8) (`RebootInstances`)
- [Release an Elastic IP address](actions/release-address.js#L8) (`ReleaseAddress`)
- [Replace the instance profile associated with an instance](../cross-services/wkflw-resilient-service/steps-demo.js#L255) (`ReplaceIamInstanceProfileAssociation`)
- [Set inbound rules for a security group](actions/authorize-security-group-ingress.js#L8) (`AuthorizeSecurityGroupIngress`)
- [Start an instance](actions/start-instances.js#L8) (`StartInstances`)
- [Stop an instance](actions/stop-instances.js#L8) (`StopInstances`)
- [Terminate an instance](actions/terminate-instances.js#L8) (`TerminateInstances`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](javascriptv3/example_code/cross-services/wkflw-resilient-service/index.js)
- [Get started with instances](scenarios/basic.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**
Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.

```bash
node ./hello.js
```


#### Build and manage a resilient service

This example shows you how to create a load-balanced web service that returns book, movie, and song recommendations. The example shows how the service responds to failures, and how to restructure the service for more resilience when failures occur.

- Use an Amazon EC2 Auto Scaling group to create Amazon Elastic Compute Cloud (Amazon EC2) instances based on a launch template and to keep the number of instances in a specified range.
- Handle and distribute HTTP requests with Elastic Load Balancing.
- Monitor the health of instances in an Auto Scaling group and forward requests only to healthy instances.
- Run a Python web server on each EC2 instance to handle HTTP requests. The web server responds with recommendations and health checks.
- Simulate a recommendation service with an Amazon DynamoDB table.
- Control web server response to requests and health checks by updating AWS Systems Manager parameters.

<!--custom.scenario_prereqs.cross_ResilientService.start-->
<!--custom.scenario_prereqs.cross_ResilientService.end-->


<!--custom.scenarios.cross_ResilientService.start-->
<!--custom.scenarios.cross_ResilientService.end-->

#### Get started with instances

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.scenario_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.scenario_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.scenarios.ec2_Scenario_GetStartedInstances.start-->
<!--custom.scenarios.ec2_Scenario_GetStartedInstances.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) Amazon EC2 reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/ec2)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0