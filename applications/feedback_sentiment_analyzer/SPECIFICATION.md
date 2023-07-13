# FSA technical specification

This document contains the technical specifications for Feedback Sentiment Analyzer (FSA), a sample application that showcases AWS services and SDKs.

It explains:

- Application inputs and outputs
- Underlying AWS components and their configurations
- Key cross-service integration details

### Table of Contents

- [Inputs](#inputs)
- [Outputs](#outputs)
- [Diagram](#diagram)
- [Input routing](#input-routing)
- [Step Function configuration](#step-function-configuration)
  - [State machine Lambda functions](#state-machine-lambda-functions)
    - [Diagram](#diagram-1)
    * [Function inputs and outputs](#function-inputs-and-outputs)
      - [ExtractText](#extracttext)
      - [AnalyzeSentiment](#analyzesentiment)
      - [TranslateText](#translatetext)
      - [SynthesizeAudio](#synthesizeaudio)
- [GetFeedback Lambda function](#getfeedback-lambda-function-1)
- [Processing S3 events with EventBridge](#processing-s3-events-with-eventbridge)
- [Managing items in DynamoDB](#managing-items-in-dynamodb)
- [Other FSA material](#other-fsa-material)

---

## Inputs

This application receives three inputs from the frontend.

- authenticate user (uses `Cognito`)
- load page (uses `DynamoDB:GetItems`)
- upload image to S3 (`S3:PutObject`)

## Outputs

This application produces two outputs from the backend:

- put new item in the database (uses `DynamoDB:PutItem`)
- put new synthesized audio to S3 (uses `S3:PutObject`)

## Diagram

![img_1.png](img_1.png)

---

## HTTP API specification

All of the APIs are created by the CDK script. The endpoints are common to every language variation and do not need any additional implementation.

### media

<details>
<summary><strong>PUT /api/media/{item}</strong></summary>

Create or update an object in Amazon S3. Creating or updating an image file will trigger the Step Function.

**Parameters**

item - the object key of the item to add or update

**Request body**

JavaScript [File](https://developer.mozilla.org/en-US/docs/Web/API/File) object

**Response body**

Empty

</details>

<details>
<summary><strong>GET /api/media/{item}</strong></summary>

Get an object from Amazon S3.

**Parameters**

item - the object key of the item to get

**Request body**

Empty

**Response body**

Empty

</details>

### feedback

<details>
<summary><strong>GET /api/feedback</strong></summary>

Get the translated text, sentiment, and audio/image keys for an uploaded image. This data comes from Amazon DynamoDB. The database table is filled as a result of running the step function.

**Parameters**

Empty

**Request body**

Empty

**Response body**

```
{
  "feedback": [
    {
      "sentiment": "POSITIVE",
      "text": "J'adore cet hôtel!",
      "audioUrl": "PXL_20230710_182358532.jpg.mp3",
      "imageUrl": "PXL_20230710_182358532.jpg"
    }
  ]
}
```

</details>

### env

<details>
<summary><strong>GET /api/env</strong></summary>

Get the environment variables required to connect to a Cognito hosted UI. The frontend calls this automatically to facilitate sign in.

**Parameters**

Empty

**Request body**

Empty

**Response body**

```
{
  "COGNITO_SIGN_IN_URL": "https://...",
  "COGNITO_SIGN_OUT_URL": "https://..."
}
```

</details>

## Step Function configuration

When an image is uploaded through `Upload image` input route, a Step Function state machine is triggered.

See [Event processing](#event-processing)
This state machine orchestrates a series of AWS Lambda functions.

### State machine Lambda functions

| trigger                      | function                              | action                                 |
| ---------------------------- | ------------------------------------- | -------------------------------------- |
| S3:CreateObject event.       | [ExtractText](#ExtractText)           | Extracts text from S3 object.          |
| `ExtractText` complete.      | [AnalyzeSentiment](#AnalyzeSentiment) | Detect positive or negative sentiment. |
| `AnalyzeSentiment` complete. | [TranslateText](#TranslateText)       | Translate text to French.              |
| `TranslateText` complete.    | [SynthesizeAudio](#SynthesizeAudio)   | Synthesize human-like audio from text. |

#### Diagram

These functions appear by name in the multi-state workflow depicted below.

Also depicted is where the workflow forks at `ContinueIfPositive` based whether sentiment analyzed is `POSITIVE` or `NEGATIVE`.

When ran successfully, the state machine adds a new item to a DynamDB table containing data gathered during the workflow.

![FSA - l'hotellerie.png](..%2F..%2F..%2F..%2F..%2FDownloads%2FFSA%20-%20l%27hotellerie.png)

Logging appears in the AWS Console under Step Functions and (for Lambda functions only) CloudWatch.

## Function inputs and outputs

See below for the required inputs and outputs of each Lambda function.

### ExtractText

#### **Input**

Function will use data available on the [S3 event object](https://docs.aws.amazon.com/AmazonS3/latest/userguide/EventBridge.html).

For example:

```json
{
  "bucket": "my-s3-bucket",
  "region": "us-east-1",
  "object": "foo/bar.png"
}
```

#### **Output**

Function will return a string representing the text extracted.

For example:

```json
THIS HOTEL WAS GREAT
```

### AnalyzeSentiment

#### **Input**

Function will use data available on the [Lambda event object](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-concepts.html#gettingstarted-concepts-event).

For example:

```json
{
  "source_text": "THIS HOTEL WAS GREAT",
  "region": "us-east-1"
}
```

#### **Output**

Function will return a string representing the text extracted.

For example:

```json
{
  "sentiment": "POSITIVE",
  "language_code": "en"
}
```

### TranslateText

#### **Input**

Function will use data available on the [Lambda event object](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-concepts.html#gettingstarted-concepts-event).

For example:

```json
{
    "source_language_code": "en"
    "region": "us-east-1",
    "extracted_text": "THIS HOTEL WAS GREAT"
}
```

#### **Output**

Function will return a string representing the text translated.

For example:

```json
CET HOTEL ÉTAIT RAVISSANT
```

### SynthesizeAudio

#### **Input**

Function will use data available on the [Lambda event object](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-concepts.html#gettingstarted-concepts-event).

For example:

```json
{
  "bucket": "my-s3-bucket",
  "translated_text": "CET HOTEL ÉTAIT RAVISSANT",
  "region": "us-east-1",
  "object": "comment.png"
}
```

#### **Output**

Function will return a string representing the key of the newly-synthesized audio file.

For example:

```json
my-s3-bucket/audio.mp3
```

---

## GetFeedback Lambda function

To display data on previously-uploaded images, the frontend invokes a Lambda function which fetches items from a [DynamoDB table](#DynamoDB).

There is no input.

### **Output**

Function will return a JSON object `feedback` containing items. Each item will contain translated text and a link to its corresponding synthesized audio file.

For example:

```json
{
  "feedback": [
    {
      "sentiment": "POSITIVE",
      "text": "J'adore cet hôtel/",
      "audioUrl": "PXL_20230710_182358532.jpg.mp3",
      "imageUrl": "PXL_20230710_182358532.jpg"
    }
  ]
}
```

---

## Processing S3 events with EventBridge

This application relies on an [EventBridge rule](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-rules.html), which triggers the [Step Functions state machine](#step-function-configuration) when new images are uploaded to S3 by the frontend.

Specifically, the trigger is scoped to `ObjectCreated` events emitted by `my-s3-bucket`:

```json
eventPattern: {
        source: ["aws.s3"],
        detailType: ["Object Created"],
        detail: {
          bucket: {
            name: [<dynamic media bucket name>],
          },
          object: {
            key: [{ suffix: ".png" }, { suffix: ".jpeg" }, { suffix: ".jpg" }],
          },
        },
      }
```

---

## Managing items in DynamoDB

This application relies on an Amazon DynamoDB table using the schema detailed below.

| key               | purpose                                   | attribute | value                  |
| ----------------- | ----------------------------------------- | --------- | ---------------------- |
| `comment_key`     | Key of the scanned image.                 | `S`       | S3 object key          |
| `source_text`     | Extracted text from image.                | `S`       | Extracted text         |
| `sentiment`       | Comprehend sentiment score.               | `S`       | Comprehend JSON object |
| `source_language` | The language detected from the Text.      | `S`       | Language code          |
| `translated_text` | French version of 'text'.                 | `S`       | Translated text        |
| `audio_key`       | Key of the audio file generated by Polly. | `S`       | S3 object key          |

---

# Other FSA material

If technical details are not what you seek, try these instead:

- [High-level summary](README.md)
- [Deployment and development instructions](DEVELOPMENT.md)
- [Opinionated architecture overview](DESIGN.md)
