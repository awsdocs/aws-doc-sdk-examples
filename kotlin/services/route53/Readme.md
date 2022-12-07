# Route 53 code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for Amazon Route 53.

Route 53 is a highly available and scalable Domain Name System (DNS) web service. Route 53 connects user requests to internet applications running on AWS or on-premises.

## ⚠️ Important
* Running this code might result in charges to your AWS account. See [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

- [Hello Route 53 domain registration](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/HelloRoute53.kt) (listPrices command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Check domain availability](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (checkDomainAvailability command)
- [Check domain transferability](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (checkDomainTransferability command)
- [Create a health check](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/CreateHealthCheck.kt) (createHealthCheck command)
- [Create a hosted zone](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/CreateHostedZone.kt) (createHostedZone command)
- [Delete a health check](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/DeleteHealthCheck.kt) (deleteHealthCheck command)
- [Delete a hosted zone](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/DeleteHostedZone.kt) (deleteHostedZone command)
- [Get domain details](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (getDomainDetail command)
- [Get the status of a specific health check](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/GetHealthCheckStatus.kt) (getHealthCheckStatus command)
- [Get operation details](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (getOperationDetail command)
- [List current domains](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (listOperationsPaginator command)
- [List health checks](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/ListHealthChecks.kt) (listHealthChecks command)
- [List hosted zones](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/ListHostedZones.kt) (listHostedZones command)
- [Request a domain registration](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (registerDomain command)
- [Update a health check](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/UpdateHealthCheck.kt) (updateHealthCheck command)

### Scenario 

- [Get started with domains](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/route53/src/main/kotlin/com/kotlin/route/Route53Scenario.kt) (various commands)

## Run the Amazon Route 53 Kotlin files

**IMPORTANT**

The Kotlin examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a hosted zone. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 

 ## Test the Amazon Route 53 Kotlin files
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Kotlin code example for Amazon Route 53 by running a test file named **Route53Test**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. If you do not define all values, the JUnit tests fail.

Define this value to successfully run the JUnit tests:

- **domainName** - The fully qualified domain name.
- **domainSuggestionSc** - The domain suggestion (for example, findmy.accountants).
- **domainTypeSc** - The domain type (for example, com).
- **phoneNumerSc** - The phone number to use (for example, +91.9966564xxx).
- **emailSc** - The email address to use.
- **firstNameSc** - The first name to use.
- **lastNameSc** - The first name to use.
- **citySc** - The city to use.

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/home.html).
* [Developer Guide - Amazon Route 53](https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/Welcome.html).
* [route53domains](https://sdk.amazonaws.com/kotlin/api/latest/route53domains/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
