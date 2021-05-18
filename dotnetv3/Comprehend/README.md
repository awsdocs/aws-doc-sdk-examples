# AWS SDK for .NET Comprehend Examples

## Purpose

The examples in this section how to use the AWS SDK for .NET with Amazon
Comprehend to inspect documents and discover information about them.

* Detect elements of a document, such as languages used, key phrases, and personally
identifiable information (PII).
* Detect common themes in a set of documents without the need for prior annotation.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- .NET Core 5.0 or later
- AWS SDK for .NET 3.5 or later

## Examples

| Project | Description |
|---------|-------------|
| DetectDominantLanguageExample | Examines the supplied text and determines the dominant language used. |
| DetectEntitiesExample | Examines the supplied text and identifies entities such as names of people, organizations, events, dates, products, places, quantities, and titles. |
| DetectingPIIExample | Examines the suppolied text and identifies personally identifiable information (PII) such as name, address, or credit card number. |
| DetectingSyntaxExample | Examines the supplied text and identifies parts of speech such as nouns, verbs, and other parts of speech. |
| DetectKeyPhrasesExample | Examines the supplied text and identifies Key Phrases included in the text. A key phrase is a string containing a noun phrase that describes a particular thing.  |
| DetectSentimentExample | Examines the supplied text and determines the overall sentiment expressed by the text. Sentiment can be positive, neutral, negative, or mixed. |
| TopicModelingExample | Examines the supplied text, looking for Topics such  |

## Additional information

- [Amazon Comprehend documentation](https://docs.aws.amazon.com/comprehend/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
