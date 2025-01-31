# Amazon EC2 code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Elastic Compute Cloud (Amazon EC2).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](hello.js) (`DescribeSecurityGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/steps.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](actions/allocate-address.js)
- [AssociateAddress](actions/associate-address.js#L4)
- [AuthorizeSecurityGroupIngress](actions/authorize-security-group-ingress.js#L4)
- [CreateKeyPair](actions/create-key-pair.js#L4)
- [CreateLaunchTemplate](../cross-services/wkflw-resilient-service/steps-deploy.js#L279)
- [CreateSecurityGroup](actions/create-security-group.js#L4)
- [DeleteKeyPair](actions/delete-key-pair.js#L4)
- [DeleteLaunchTemplate](../cross-services/wkflw-resilient-service/steps-destroy.js#L261)
- [DeleteSecurityGroup](actions/delete-security-group.js#L4)
- [DescribeAddresses](actions/describe-addresses.js#L4)
- [DescribeIamInstanceProfileAssociations](../cross-services/wkflw-resilient-service/steps-demo.js#L241)
- [DescribeImages](actions/describe-images.js#L4)
- [DescribeInstanceTypes](actions/describe-instance-types.js#L4)
- [DescribeInstances](actions/describe-instances.js#L4)
- [DescribeKeyPairs](actions/describe-key-pairs.js#L4)
- [DescribeRegions](actions/describe-regions.js#L4)
- [DescribeSecurityGroups](actions/describe-security-groups.js#L4)
- [DescribeSubnets](../cross-services/wkflw-resilient-service/steps-deploy.js#L373)
- [DescribeVpcs](../cross-services/wkflw-resilient-service/steps-deploy.js#L358)
- [DisassociateAddress](actions/disassociate-address.js#L4)
- [MonitorInstances](actions/monitor-instances.js#L4)
- [RebootInstances](actions/reboot-instances.js#L4)
- [ReleaseAddress](actions/release-address.js#L4)
- [ReplaceIamInstanceProfileAssociation](../cross-services/wkflw-resilient-service/steps-demo.js#L253)
- [RunInstances](actions/run-instances.js#L4)
- [StartInstances](actions/start-instances.js#L4)
- [StopInstances](actions/stop-instances.js#L4)
- [TerminateInstances](actions/terminate-instances.js#L4)
- [UnmonitorInstances](actions/unmonitor-instances.js#L4)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../cross-services/wkflw-resilient-service/index.js)


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

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.

```bash
node ./hello.js
```

#### Learn the basics

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.basics.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basics.ec2_Scenario_GetStartedInstances.end-->


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