# AWS SDK for Rust code examples for Amazon Polly

## Purpose

These examples demonstrate how to perform several Amazon Polly operations using the developer preview version of the AWS SDK for Rust.

Amazon Polly is a Text-to-Speech (TTS) cloud service that converts text into lifelike speech.

## Code examples

- [Lists the available voices](src/bin/describe-voices.rs) (DescribeVoices)
- [Lists the available lexicons](src/bin/list-lexicons.rs) (ListLexicons)
- [Lists the available voices and language](src/bin/polly-helloworld.rs) (DescribeVoices)
- [Creates a lexicon](src/bin/put-lexicon.rs) (PutLexicon)
- [Create speech from text](src/bin/synthesize-speech.rs) (SynthesizeSpeech)
- [Create speech from text and return a presigned URI](src/bin/synthesize-speech-presigned.rs) (SynthesizeSpeech)

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

### describe-voices

This example describes the voices in the Region.

`cargo run --bin describe-voices -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-lexicons

This example lists the lexicons in the Region.

`cargo run --bin list-lexicons -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### polly-helloworld

This example displays a list of the voices and their language, and those supporting a neural engine, in the Region.

`cargo run --bin polly-helloworld -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### put-lexicon

This example adds a pronunciation lexicon to the Region.

`cargo run --bin put-lexicon -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### synthesize-speech

This example reads a text file and creates an MP3 file with the text synthesized into speech by Amazon Polly.

`cargo run --bin synthesize-speech -- -f FILENAME [-r REGION] [-v]`

- _FILENAME_ is name of the file containing the text to synthesize.
  The MP3 output is saved in a file with the same basename and a ".MP3" extension.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### synthesize-speech-presigned.rs) (SynthesizeSpeech)

This example reads a text file, creates an MP3 file with the text synthesized into speech by Amazon Polly, and returns a public URI to access the MP3 file.

`cargo run --bin synthesize-speech-presigned -- -f FILENAME [-e EXPIRES-IN] [-r REGION] [-v]`

- _FILENAME_ is name of the file containing the text to synthesize.
  The MP3 output is saved in a file with the same basename and an ".MP3" extension.
- _EXPIRES-IN_ is the number of seconds the URI is valid.
  If not supplied, this defaults to 900 (15 minutes).
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon Polly](https://docs.rs/aws-sdk-polly)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
