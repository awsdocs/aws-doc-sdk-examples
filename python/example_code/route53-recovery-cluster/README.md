# Amazon Route 53 Application Recovery Controller code examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Route 53 Application 
Recovery Controller to manage routing control states.

*Application Recovery Controller improves application availability, including by 
centrally coordinating failovers within an AWS Region or across multiple Regions.*

## Code examples

### API examples

* [Get the state of a routing control](routing_control_states.py)
(`GetRoutingControlState`)
* [Update the state of a routing control](routing_control_states.py)
(`UpdateRoutingControlState`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.18.50 or later

### Command

Run this example at a command prompt with the following command.

```commandline
python routing_control_state.py [routing_control_arn] [cluster_endpoints_json_file]
``` 

#### Example arguments

A routing control ARN looks something like this:

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

## Additional information

- [Boto3 Amazon Route 53 Application Recovery Controller service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/route53-recovery-cluster.html)
- [Amazon Route 53 Application Recovery Controller documentation](https://docs.aws.amazon.com/route53)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
