# Feedback Sentiment Analyzer (FSA)

Feedback Sentiment Analyzer (FSA) is an example application that showcases AWS services and SDKs.

Specifically, it solves a ficticious use case of a hotel in Paris, France, which receives feedback from guests in a variety of foreign languages.

## SDKs this application is implemented in
The underlying business logic has been implemented in the following AWS SDKs:
* [Ruby](../../ruby/cross-services/feedback-sentiment-analyzer/README.md)


## AWS services used

This application uses a suite of [machine learning services on AWS](https://aws.amazon.com/machine-learning/) to:
* extract text using [Amazon Textract](https://aws.amazon.com/textract/).
* detect sentiment using [Amazon Comprehend](https://aws.amazon.com/comprehend/).
* translate to French using [Amazon Translate](https://aws.amazon.com/translate/).
* synthesize to human-like speech using [Amazon Polly](https://aws.amazon.com/polly/).

Additionally, the application showcases:
* [Amazon S3]() to store images of comment cards
* [AWS Lambda]() to execute business logic
* [Step Functions]() to orchestrate multiple Lambda functions 
* [Amazon DynamoDB]() to store details about each comment

## Deployment instructions
This application is deployed using the [AWS Cloud Development Kit (CDK)](https://aws.amazon.com/cdk/).

1. `cd cdk`
1. Run `npm i`
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
