# Amazon EC2 code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Elastic
Compute Cloud (Amazon EC2).

*Amazon EC2 is a web service that provides resizable computing capacity that you use to 
build and host your software systems.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Amazon EC2](hello.py)

### Single actions

Code excerpts that show you how to call individual service functions.

* [Allocate an Elastic IP address](elastic_ip.py)
(`AllocateAddress`)
* [Associate an Elastic IP address with an instance](elastic_ip.py)
(`AssociateAddress`)
* [Create a security group](security_group.py)
(`CreateSecurityGroup`)
* [Create a security key pair](key_pair.py)
(`CreateKeyPair`)
* [Create and run an instance](instance.py)
(`RunInstances`)
* [Delete a security group](security_group.py)
(`DeleteSecurityGroup`)
* [Delete a security key pair](key_pair.py)
(`DeleteKeyPair`)
* [Describe instances](instance.py)
(`DescribeInstances`)
* [Disassociate an Elastic IP address from an instance](elastic_ip.py)
(`DisassociateAddress`)
* [Get data about Amazon Machine Images](instance.py)
(`DescribeImages`)
* [Get data about a security group](security_group.py)
(`DescribeSecurityGroups`)
* [Get data about instance types](instance.py)
(`DescribeInstanceTypes`)
* [List security key pairs](key_pair.py)
(`DescribeKeyPairs`)
* [Release an Elastic IP address](elastic_ip.py)
(`ReleaseAddress`)
* [Set inbound rules for a security group](security_group.py)
(`AuthorizeSecurityGroupIngress`)
* [Start an instance](instance.py)
(`StartInstances`)
* [Stop an instance](instance.py)
(`StopInstances`)
* [Terminate an instance](instance.py)
(`TerminateInstances`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with instances](scenario_get_started_instances.py)

## Run the examples

### Prerequisites

To find prerequisites for running these examples, see the
[README](../../README.md#Prerequisites) in the Python folder.

* An SSH client, such as Open SSH, can be used to connect to example instances.

### Instructions

#### Get started with instances

Run an interactive example that shows you how to create, manage, and connect to 
instances by running the following at a command prompt:

```
python scenario_get_started_instances.py
```

## Tests

⚠️ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../README.md#Tests)
in the Python folder.

## Additional resources

* [Amazon EC2 User Guide for Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
* [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
* [SDK for Python Amazon EC2 client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ec2.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
