# Elastic Beanstalk code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for AWS Elastic Beanstalk.

AWS Elastic Beanstalk is an easy-to-use service for deploying and scaling web applications.

## ⚠️ Important
* The SDK for Kotlin examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing page](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **ElasticBeanstalkClient** object:

- [Creating an AWS Elastic Beanstalk application](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/elasticbeanstalk/src/main/kotlin/com/aws/example/CreateApplication.kt) (CreateApplication command)
- [Creating an AWS Elastic Beanstalk environment](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/elasticbeanstalk/src/main/kotlin/com/aws/example/CreateEnvironment.kt) (CreateEnvironment command)
- [Deleting an AWS Elastic Beanstalk application](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/aws/example/DeleteApplication.kt) (DeleteApplication command)
- [Describing an AWS Elastic Beanstalk application](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/aws/example/DescribeApplication.kt) (DescribeApplication command)
- [Describing AWS Elastic Beanstalk configuration options](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/aws/example/DescribeConfigurationOptions.kt) (DescribeConfigurationOptions command)
- [Describing an AWS Elastic Beanstalk environment](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/aws/example/DescribeEnvironment.kt) (DescribeEnvironment command)


## Running the AWS Elastic Beanstalk Kotlin files

Some of these examples perform *destructive* operations on AWS resources, such as deleting an AWS Elastic Beanstalk application. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment to use Gradle. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 


 ## Testing the AWS Elastic Beanstalk Kotlin files

You can test the Kotlin code examples for AWS Elastic Beanstalk by running a test file named **ElasticBeanstalkTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the AWS Elastic Beanstalk JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **appName** - The name of the application.   
- **envName** - The name of the environment. 

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).
* [Developer Guide - AWS Elastic Beanstalk](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/Welcome.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
