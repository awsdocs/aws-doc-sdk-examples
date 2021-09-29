# AWS SDK for Rust croos-service code example for Amazon Polly, Amazon S3, and Amazon Transcribe

Amazon Polly is a Text-to-Speech (TTS) cloud service that converts text into lifelike speech.
Use Amazon Simple Storage Service (Amazon S3) to store and retrieve any amount of data using highly scalable, reliable, fast, and inexpensive data storage.
Amazon Transcribe provides transcription services for your audio files.

## Purpose

This example demonstrate how to use Amazon Polly, Amazon S3, and Amazon Transcribe operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### telephone

This example synthesizes a plain text (UTF-8) input file to an audio file, converts that audio file to text, and displays the text.

`cargo run -- -f FILENAME -b BUCKET -j JOB-NAME  [-r REGION] [-v]`

- __FILENAME__ is the name of the input file.
  The output is saved in MP3 format in a file with the same basename, but with an __mp3__ extension.
- __BUCKET__ is the Amazon S3 bucket to which the MP3 file is uploaded.
- __JOB-NAME__ is the unique name of the job.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
