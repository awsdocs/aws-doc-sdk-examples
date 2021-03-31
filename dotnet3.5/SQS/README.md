<!-- Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
     SPDX - License - Identifier: Apache - 2.0 -->

# Amazon Simple Queue Service (Amazon SQS) Code Examples in C\#

## Purpose

This folder contains examples that show how to use the AWS SDK for .NET 3.x to
get started using queue and message operations in Amazon Simple Queue Service
(Amazon SQS). The examples will show you how to create a queue and send, receive,
and delete messages from it.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 
- AWS SDK for .NET 3.0 or later
- XUnit and Moq (to run unit tests)

## Cautions

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

## Running the tests

Navigate to the folder that contains the test application, CreateSendExample
for instance, and run:

```
dotnet test
```

If you want more information, run:

```
dotnet test -l "console;verbosity=detailed"
```

## Running the examples

The examples in this folder use the default user account. The call to
initialize the Amazon SQS client supplies the region. Change the region to
match your own before running the example.

Once the example has been compiled, you can run it from the commandline by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

```
dotnet run
```

Or you can execute the example from within your IDE.

| Example | Description |
|---------|-------------|
| [CreateSendExample](CreateSendExample/) | Create an SQS queue and sends a sample message. |
| [ReceiveDelete](ReceivDeleteExample) | Receives a single message from the queue and then deletes it from the queue. |

## Additional information

- [AWS SDK for .NET Documentation](https://docs.aws.amazon.com/sdk-for-net/index.html#latest-version).

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0