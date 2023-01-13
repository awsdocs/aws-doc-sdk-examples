# Amazon Keyspaces code examples for the SDK for .NET

## Overview
*High-level description of the purpose of the folder*

Amazon Keyspaces (for Apache Cassandra) is a scalable, highly available, and
managed Apache Cassandra–compatible database service. With Amazon Keyspaces,
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
* [Update a table](Actions/KeyspacesWrapper.cs) (`RestoreTableAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Get started with keyspaces and tables](Scenarios/KeyspacesBasics.cs)

## Run the examples

### Prerequisites
*If there are language-level prerequisites that apply to **all** examples, put them in the language level README [(see template)](https://github.com/awsdocs/aws-doc-sdk-examples/wiki/Language-level-README-template), and link to it as one of the prerequisites for the service level.* **<-- Delete this sentence from template**

### Instructions

*Minimum instructions required to run examples. This varies from language to language.* **<-- Delete this sentence from template**

## Tests
⚠️ Running the tests might result in charges to your AWS account.

*Minimum instructions required to run tests.* **<-- Delete this sentence from template**

## Additional resources
* [Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/?icmpid=docs_homepage_databases)
* [Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
* [Keyspaces .NET SDK Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Keyspaces/NKeyspaces.html) 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
