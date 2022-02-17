# AWS SDK for Rust code examples for AWS KMS

## Purpose

These examples demonstrate how to perform several AWS Key Management Service (AWS KMS) operations using the developer preview version of the AWS SDK for Rust.

AWS KMS is an encryption and key management service scaled for the cloud. AWS KMS keys and functionality are used by other AWS services, and you can use them to protect data in your own applications that use AWS.

## Code examples

- [Create a key](src/bin/create-key.rs) (CreateKey)
- [Decrypt an encrypted string](src/bin/decrypt.rs) (Decrypt)
- [Encrypt a string](src/bin/encrypt.rs) (Encrypt)
- [Create a data key](src/bin/generate-data-key.rs) (GenerateDataKey)
- [Create a data key without plaintext](src/bin/generate-data-key-without-plaintext.rs) (GenerateDataKeyWithoutPlaintext)
- [Create random string](src/bin/generate-random.rs) (GenerateRandom)
- [Create random 64-bit string](src/bin/kms-helloworld.rs) (GenerateRandom using aws_hyper client)
- [Lists your keys](src/bin/list-keys.rs) (ListKeys)
- [Re-encrypts a string](src/bin/reencrypt-data.rs) (ReEncrypt)

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

### create-key

This example creates an AWS KMS key.

`cargo run --bin create-key -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### decrypt

This example decrypts a string encrypted by an AWS KMS key.

`cargo run --bin decrypt -- -k KEY -i INPUT-FILE [-r REGION] [-v]`

- _KEY_ is the encryption key.
- _INPUT-FILE_ is the name of the file containing text encrypted by the key.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### encrypt

This example encrypts a string using an AWS KMS key.

`cargo run --bin encrypt -- -k KEY -t TEXT -o OUT-FILE [-r REGION] [-v]`

- _KEY_ is the encryption key.
- _TEXT_ is the string to encrypt by the key.
- _OUT-FILE_ is the file in which the encrypted text is saved.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-data-key

This example creates a data key for client-side encryption using an AWS KMS data key.

`cargo run --bin generate-data-key -- -k KEY -t TEXT -o OUT-FILE [-r REGION] [-v]`

- _KEY_ is the name of the AWS KMS data key.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-data-key-without-plaintext

This example creates a data key for client-side encryption using an AWS KMS data key,
showing the plaintext public key but not the plaintext private key.

`cargo run --bin generate-data-key-without-plaintext -- -k KEY -t TEXT -o OUT-FILE [-r REGION] [-v]`

- _KEY_ is the name of the AWS KMS data key.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### generate-random

This example creates a random byte string that is cryptographically secure.

`cargo run --bin generate-random -- -l LENGTH [-r REGION] [-v]`

- _LENGTH_ is the number of bytes, which must be less than 1024.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### kms-helloworld

This example creates a random, 64-byte string that is cryptographically secure in __us-east-1__.

`cargo run --bin kms-helloworld`

### list-keys

This example lists your AWS KMS keys in the Region.

`cargo run --bin list-keys -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### reencrypt-data

This example re-encrypts a text string that was encrypted using an AWS KMS key with another AWS KMS key.

`cargo run --bin reencrypt-data -- -f FIRST-KEY -n NEW-KEY -i INPUT-FILE -o OUT-FILE [-r REGION] [-v]`

- _FIRST-KEY_ is the encryption key used to initially encrypt the text.
- _NEW-KEY_ is the new encryption key used to re-encrypt the text.
- _IN-FILE_ is the file containing the original encrypted text.
- _OUT-FILE_ is the file in which the re-encrypted text is saved.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- - [AWS SDK for Rust API Reference for AWS KMS](https://docs.rs/aws-sdk-kms)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
