# AWS SDK for Rust code examples for AWS KMS

## Purpose

These examples demonstrate how to perform several AWS Key Management Service (AWS KMS) operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-key

This example creates an AWS KMS key.

`cargo run --bin create-key -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.

### decrypt

This example decrypts a string encrypted by AWS KMS key.

`cargo run --bin decrypt -- -k KEY -i INPUT-FILE [-d DEFAULT-REGION] [-v]`

- _KEY_ is the encryption key.
- _INPUT-FILE_ is the name of the file containing text encrypted by the key.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### encrypt

This example encrypts a string using an AWS KMS key.

`cargo run --bin encrypt -- -k KEY -t TEXT -o OUT-FILE [-d DEFAULT-REGION] [-v]`

- _KEY_ is the encryption key.
- _TEXT_ is the string to encrypt by the key.
- _OUT-FILE_ is the file in which the encrypted text is saved.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-data-key

This example creates a data key for client-side encryption using an AWS KMS data key.

`cargo run --bin generate-data-key -- -k KEY -t TEXT -o OUT-FILE [-d DEFAULT-REGION] [-v]`

- _KEY_ is the name of the AWS KMS data key.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-data-key-without-plaintext

This example creates a data key for client-side encryption using an AWS KMS data key,
showing the plaintext public key but not the plaintext private key.

`cargo run --bin generate-data-key-without-plaintext -- -k KEY -t TEXT -o OUT-FILE [-d DEFAULT-REGION] [-v]`

- _KEY_ is the name of the AWS KMS data key.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-random

This example creates a random byte string that is cryptographically secure.

`cargo run --bin generate-random -- -l LENGTH [-d DEFAULT-REGION] [-v]`

- _LENGTH_ is the number of bytes, which must be less than 1024.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ displays additional information.

### kms-helloworld

This example creates a random, 64-byte string that is cryptographically secure in __us-east-1__.

`cargo run --bin kms-helloworld`

### reencrypt-data

This example re-encrypts a text string that was encrypted using an AWS KMS key with another AWS KMS key.

`cargo run --bin reencrypt-data -- -f FIRST-KEY -n NEW-KEY -i INPUT-FILE -o OUT-FILE [-d DEFAULT-REGION] [-v]`

- _FIRST-KEY_ is the encryption key used to initially encrypt the text.
- _NEW-KEY_ is the new encryption key used to re-encrypt the text.
- _IN-FILE_ is the file containing the original encrypted text.
- _OUT-FILE_ is the file in which the re-encrypted text is saved.
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
