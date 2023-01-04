# Amazon Keyspaces code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) examples for Amazon Keyspaces.

Amazon Keyspaces (for Apache Cassandra) is a scalable, highly available, and managed Apache Cassandra–compatible database service. 

## ⚠️ Important
* Running this code might result in charges to your AWS account. See [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Get started

- [Hello Amazon Keyspaces](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/HelloKeyspaces.java) (listKeyspaces command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Create a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (createKeyspace command)
- [Create a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (createTable command)
- [Delete a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (deleteKeyspace command)
- [Delete a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (deleteTable command)
- [Get data about a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (getKeyspace command)
- [Get data about a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (getTable command)
- [List keyspaces](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (listKeyspacesPaginator command)
- [List tables in a keyspace](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (listTablesPaginator command)
- [Restore a table to a point in time](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (restoreTable command)
- [Update a table](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (updateTable command)


### Scenario 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with keyspaces and tables](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (multiple commands)

## Run the examples

### Prerequisites

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 

 These examples requires a **cassandra_truststore.jks** file to make a connection to Amazon Keyspaces.
 For more information, see [Using a Cassandra Java client driver to access Amazon Keyspaces programmatically](https://docs.aws.amazon.com/keyspaces/latest/devguide/using_java_driver.html). 

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

 ## Test the examples
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Java code example for Amazon Keyspaces by running a test file named **KeyspaceTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

To successfully run the JUnit tests, define the following values in the test:

- **fileName** - The name of the JSON file that contains movie data. (Get this file from the GitHub repo at resources/sample_file.)
- **keyspaceName** - The name of the keyspace to create.

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - Amazon Keyspaces (for Apache Cassandra)](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html).
* [Interface KeyspacesClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/keyspaces/KeyspacesClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

