# AWS SDK for Rust code examples

## Purpose

These examples demonstrate how to perform several operations using the developer preview version of the AWS SDK for Rust.

## ⚠ Important

- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Examples Layout

### `examples`

Examples show how to use the AWS SDK for Rust using single actions and services. These are actions with individual binaries, as well as `scenario` binaries which run several actions using a single AWS SDK service.

### `application`

Applications show how to use multiple AWS services in a single holistic application. These are built to meet the needs of a user persona. Each application is a standalone program or set of programs.

### `lambda`

The Lambda example shows how to use the AWS SDK for Rust within the Lambda Rust Runtime.

### `webassembly`

The WebAssembly example shows how to use the AWS SDK for Rust within a WASM runtime.

## Running the code examples

Each example has one or more examples that can be executed with `cargo run --bin [program name]`.
See the individual readme files in each service directory for information about specific code examples for that service.

Unit tests for each example can be run with `cargo test`, and these will not cause any changes or charges to your AWS account.
Integration tests may cause changes or charges to your AWS account, and are described individually for each example.
However, they can be run with `cargo test -- --ignored`.

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Rust](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/getting-started.html).

You must have the [Cargo](https://doc.rust-lang.org/cargo/) build tool, which is typically installed via [rustup](https://rustup.rs/).

### Environment variables

These examples use `tracing_subscriber` with `env_filter` to print information about various information as the example runs. Because the AWS SDK for Rust and many crates used in these examples use `tracing` for structured logging, it is important to have an understanding of the `RUST_LOG` variable.

- `RUST_LOG` controls the tracing environment logger level, allowing fine-tuned control of what log messages to display.
  - `info` will show all common output for the program.
  - `{crate_name}=debug` will show some useful per-action details.
  - `aws_smithy_http_tower::dispatch=trace` will print the full HTTP request of every call to an AWS SDK.
  - `aws_smithy_http::middleware=trace` will print the full HTTP response of every call to an AWS SDK.

The AWS SDK for Rust uses environment variables to configure some of its behavior.
These variables are documented in the [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/environment-variables.html).
Some common and useful variables are described here for easy reference.

The following environment variables are a subset of the variables used by the AWS Command Line Interface (AWS CLI) that are supported by the AWS SDK for Rust.
See [Configuring the AWS CLI - Environment Variables](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-envvars.html#envvars-list) for more details on these variables.

- `AWS_REGION` (`AWS_DEFAULT_REGION`) specifies the AWS Region to send requests to.
- `AWS_PROFILE` specifies the name of the AWS CLI profile with the credentials and options to use. This can be the name of a profile stored in a credentials or config file, or the value default to use the default profile.
- `AWS_ACCESS_KEY_ID` specifies an AWS access key associated with an IAM user or role.
- `AWS_SECRET_ACCESS_KEY` specifies the secret key associated with the access key. This is essentially the "password" for the access key.

The following environment variables are specific to using the AWS SDK for Rust running in Amazon Elastic Container Service (Amazon ECS).
For more information, see [AWS SDKs and Tools - Container Credentials](https://docs.aws.amazon.com/sdkref/latest/guide/feature-container-credentials.html).

- `AWS_CONTAINER_CREDENTIALS_FULL_URI` specifies the full HTTP URL endpoint for the SDK to use when making a request for credentials.
- `AWS_CONTAINER_CREDENTIALS_RELATIVE_URI` specifies the relative HTTP URL endpoint for the SDK to use when making a request for credentials.
- `AWS_CONTAINER_AUTHORIZATION_TOKEN` specifies the Authorization header on HTTP requests.

The following environment variables specify how Instance Metadata Service (IMDS) provides data about your instance when using the AWS SDK for Rust running in Amazon EC2.
See [AWS SDKs and Tools - IMDS Credentials](https://docs.aws.amazon.com/sdkref/latest/guide/feature-imds-credentials.html) for more information.

- `AWS_EC2_METADATA_DISABLED` specifies whether or not to attempt to use IMDS to obtain credentials.
- `AWS_EC2_METADATA_SERVICE_ENDPOINT` specifies the endpoint for IMDS.
- `AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE` specifies whether to access IMDS using IPv4 or IPv6.

The following environment variables are specific to the AWS SDK for Rust.

- `AWS_SDK_UA_APP_ID` specifies an additional app name that will be present in the User-Agent header for every SDK request.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference](https://docs.rs/releases/search?query=aws-sdk-)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Docker image (Beta)

This example code will soon be available in a container image
hosted on [Amazon Elastic Container Registry (ECR)](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html). This image will be pre-loaded
with all Rust examples with dependencies pre-resolved, allowing you to explore
these examples in an isolated environment.

⚠️ As of January 2023, the [SDK for Rust image](https://gallery.ecr.aws/b4v4v1s0/rust_dev_preview) is available on ECR Public but is still
undergoing active development. Refer to
[this GitHub issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/4134)
for more information.

## Contributing

To propose a new code example to the AWS documentation team,
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
