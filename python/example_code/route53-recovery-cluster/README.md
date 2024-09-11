# Route 53 ARC code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Route 53 Application Recovery Controller.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Route 53 ARC gives you insights about whether your applications and resources are ready for recovery, and helps you move traffic across AWS Regions or away from Availability Zones for application disaster recovery._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [GetRoutingControlState](routing_control_states.py#L37)
- [UpdateRoutingControlState](routing_control_states.py#L66)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
Run this example at a command prompt with the following command.

```commandline
python routing_control_state.py [routing_control_arn] [cluster_endpoints_json_file]
```

#### Example arguments

A routing control Amazon Resource Name (ARN) looks something like this:

`arn:aws:route53-recovery-control::123456789012:controlpanel/ffa374e10db34a90bc56EXAMPLE/routingcontrol/60649aEXAMPLE`

The Region within the cluster endpoint and the Region you provide with that endpoint
must match. A cluster endpoints JSON looks something like this:

```json
{"ClusterEndpoints":
    [{"Endpoint": "https://11111111.route53-recovery-cluster.us-east-1.amazonaws.com/v1",
      "Region": "us-east-1"},
     {"Endpoint": "https://22222222.route53-recovery-cluster.ap-northeast-1.amazonaws.com/v1",
      "Region": "ap-northeast-1"},
     {"Endpoint": "https://33333333.route53-recovery-cluster.ap-southeast-2.amazonaws.com/v1",
      "Region": "ap-southeast-2"},
     {"Endpoint": "https://44444444.route53-recovery-cluster.us-west-2.amazonaws.com/v1",
      "Region": "us-west-2"},
     {"Endpoint": "https://55555555.route53-recovery-cluster.eu-west-1.amazonaws.com/v1",
      "Region": "eu-west-1"}]}
```
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Route 53 ARC Developer Guide](https://docs.aws.amazon.com/r53recovery/latest/dg/what-is-route53-recovery.html)
- [Route 53 ARC API Reference](https://docs.aws.amazon.com/routing-control/latest/APIReference/Welcome.html)
- [SDK for Python Route 53 ARC reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/route53-recovery-cluster.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0