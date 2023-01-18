# Amazon Keyspaces code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for Amazon Keyspaces (for Apache Cassandra).

Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra–compatible database service. 

## ⚠️ Important
* Running this code might result in charges to your AWS account. See [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

- [Hello Amazon Keyspaces](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/HelloKeyspaces.kt) (listKeyspaces command)

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (createKeyspace command)
- [Create a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (createTable command)
- [Delete a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (deleteKeyspace command)
- [Delete a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (deleteTable command)
- [Get data about a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (getKeyspace command)
- [Get data about a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (getTable command)
- [List keyspaces](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (listKeyspacesPaginator command)
- [List tables in a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (listTablesPaginator command)
- [Restore a table to a point in time](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (restoreTable command)
- [Update a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (updateTable command)


### Scenarios 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with keyspaces and tables](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/keyspaces/src/main/kotlin/com/example/keyspace/ScenarioKeyspaces.kt) (multiple commands)

## Run the examples

### Prerequisites

To run these examples, set up your development environment to use Gradle. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 

 These examples requires a **cassandra_truststore.jks** file to make a connection to Amazon Keyspaces.
 For more information, see [Using a Cassandra Java client driver to access Amazon Keyspaces programmatically](https://docs.aws.amazon.com/keyspaces/latest/devguide/using_java_driver.html). 

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

 ## Test the examples
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Kotlin code example for Amazon Keyspaces by running a test file named **KeyspaceTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

To successfully run the JUnit tests, define the following values in the test:

- **fileName** - The name of the JSON file that contains movie data. (Get this file from the GitHub repo at resources/sample_file.)
- **keyspaceName** - The name of the keyspace to create.

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).
* [Developer Guide - Amazon Keyspaces (for Apache Cassandra)](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html).
* [Interface KeyspacesClient](https://sdk.amazonaws.com/kotlin/api/latest/keyspaces/aws.sdk.kotlin.services.keyspaces/-keyspaces-client/index.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0


