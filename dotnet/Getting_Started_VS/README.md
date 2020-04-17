# README for code example Getting_Started_VS

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0


## Purpose

  The purpose of this example is to verify that you can connect to your AWS 
  account and perform simple read operations on your AWS objects. This example 
  reads the names of your RDS DB instances and your S3 buckets. If you don't 
  have any DB instances or buckets, the example still connects to your AWS 
  account and reports what it doesn't find.

  This code example was created by using Visual Studio 2019
  This code example uses .NET Core 3.1 to create a cross-platform application
  This code example creates a console application


## Prerequisites

To build and run this example, you need the following:

- The AWS SDK for .NET. For more information, see the [AWS SDK for .NET
Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html).

- AWS credentials and a default AWS region. If you have the AWS CLI installed, you can specify them a local AWS config file such as C:\Users\username\.aws\config, and an AWS credentials file such as C:\Users\username\.aws\credentials. For more information, see the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/overview.html).

- The AWSSDK.RDS package. This is already referenced in the solution.

- The AWSSDK.S3 package. This is already referenced in the solution.


## Running the code

  - Open the solution in Visual Studio
  - Build the solution from the **Build** menu
  - Start the solution from the **Debug** menu


## Running the tests

  - Open the solution in Visual Studio
  - (Optional) Open the **Test Explorer** window from the **Test** menu
  - Run all tests from the **Test** menu


## Additional information

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.

- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services) on the AWS website. 

- Running this code might result in charges to your AWS account.
