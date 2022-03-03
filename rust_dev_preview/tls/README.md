# AWS SDK for Rust code examples for Amazon S3

## Purpose

These examples demonstrate how to adjust TLS settings to custom values. This can be useful to enforce
custom levels of security. For instance, if your company requires a minimum version of 1.3 for outgoing calls.

## Code examples

### Scenario examples

* [Setting the minimum TLS version to 1.3](src/lib.rs) 

### API examples

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

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in the [Getting Started](https://github.com/awslabs/aws-sdk-rust#getting-started-with-the-sdk) section of the SDK README.
The minimum version of Rust needed to run the SDK is listed [here](https://github.com/awslabs/aws-sdk-rust#supported-rust-versions-msrv).
Instructions for installing Rust and Cargo can be found in the [Official Rust Documentation](https://doc.rust-lang.org/book/ch01-01-installation.html).

## Running the code examples

### Setting the minimum TLS version to 1.3

Shows how to build a custom connector using rustls and then pass that to the client to make calls.
This example uses the Key Management Service for demonstration purposes, but this can be extrapolated
to any service which supports TLS 1.3.
To start, run the following at a command prompt from the Rust root of the project:

```
cargo run --bin tls
```   

Or, to run the test suite, run this command from the Rust root of the project:

```
cargo test -p tls --test test-tls -- --include-ignored
```

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg) 

=======
## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0