# Bedrock code examples for the SDK for Java

## Overview
Shows how to use the AWS SDK for Java 2.x to work with Amazon Bedrock.

Amazon Bedrock is a fully managed service that offers a choice of high-performing foundation models (FMs) from leading AI companies like AI21 Labs, Anthropic, Cohere, Meta, Stability AI, and Amazon with a single API, along with a broad set of capabilities you need to build generative AI applications, simplifying development while maintaining privacy and security.

## ⚠️ Important
* The SDK for Java examples performs AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single actions

The following example uses the **BedrockClient** object:

- [Listing the available Bedrock foundation models](./src/main/java/com/example/bedrock/ListFoundationModels.java) (ListFoundationModels command)

## Run the examples

To run these examples, set up your development environment. For more information,
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

## Testing the Amazon Bedrock Java files

You can test the Java code examples for Amazon Bedrock by running a test file named **APIGatewayTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail.

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

### Command line
To run the JUnit tests from the command line, you can use the following command:

		mvn test

You will see output from the JUnit tests, as shown here.

    [INFO] --------------------------------------------------
	[INFO]  T E S T S
    [INFO] --------------------------------------------------
	[INFO] Running BedrockTest
    ...
	Test 1 passed
	...
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	[INFO] --------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] --------------------------------------------------
	[INFO] Total time:  3.589 s
	[INFO] Finished at: 2023-11-09T15:41:02+01:00
	[INFO] --------------------------------------------------

## Additional resources

* [Amazon Bedrock - User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide)
* [Amazon Bedrock - API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference)
* [AWS SDK for Java - Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
* [AWS SDK for Java - Amazon Bedrock API reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/bedrock/package-summary.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0