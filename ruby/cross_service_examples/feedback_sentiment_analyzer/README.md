# Create Feedback Sentiment Analyzer (FSA) using the SDK for Ruby

## Overview

This document discusses the language-specific nuances of deploying the Feedback Sentiment Analyzer (FSA) application. For more details, see the application [README.md](/applications/feedback_sentiment_analyzer/README.md).

## Prerequisites

To complete the tutorial, you need the following:

- An AWS account
- All Ruby dependencies outlined in the [Ruby Example Code README.md](../../README.md#get-started)

### Important

- The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.
- Be sure to delete all of the resources you create while going through this tutorial so that you won't be charged.
- Also make sure to properly set up your development environment. For information, see [Get started with the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/getting-started.html).

### Resource creation

The required AWS resources are created by using an AWS Cloud Development Kit (AWS CDK) script. This is discussed later in the document. There is no need to create any resources by using the AWS Management Console.
