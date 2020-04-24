# Getting started with the AWS SDK for .NET code example

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0


## Purpose

  This example verifies that you can connect to your AWS account and perform
  simple read operations on your AWS objects. This example reads the names
  of your Amazon RDS DB instances and your Amazon S3 buckets. If you don't
  have any DB instances or buckets, the example still connects to your AWS
  account and reports what it can't find.

  This code example uses .NET Core 3.1 to create a cross-platform console application.


## Prerequisites

To build and run this example, you need the following:

- The AWS SDK for .NET. For more information, see the [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html).

- AWS credentials and a default AWS Region. If you have the AWS CLI installed, you can specify them in a local AWS config file such as C:\Users\username\.aws\config, and an AWS credentials file such as C:\Users\username\.aws\credentials. For more information, see the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/overview.html) or the [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config-creds.html).

- The AWSSDK.RDS package. This is already referenced in the solution.

- The AWSSDK.S3 package. This is already referenced in the solution.


## Running the code

  1. Open the solution in Visual Studio.
  2. To build the solution, choose **Build**, **Build Solution**.
  3. To run the code, choose **Debug**, **Start Debugging**.


## Running the tests

  1. Open the solution in Visual Studio.
  2. (Optional) To open the Test Explorer window, choose **Test**, **Test Explorer**.
  3. To run the tests, choose **Test**, **Run All Tests**.


## Running the code from the .NET Core command-line interface

  1. Open a command window.
  2. Navigate to the code project directory.
  3. Type **dotnet run** and press enter.


## Running the tests from the .NET Core command-line interface

  1. Open a command window.
  2. Navigate to the test project directory.
  3. Type **dotnet test** and press enter.




## Additional information

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.

- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services) on the AWS website. 

- Running this code might result in charges to your AWS account.
