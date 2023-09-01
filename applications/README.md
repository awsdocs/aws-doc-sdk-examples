# Example applications

This directory contains a collection of production-like examples designed to show the AWS SDKs in context.

## Example contents

Every example is different, but each will have the following:

- Automatic/semi-automatic resource creation - AWS CloudFormation (AWS CFN) and AWS Cloud Development Kit (AWS CDK) provide the scaffolding for the applications.
- Language implementations - Each example will have at least one feature that is implemented in multiple languages.
- A frontend - Many examples have a frontend component.
- README.md - A high level overview of the example with instructions for deployment and usage.
- SPECIFICATION.md - The architecture and business logic of the application.
- DEVELOPMENT.md - A guide on extending the application with another language-specific implementation.
- README_TEMPLATE.md - A template for creating a language-specific README.md.
- DESIGN.md - A lightly-edited history of the decisions concerning the development of this example.

## List of examples

| Name                | Path                                         | Supported languages | Highlighted Services |
| ------------------- | -------------------------------------------- | ------------------- | -------------------- |
| Photo Asset Manager | [photo-asset-manager](./photo-asset-manager) | Java, C++           | [Amazon Rekognition](https://aws.amazon.com/rekognition/), [Amazon API Gateway](https://aws.amazon.com/api-gateway/), [Amazon CloudFront](https://aws.amazon.com/cloudfront/), [Amazon Cognito](https://aws.amazon.com/cognito/), [Amazon S3](https://aws.amazon.com/s3/) |
| Feedback Sentiment Analyzer | [feedback_sentiment_analyzer](./feedback_sentiment_analyzer/)| Ruby, JavaScript | [Amazon Textract](https://docs.aws.amazon.com/textract/latest/dg/what-is.html), [Amazon Comprehend](https://docs.aws.amazon.com/comprehend/latest/dg/what-is.html), [Amazon Translate](https://docs.aws.amazon.com/translate/latest/dg/what-is.html), [Amazon Polly](https://docs.aws.amazon.com/polly/latest/dg/what-is.html), [AWS Step Functions](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html) |
