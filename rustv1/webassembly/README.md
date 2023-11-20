# AWS SDK for Rust code examples using WebAssembly

## Purpose

This example demonstrates how to package in a WebAssembly module that uses the developer preview version of the AWS SDK for Rust.

## Code examples

- [Show functions](./src/lib.rs) (ListFunctions)

## Running the code examples

### Prerequisites

- You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

- Install the latest stable version of [Node.js](https://nodejs.org/en/download/).

- Install [wasm-pack](https://rustwasm.github.io/wasm-pack/installer/#).

### ⚠ Important

Your must customize your AWS credentials in the file [www/env/credentials.js](./www/env/credentials.js). Otherwise, it will not make the request to the backend.

### count-functions

This example lists your Lambda functions and returns the total amount found in a certain Region.

```
wasm-pack build --target web --out-dir www/pkg --dev
```

From within the [www](./www) directory, run the following command to install project and start serving.

```
npm ci
npm start
```

Access your page at `http://localhost:3000`. Make your selection and press `Run`:

- **region** is the Region in which the client is created.
  If not supplied, defaults to **us-west-2**.
- **verbose** displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html)

## Contributing

To propose a new code example to the AWS documentation team,
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
