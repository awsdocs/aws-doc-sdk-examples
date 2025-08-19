# Amazon Comprehend code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Comprehend.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Comprehend is a natural language processing (NLP) service that uses machine learning to find insights and relationships in text._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.

### Get started

- [Hello Amazon Comprehend](src/bin/hello.rs#L25) (`DetectDominantLanguage`)

### Single actions

Code excerpts that show you how to call individual service functions.

- [DetectDominantLanguage](src/bin/detect-language.rs#L18) (`DetectDominantLanguage`)
- [DetectEntities](src/bin/detect-entities.rs#L18) (`DetectEntities`)
- [DetectSentiment](src/bin/detect-sentiment.rs#L18) (`DetectSentiment`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with Amazon Comprehend](src/bin/getting-started.rs) (`DetectDominantLanguage`, `DetectEntities`, `DetectKeyPhrases`, `DetectSentiment`, `DetectPiiEntities`, `DetectSyntax`)

## Run the examples

### Instructions

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Comprehend

This example shows you how to get started using Amazon Comprehend.

```
cargo run --bin hello
```

#### Get started with Amazon Comprehend

This example shows you how to do the following:

- Detect the dominant language in text.
- Extract entities from text.
- Detect key phrases in text.
- Analyze sentiment in text.
- Detect personally identifiable information (PII) in text.
- Analyze syntax in text.

```
cargo run --bin getting-started
```

#### Detect dominant language

This example shows you how to detect the dominant language in text.

```
cargo run --bin detect-language
```

You can also specify custom text:

```
cargo run --bin detect-language -- --text "Bonjour, comment allez-vous?"
```

#### Detect entities

This example shows you how to detect entities in text.

```
cargo run --bin detect-entities
```

You can also specify custom text and language:

```
cargo run --bin detect-entities -- --text "John works at Amazon in Seattle" --language-code "en"
```

#### Detect sentiment

This example shows you how to detect sentiment in text.

```
cargo run --bin detect-sentiment
```

You can also specify custom text and language:

```
cargo run --bin detect-sentiment -- --text "I love this product!" --language-code "en"
```

### Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../README.md#Tests) in the `rustv1` folder.

<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Comprehend Developer Guide](https://docs.aws.amazon.com/comprehend/latest/dg/what-is.html)
- [Amazon Comprehend API Reference](https://docs.aws.amazon.com/comprehend/latest/APIReference/Welcome.html)
- [SDK for Rust Amazon Comprehend reference](https://docs.rs/aws-sdk-comprehend/latest/aws_sdk_comprehend/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0