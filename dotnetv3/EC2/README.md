// snippet-start:# Amazon EC2 code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon Elastic Compute Cloud (Amazon EC2) to manage custom metrics and alarms.

Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Getting started
* [Hello Amazon EC2](actions/HelloEc2.cs)

### Single actions
Code excerpts that show you how to call individual service functions.
* [AllocateAddress](actions/EC2Wrapper.cs) (`AllocateAddressAsync`)
* [AssociateAddress](actions/EC2Wrapper.cs) (`AssociateAddressAsync`)
* [AuthorizeSecurityGroupIngress](actions/EC2Wrapper.cs) (`AuthorizeSecurityGroupIngressAsync`)
* [Create a security group](actions/EC2Wrapper.cs) (`CreateSecurityGroupAsync`)
* [Create a security key pair](actions/EC2Wrapper.cs) (`CreateKeyPairAsync`)
* [Create a VPC](actions/EC2Wrapper.cs) (`CreateVpcAsync`)
* [Create a VPC endpoint to use with an Amazon Simple Storage Service (Amazon S3) client.](VirtualPrivateCloudExamples/CreateVPCforS3Example/CreateVPCforS3.cs) (`CreateVpcAsync`)
* [Delete a security group](actions/EC2Wrapper.cs) (`DeleteSecurityGroupAsync`)
* [Delete a security key pair](actions/EC2Wrapper.cs) (`DeleteKeyPairAsync`)
* [Delete a VPC](actions/EC2Wrapper.cs) (`DeleteVpcAsync`)
* [DescribeImages](actions/EC2Wrapper.cs) (`DescribeImagesAsync`)
* [Describe instances](actions/EC2Wrapper.cs) (`DescribeInstancesAsync`)
* [DescribeInstances](actions/EC2Wrapper.cs) (`DescribeInstancesAsync`)
* [DescribeInstanceTypes](actions/EC2Wrapper.cs) (`DescribeInstanceTypesAsync`)
* [DescribeKeyPairs](actions/EC2Wrapper.cs) (`DescribeKeyPairsAsync`)
* [DescribeSecurityGroups](actions/EC2Wrapper.cs) (`DescribeSecurityGroupsAsync`)
* [DisassociateAddress](actions/EC2Wrapper.cs) (`DisassociateAddressAsync`)
* [Reboot an instance](actions/EC2Wrapper.cs) (`RebootInstancesAsync`)
* [ReleaseAddress](actions/EC2Wrapper.cs)
* [RunInstances](actions/EC2Wrapper.cs)
* [Start an instance](actions/EC2Wrapper.cs) (`StartInstancesAsync`)
* [StartInstance](actions/EC2Wrapper.cs)
* [Stop an instance](actions/EC2Wrapper.cs) (`StopInstancesAsync`)
* [StopInstance](actions/EC2Wrapper.cs)
* [TerminateInstances](actions/EC2Wrapper.cs)

### Scenarios
Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.
* [Get started with Instances](scenarios/EC2_Basics/EC2Basics.cs)

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
