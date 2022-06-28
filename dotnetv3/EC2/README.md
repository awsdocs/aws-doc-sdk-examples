# Amazon EC2 code examples for the AWS SDK for .NET v3

## Overview

The examples in this section how to use the AWS SDK for .NET with Amazon Elastic Compute Cloud (Amazon EC2).

Amazon EC2 provides secure, resizable compute capacity in the cloud.

## ⚠️ Important

- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide. 
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).
- Running this code might result in charges to your AWS account.

## Code examples

### Single actions

- [Creating a key pair](CreateKeyPairExample/)
- [Creating a security group](CreateSecurityGroupExample/)
- [Creating a VPC](CreateVPCExample/)
- [Deleting a key pair](DeleteKeyPairExample/)
- [Deleting a security group](DeleteSecurityGroupExample/)
- [Deleting a VPC](DeleteVPCExample/)
- [Describing instances](DescribeInstancesExample/)
- [Rebooting instances](RebootInstancesExample/)
- [Starting instances](StartInstancesExample/)
- [Stopping instances](StopInstancesExample/)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

## Additional resources

- [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
- [AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

