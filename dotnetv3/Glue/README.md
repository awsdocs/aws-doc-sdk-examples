# AWS Glue code examples for the AWS SDK for .NET v3

## Overview

Examples in this folder show how to use AWS Glue with the AWS SDK for .NET.

AWS Glue is a serverless data integration service that makes it easy to 
discover, prepare, and combine data for analytics, machine learning, and
application development. AWS Glue provides all the capabilities needed for data
integration so that you can start analyzing your data and putting it to use in
minutes instead of months.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only
  the minimum permissions required to perform the task. For more information,
  see Grant least privilege.
- This code is not tested in every AWS Region. For more information, see AWS
  Regional Services.

## Code examples

### Scenario

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

- [Glue basics scenario](scenarios/Glue_Basics_Scenario/)

## Running the examples

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then issuing
the following command:

```
dotnet run
```

Alternatively, you can execute the example from within your IDE.

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the AWS Tools and SDKs Shared Configuration and
- Credentials Reference Guide.
- NET Core 6.0 or later
- AWS SDK for .NET 3.7 or later

### Tests

⚠️ Running the tests might result in charges to your AWS account.

The scenario has a test project included in the solution.

1. Naviate to the folder containing the scenario and then enter:

```
dotnet test
```

Alternatively, you can open the scenario solution and use the Visual Studio
Test Runner to execute the tests.

## Additional Resources

- [AWS Glue developer guide](https://docs.aws.amazon.com/glue/latest/dg/glue-dg.pdf)
- [AWS Glue API reference guide](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [AWS Glue API reference guide SDK for the AWS SDK for .NET](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Glue/NGlue.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0