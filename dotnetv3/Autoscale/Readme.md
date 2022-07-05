# Amazon EC2 Auto Scaling code examples for the SDK for .NET

## Overview
This README discusses how to run and test the SDK for .NET (v3) examples for Amazon EC2 Auto Scaling.

With Amazon EC2 Auto Scaling, you can maintain application availability and automatically add or remove EC2 instances according to conditions.

## ⚠️ Important
* The SDK for .NET examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Scenario

- [Performing various Amazon EC2 Auto Scaling operations](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/dotnetv3/Autoscale/AutoScaleMVP/AutoScalingScenario.cs) (Multiple commands)

## Running the Amazon EC2 Auto Scaling .NET files

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Auto Scaling group. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Setting up your AWS SDK for .NET environment](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html). 

## Additional resources
* [Developer Guide - AWS SDK for .NET](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html).
* [User Guide - Amazon EC2 Auto Scaling](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
