# Amazon MemoryDB for Redis code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) examples for Amazon MemoryDB for Redis.

Amazon MemoryDB for Redis is a Redis-compatible, durable, in-memory database service that delivers ultra-fast performance.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

The following examples use the **MemoryDbClient** object:

- [Creating a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/CreateCluster.java) (createCluster command)
- [Creating a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/CreateSnapshot.java) (createSnapshot command)
- [Deleting a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (deleteCluster command)
- [Describing a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeClusters command)
- [Describing a snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeSnapshots command)
- [Describing a specific cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/memorydb/src/main/java/com/example/memorydb/DeleteCluster.java) (describeClusters command)


## Running the Amazon MemoryDB for Redis Java files

Some of these examples perform *destructive* operations on AWS resources, such as deleting a cluster. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Testing the Amazon MemoryDB for Redis Java files

You can test the Java code examples forAmazon MemoryDB for Redis Java by running a test file named **MemoryDBTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **clusterName** - The name of the cluster.   
- **nodeType** - The compute and memory capacity of the nodes in the cluster.
- **subnetGroupName** - The name of the subnet group to be used for the cluster.
- **aclName** - The name of the Access Control List to associate with the cluster.
- **snapShotName** - The name of the snapshot.

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - Amazon MemoryDB for Redis](https://docs.aws.amazon.com/memorydb/latest/devguide/getting-started.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

