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

- [Listing the available Bedrock foundation models](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/bedrock/src/main/java/com/example/bedrock/ListFoundationModels.java) (ListFoundationModels command)

## Run the examples

To run these examples, set up your development environment. For more information,
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

## Additional resources

* [Amazon Bedrock - User Guide](https://docs.aws.amazon.com/bedrock/latest/userguide)
* [Amazon Bedrock - API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference)
* [AWS SDK for Java - Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)
* [AWS SDK for Java - Amazon Bedrock API reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/bedrock/package-summary.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0