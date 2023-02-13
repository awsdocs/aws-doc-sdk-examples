# Amazon Keyspaces code examples for the SDK for .NET

## Overview
The examples in this folder perform Amazon Keyspaces (for Apache Cassandra)
actions using the AWS SDK for .NET.

Amazon Keyspaces is a scalable, highly available, and managed Apache
Cassandra–compatible database service. With Amazon Keyspaces,
you can run your Cassandra workloads on AWS using the same Cassandra
application code and developer tools that you use today. 

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello Keyspaces](Actions/HelloKeyspaces.cs)

### Single actions
Code excerpts that show you how to call individual service functions.
* [Create a keyapce](Actions/KeyspacesWrapper.cs) (`CreateKeyspaceAsync`)
* [Create a table](Actions/KeyspacesWrapper.cs) (`CreateTableAsync`)
* [Delete a keyspace](Actions/KeyspacesWrapper.cs) (`DeleteKeyspaceAsync`)
* [Get data about a keyspace](Actions/KeyspacesWrapper) (`GetKeyspaceAsync`)
* [Get data about a table](Actions/KeyspacesWrapper.cs) (`GetTableAsync`)
* [List keyspaces](Actions/KeyspacesWrapper.cs) (`ListKeyspacesAsync`)
* [List tables in a keyspace](Actions/KeyspacesWrapper.cs) (`ListTablesAsync`)
* [Restore a table to a point in time](Actions/KeyspacesWrapper.cs) (`RestoreTableAsync`)
* [Update a table](Actions/KeyspacesWrapper.cs) (`UpdateTableAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Get started with keyspaces and tables](Scenarios/KeyspacesBasics.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

### Instructions
The examples in this folder use the default user account. The call to
initialize the client object does not specify the AWS Region. The following
example shows how to supply the AWS Region to match your own as a
parameter to the client constructor:

```
var client = new AmazonKinesisClient(Amazon.RegionEndpoint.USWest2);
```

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests

⚠ Running the tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.

## Additional resources
* [Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/?icmpid=docs_homepage_databases)
* [Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
* [Keyspaces .NET SDK Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Keyspaces/NKeyspaces.html) 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
