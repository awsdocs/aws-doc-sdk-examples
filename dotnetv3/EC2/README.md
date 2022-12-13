# Amazon EC2 code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Elastic Compute Cloud (Amazon EC2) to manage custom metrics and alarms.

Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Create a security group](CreateSecurityGroupExample/CreateSecurityGroupExample/CreateSecurityGroup.cs) (`CreateSecurityGroupAsync`)
* [Create a security key pair](CreateKeyPairExample/CreateKeyPairExample/CreateKeyPair.cs) (`CreateKeyPairAsync`)
* [Create a VPC](VirtualPrivateCloudExamples/CreateVPCExample/CreateVPC.cs) (`CreateVpcAsync`)
* [Create a VPC Endpoint to use with an S3 Client](VirtualPrivateCloudExamples/CreateVPCforS3Example/CreateVPCforS3.cs) (`CreateVpcAsync`)
* [Delete a security group](DeleteSecurityGroupExample/DeleteSecurityGroupExample/DeleteSecurityGroup.cs) (`DeleteSecurityGroupAsync`)
* [Delete a security key pair](DeleteKeyPairExample/DeleteKeyPairExample/DeleteKeyPair.cs) (`DeleteKeyPairAsync`)
* [Delete a VPC](DeleteVPCExample/DeleteVPCExample/DeleteVPC.cs) (`DeleteVpcAsync`)
* [Describe instances](DescribeInstancesExample/DescribeInstancesExample/DescribeInstances.cs) (`DescribeInstancesAsync`)
* [Reboot an instance](RebootInstancesExample/RebootInstancesExample/RebootInstances.cs) (`RebootInstancesAsync`)
* [Start an instance](StartInstancesExample/StartInstancesExample/StartInstances.cs) (`StartInstancesAsync`)
* [Stop an instance](StopInstancesExample/StopInstancesExample/StopInstances.cs) (`StopInstancesAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon EC2 User Guide for Linux](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
* [Amazon EC2 User Guide for Windows](https://docs.aws.amazon.com/AWSEC2/latest/WindowsGuide/concepts.html)
* [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon EC2](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/NEC2.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
