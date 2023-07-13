# Feedback Sentiment Analyzer - Design
This is our design decision document. It's meant for record keeping purposes and not educational ones.

## Inspiration

[This guide](https://github.com/awsdocs/amazon-translate-developer-guide/blob/master/README.md) is being taken down from GitHub. We wanted to give it new life as a code example journey.

> Create content on the many ways that developers who know how to use API’s can leverage ML in their projects without any need of ML knowledge.


## User Persona/Stories

L’Hôtel Paris is a fictional hotel of high repute in Paris, France. The hotel maintains a database of customer feedback. A large portion of the feedback comes from handwritten comment cards left by guests. Elise in guest services is responsible for transcribing these comment cards into the database. She translates the comments to French, copies them over to the database, and assigns a ‘sentiment’ value to each comment.She likes to read out the most positive comments into a recording device and offer them for the hotel staff to listen to every morning.

Elise has received a promotion and the budget necessary to automate this portion of her job. She reached out to us and asked for the following features:

- Scan the comment cards instead of manually transcribing them
- Translate each comment automatically from any language to French.
- Automatically assess the sentiment of each comment (e.g. “positive” or “negative”)
- Synthesize a natural-sounding human speech MP3 for each comment
- Display a list of recordings from the last day that the staff can listen to

## Why French?

Because Elise and her staff are French! This comes from the original dataset being in French. When creating example applications, the goal is to show as real-world examples as possible. While the final application did not end up using additional training, the initial user personal and journey stuck. Varying the output language is left as an exercise for the reader.

### Client Interface

Elros. It’s there and it’s easy.

The interface uses CloudScape’s card layout to present the uploaded images in a card, with an audio player overlay to listen to the translated feedback.

Users can log in via the top bar to allow uploading new images.
[Image: Screenshot 2023-05-10 at 2.19.10 PM.png]

## Infrastructure

![architecture](./architecture.png)

### A note on CDK

Whichever of the below architecture options we pick, it will all be deployed with the CDK.
https://aws.amazon.com/blogs/networking-and-content-delivery/using-amazon-cloudfront-with-aws-lambda-as-origin-to-accelerate-your-web-applications/

Single Stack: with our last example, we embedded CloudFront details in an .env file for a vite bundle in a second stack. We could instead “inject” this at run time by having a lambda serve `/env.js` which assigns the constants to `window`, then in the config section of the app, use `GLOBAL = window.GLOBAL ?? process.env.GLOBAL ?? 'default';` . To make this work, we need 1. a Lambda (doesn’t need to be in every language) that builds a javascript file from its environment variables. 2. add that lambda’s function URL as an origin to the CloudFront distribution. 3. set a behavior in the distribution to use that origin for `env.js`, and ensure that the response has `content-type: application/javascript`.

This worked, but causes our automated security scans to blow up because of a lambda function open to the public.

We opted to shift our architecture to put cloudfront in front of api gateway, and create an additional endpoint for the env script. This allowed us to lock down the lambda. In either case, this architecture created a circular dependency (appClient depends on distribution depends on api depends on env lambda depends on appClient). To resolve this dependency we set up the env lambda to use the SDK to get the app client id from Cognito rather than have CDK pass the app client id as an environment variable.

### Client Bug Fixes

We used a fork of the same client from the previous example. That client had a bug where it would not log out correctly if an unauthorized error was returned. This has been fixed.

### Analysis

Of the options, SageMaker and Step Functions are the most qualified. Due to the uncertain future of SageMaker, let's opt for Step Functions which fits with chaining together AWS services in workflows.

### Full analysis

https://github.com/awsdocs/aws-doc-sdk-examples/issues/4807
[Image: Screenshot 2023-05-23 at 3.36.42 PM.png]

> I'd say Step Functions is still the go-to for orchestrating workflows that cut across AI services... Other ML options like SageMaker Pipelines tend to have too specific a focus. SFn has great integrations & flexibility, and samples already available out there for a lot of AI services individually, maybe just not combined together. - thewsey@

The client interface is supported by infrastructure components. The majority of these components are serverless AI services that will be defined logical workflows. These workflows will be implemented using [AWS Step Functions](https://aws.amazon.com/step-functions), which provides easy service integrations and built-in scalability and resilience. Full option analysis in [GitHub issue 4807](https://github.com/awsdocs/aws-doc-sdk-examples/issues/4807).

### Lambda Bundles - One vs. Many

Lambda function deployment is divided between for each language. The dynamic languages generally prefer to have a single code bundle, and choose between handlers using the `handler` function configuration. Compiled languages lean towards having a binary asset per Lambda function. CDK is flexible and allows either approach.

### Image Upload

S3 pre-signed URL VS raw data VS direct proxy to s3 bucket. Sending raw data requires [additional configuration](https://aws.amazon.com/blogs/compute/binary-support-for-api-integrations-with-amazon-api-gateway/) and there are [simpler options](https://aws.amazon.com/blogs/compute/patterns-for-building-an-api-to-upload-files-to-amazon-s3/) for sending something to S3. We opted to try the direct proxy to the S3 bucket to avoid needing a lambda to generate the pre-signed URL. It’s also cool. But, it means that the client is responsible for file naming. Uploading an object with the same key will overwrite the original object, and the pipeline will re-run for the new data. This isn’t a deal-breaker for the app, but does imply that Elise and her staff will need to manage file naming outside of this application.

## Model training

The original example has additional model training, but it was pretty contrived. It assigned a 0 for negative sentiment and a 1 for positive sentiment to a dataset of 500,000 tweets. That data was then used to train comprehend. But, comprehend already does sentiment analysis to a higher level of detail than that, so the additional training was for educational purposes only and not realistic. With the “I’m a developer, get me out of here” signal as a foundational goal for our projects, we cut the additional model training.

## Simple step function pipeline

There is one decision point in the pipeline, when deciding to continue translating based on the sentiment of the analyzed comment. With this relatively linear approach, a Choice node evaluates the detected sentiment. Each function in the pipeline creates its output, and works on only the output field of the prior input. The first Lambda, ExtractText, works on the S3 PutObject event bridge event, and the final pair of steps pluck information from all four to create the final entry in the DynamoDB table.

Step Function transitions are untyped and schemaless. Tasks and Lambda functions must know the exact properties and shape of incoming JSON, and must write with equal care their output for the next task. InputPath and ResultPath, as well as Parameters, are a failure if parsed incorrectly, but are only evaluated at runtime.

### Tricky bits

Binary media input and output types to get raw data in and out of s3.
