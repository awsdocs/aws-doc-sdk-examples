# AWS SDK for Rust code examples for Amazon Polly

## Purpose

These examples demonstrate how to perform several Amazon Polly operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### describe-voices

This example describes the voices in the region.

`cargo run -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### list-lexicons

This example lists the lexicons in the region.

`cargo run -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### put-lexicon

This example adds a pronunciation lexicon to the region.

`cargo run -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### synthesize-speech

This example reads a text file and creates an MP3 file with the text synthesized into speech by Amazon Polly.

`cargo run -- -f FILENAME [-d DEFAULT-REGION] [-v]`

- _FILENAME_ is name of the file containing the text to synthesize.
  The MP3 output is saved in a file with the same basename and a ".MP3" extension.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
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
