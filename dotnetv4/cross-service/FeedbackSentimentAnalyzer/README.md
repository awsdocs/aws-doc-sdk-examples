# Create Feedback Sentiment Analyzer (FSA) using the SDK for .NET (v3)

## Overview

This document discusses the language-specific nuances of deploying the Feedback Sentiment Analyzer (FSA) application. For more details, see the application [README.md](../../../applications/feedback_sentiment_analyzer/README.md).

## Prerequisites

- Follow the main [README](../../README.md#Prerequisites) in the `dotnetv3` folder
- Install the [AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html)

### Important

- The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.
- Be sure to delete all the resources you create while going through this tutorial so that you won't be charged.

### .NET Implementation Details
 - This example includes AWS Lambda functions for the various operations of the Feedback Sentiment Analyzer.
 - Each function was created using the AWS Lambda Template from the [AWS Toolkit for Visual Studio](https://aws.amazon.com/visualstudio/).
 - Each function also uses [Powertools for AWS Lambda (.NET)](https://github.com/aws-powertools/powertools-lambda-dotnet) for enhanced logging.