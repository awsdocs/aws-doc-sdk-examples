# Feedback Sentiment Analyzer (FSA)

Feedback Sentiment Analyzer (FSA) is an example application that showcases AWS services and SDKs. Built with ❤️ for you to explore, download, and deploy.

## What it does

Specifically, this application solves a fictitious use case of a hotel in New York City that receives guest feedback on comment cards in a variety of languages.

These comment cards are uploaded through a web client, transformed using a suite of machine learning services, and rendered through the same web client. The end result contains the original image, an English translation of the text, and an audio element.

---

## SDK implementations

This application has been implemented with the following AWS SDKs.

- [Ruby](../../ruby/cross_service_examples/feedback_sentiment_analyzer/README.md)
- [JavaScript](../../javascriptv3/example_code/cross-services/feedback-sentiment-analyzer/README.md)
- [Java](../../javav2/usecases/creating_fsa_app/README.md)
- [.NET](../../dotnetv3/cross-service/FeedbackSentimentAnalyzer/README.md)

To deploy one of these implementations, follow the [Deployment instructions](#deployment-instructions).

---

## AWS services used

This application uses a suite of [AWS machine learning services](https://aws.amazon.com/machine-learning/) to do the following:

- Extract text using [Amazon Textract](https://docs.aws.amazon.com/textract/latest/dg/what-is.html)
- Detect sentiment using [Amazon Comprehend](https://docs.aws.amazon.com/comprehend/latest/dg/what-is.html)
- Translate to English using [Amazon Translate](https://docs.aws.amazon.com/translate/latest/dg/what-is.html)
- Synthesize to human-like speech using [Amazon Polly](https://docs.aws.amazon.com/polly/latest/dg/what-is.html)

Additionally, the application showcases the following AWS services:

- [Amazon Simple Storage Service (Amazon S3)](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html) - Stores images of comment cards
- [Amazon EventBridge](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-what-is.html) - Relays events from Amazon S3
- [AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html) - Executes business logic
- [AWS Step Functions](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html) - Orchestrates multiple Lambda functions
- [Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html) - Stores details about each comment
- [Amazon API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html) - Routes requests from frontend to backend
- [Amazon CloudFront](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Introduction.html) - Distributes this application globally
- [Amazon Cognito](https://docs.aws.amazon.com/cognito/latest/developerguide/what-is-amazon-cognito.html) - Authenticates users

---

## Deployment instructions

This application is deployed using the [AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/v2/guide/home.html).

1. Get [AWS credentials](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html).
1. Set the following environment variables:

   - `FSA_NAME` - Any text less than 10 characters
   - `FSA_EMAIL` - A valid email address that you own
   - `FSA_LANG` - Any of the [implemented languages](#sdk-implementations)

   For example:

   _Bash_

   ```
   export FSA_NAME=ana
   export FSA_EMAIL=ana@example.com
   export FSA_LANG=ruby
   ```

   _Windows cmd_

   ```
   set FSA_NAME=ana
   set FSA_EMAIL=ana@example.com
   set FSA_LANG=ruby
   ```

   _Windows Powershell_

   ```
   $Env:FSA_NAME = ana
   $Env:FSA_EMAIL = ana@example.com
   $Env:FSA_LANG = ruby
   ```

1. Run the following commands:
   ```
   cd cdk
   npm install
   cdk deploy
   ```
   After deploying, observe the `Output` in your terminal session.
   Copy the CloudFront distribution URL, which has `websiteurl` in the name.
   ![console output](docs/output.png)
   Paste this URL into a browser to launch the application.

---

## Application instructions

### Sign in

1. Choose `Sign in`.
2. Enter the the email from `FSA_EMAIL` into the `Username` field.
3. Enter the temporary password that was sent to you into the `Password` field.

![login form](docs/login.png)

4. Reset your password as prompted.

### Upload images

1. Signing in directs you to a landing page that says `No data found`.

![empty](docs/no-data-found.png)

2. Choose `Upload`.
3. Choose `Select a file`.

> Note: Feel free to choose a [sample comment](/comments) instead of writing your own. 4. Select a PNG or JPEG image that contains a positive comment about the hotel. Negative comments are saved, but not returned to the frontend.

![upload](docs/upload.png)

5. Choose `Upload`.

### Play the result

1. Wait. The image upload takes a minute or two to process. CloudFront will also cache API responses for five 
minutes. To speed things up, create a CloudFront invalidation.
2. Choose the `Refresh` button.

![refresh](docs/refresh.png)

3. You should see a card with the original image, translated text, and a play button.

![translated text](docs/card.png)

4. Choose the play button to listen to the translated audio.
