# AWS SDK for Rust code examples for Amazon Transcribe

Amazon Transcribe provides transcription services for audio files.

## Purpose

This example demonstrate how to perform an Amazon Transcribe operation using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### transcribestreaming

This example displays a transcription of a WAV audio file.

` cargo run -- -a AUDIO-FILE [-r REGION] [-v]`

- _AUDIO-FILE_ is the name of the audio file to transcribe. It must be in WAV format; the example converts the WAV file content to __pcm__ format for Amazon Transcribe.
  Note that Amazon Transcribe supports encoding in __pcm__, __ogg-opus__, and __flac__ formats.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

If you run it with the WAV file in __.audio/hello-transcribe-8000.wav__, you should see the following transcribed text:

```
Good day to you transcribe.
This is Polly talking to you from the Rust ST K.
```

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
