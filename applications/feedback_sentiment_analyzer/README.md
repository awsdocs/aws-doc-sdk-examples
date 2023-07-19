# Feedback Sentiment Analyzer (FSA)

Feedback Sentiment Analyzer (FSA) is an example application that showcases AWS services and SDKs.

## What it does
Specifically, this application solves a ficticious use case of a hotel in Paris, France, which receives feedback from guests via comment cards in a variety of foreign languages.

These comment cards are:
* uploaded through a web client
* transformed using a suite of machine learning services,
* and rendered through the same web client

## SDK implementations
This application has been implemented in the following AWS SDKs. Choose your language to explore, download, deploy, and run:
* [Ruby](../../ruby/cross-services/feedback-sentiment-analyzer/README.md)

## AWS services used

This application uses a suite of [machine learning services on AWS](https://aws.amazon.com/machine-learning/) to:
* extract text using [Amazon Textract](https://aws.amazon.com/textract/).
* detect sentiment using [Amazon Comprehend](https://aws.amazon.com/comprehend/).
* translate to French using [Amazon Translate](https://aws.amazon.com/translate/).
* synthesize to human-like speech using [Amazon Polly](https://aws.amazon.com/polly/).

Additionally, the application showcases:
* [Amazon S3](https://aws.amazon.com/s3/) to store images of comment cards
* [EventBridge](https://aws.amazon.com/eventbridge/) to relay events from Amazon S3
* [AWS Lambda](https://aws.amazon.com/lambda/) to execute business logic
* [Step Functions](https://aws.amazon.com/stepfunctions/) to orchestrate multiple Lambda functions 
* [Amazon DynamoDB](https://aws.amazon.com/dynamodb/) to store details about each comment
* [API Gateway](https://aws.amazon.com/apigw/) to route requests from frontend to backend
* [CloudFront](https://aws.amazon.com/cloudfront/) to distribute this application globally
* [Cognito](https://aws.amazon.com/cognito) to authenticate users

## Deployment instructions
This application is deployed using the [AWS Cloud Development Kit (CDK)](https://aws.amazon.com/cdk/).

1. Run `cd cdk`
1. Run `npm install`
1. Export the following variables:
    ```bash
    FSA_NAME=foo
    FSA_EMAIL=your@email.com
    FSA_LANG=bar
    ```
1. Run `cdk deploy`

## Application usage
Once deployed, observe the `Output` in your terminal session.
Copy the CloudFront distribution URL, which will have `websiteurl` in the name.
Paste this URL into a browser session and begin uploading images representing comments.

