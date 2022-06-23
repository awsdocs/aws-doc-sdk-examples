# AWS SDK for Rust cross-service code examples 

## Purpose

These code examples demonstrate how to perform two or more service operations using the developer preview version of the AWS SDK for Rust.

Amazon Simple Storage Service (Amazon S3) is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

Amazon Rekognition makes it easy to add image and video analysis to your applications.

Amazon DynamoDB (DynamoDB) is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability.

Amazon Polly is a Text-to-Speech (TTS) cloud service that converts text into lifelike speech.

Amazon Transcribe provides transcription services for your audio files.

## Code examples

### Detect faces (detect_faces/)

- [Save file to bucket](detect_faces/src/main.rs) (Amazon S3 PutObject)
- [Display face information](detect_faces/src/main.rs) (Amazon Rekognition DetectFaces)

### Detect labels (detect_labels/)

- [Add file to bucket](detect_labels/src/main.rs) (Amazon S3 PutObject)
- [Get EXIF data](detect_labels/src/main.rs)
- [Get label data](detect_labels/src/main.rs) (Amazon Rekognition DetectLabels)
- [Add data to table](detect_labels/src/main.rs) (DynamoDB PutItem)

### Telephone (telephone/)

- [Convert text to speech](telephone/src/main.rs) (Amazon Polly SynthesizeSpeech)
- [Save file to bucket](telephone/src/main.rs) (Amazon S3 PutObject)
- [Convert audio to text](telephone/src/main.rs) (Amazon Transcribe GetTranscriptionJob and StartTranscriptionJob)

## ⚠ Important

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### detect_faces

This code example:
- Saves the image in an Amazon Simple Storage Service (Amazon S3) bucket with an "uploads/" prefix.
- Displays facial details age range, gender, and emotion (smiling, etc.).

```
cd detect_faces; 
cargo run -- -b BUCKET -f FILENAME [-r REGION] [-v]
```

- _BUCKET_ is the name of the Amazon S3 bucket where the JPG, JPEG, or PNG file is uploaded.
- _FILENAME_ is the name of the file to upload.
  It must have a __jpg__, __jpeg__, or __png__ file extension.  
- _REGION_ is the name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### detect_labels

This code example:
- Gets EXIF information from a JPG, JPEG, or PNG file.
- Uploads the file to an Amazon S3 bucket.
- Uses Amazon Rekognition to identify the three top attributes (labels in Amazon Rekognition) in the file.
- Adds the EXIF and label information to an Amazon DynamoDB table.

```
cd detect_labels; 
cargo run -- -b BUCKET -f FILENAME -t TABLE [-r REGION] [-v]
```

- _BUCKET_ is the name of the Amazon S3 bucket where the JPG, JPEG, or PNG file is uploaded.
- _FILENAME_ is the name of the file to upload.
  It must have a __jpg__, __jpeg__, or __png__ file extension.
- _TABLE_ is the DynamoDB table in which the EXIF and label information is stored.
  It must use the primary key __filename__.
- _REGION_ is the name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### telephone

This example synthesizes a plain text (UTF-8) input file to an audio file, converts that audio file to text, and displays the text.

```
cd telephone
cargo run -- -f FILENAME -b BUCKET -j JOB-NAME  [-r REGION] [-v]
```

- __FILENAME__ is the name of the input file.
  The output is saved in MP3 format in a file with the same basename, but with an __mp3__ extension.
- __BUCKET__ is the Amazon S3 bucket to which the MP3 file is uploaded.
- __JOB-NAME__ is the unique name of the job.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon DynamoDB](https://docs.rs/aws-sdk-dynamodb)
- [AWS SDK for Rust API Reference for Amazon Polly](https://docs.rs/aws-sdk-polly)
- [AWS SDK for Rust API Reference for Amazon Rekognition](https://docs.rs/aws-sdk-rekognition)
- [AWS SDK for Rust API Reference for Amazon S3](https://docs.rs/aws-sdk-s3)
- [AWS SDK for Rust API Reference for Amazon Transcribe](https://docs.rs/aws-sdk-transcribe)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
