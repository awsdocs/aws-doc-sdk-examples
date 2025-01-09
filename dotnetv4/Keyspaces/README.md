# Amazon Keyspaces code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Keyspaces (for Apache Cassandra).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra-compatible database service._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Keyspaces](Actions/HelloKeyspaces.cs#L4) (`ListKeyspaces`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/Usings.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateKeyspace](Actions/KeyspacesWrapper.cs#L23)
- [CreateTable](Actions/KeyspacesWrapper.cs#L39)
- [DeleteKeyspace](Actions/KeyspacesWrapper.cs#L63)
- [DeleteTable](Actions/KeyspacesWrapper.cs#L78)
- [GetKeyspace](Actions/KeyspacesWrapper.cs#L94)
- [GetTable](Actions/KeyspacesWrapper.cs#L109)
- [ListKeyspaces](Actions/KeyspacesWrapper.cs#L125)
- [ListTables](Actions/KeyspacesWrapper.cs#L144)
- [RestoreTable](Actions/KeyspacesWrapper.cs#L163)
- [UpdateTable](Actions/KeyspacesWrapper.cs#L188)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Keyspaces

This example shows you how to get started using Amazon Keyspaces.


#### Learn the basics

This example shows you how to do the following:

- Create a keyspace and table. The table schema holds movie data and has point-in-time recovery enabled.
- Connect to the keyspace using a secure TLS connection with SigV4 authentication.
- Query the table. Add, retrieve, and update movie data.
- Update the table. Add a column to track watched movies.
- Restore the table to its previous state and clean up resources.

<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.end-->


<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html)
- [Amazon Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon Keyspaces reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Keyspaces/NKeyspaces.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0