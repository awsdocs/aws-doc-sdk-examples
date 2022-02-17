# AWS SDK for Rust code examples for Amazon QLDB

## Purpose

These examples demonstrate how to perform several Amazon Quantum Ledger Database (Amazon QLDB) operations using the developer preview version of the AWS SDK for Rust.

Amazon QLDB) is a fully managed ledger database that provides a transparent, immutable, and cryptographically verifiable transaction log owned by a central trusted authority.

## Code examples

- [Create a ledger](src/bin/create-ledger.rs) (CreateLedger)
- [List your ledgers](src/bin/list-ledgers.rs) (ListLedgers)
- [Start a ledger session](src/bin/qldb-helloworld.rs) (StartSession)

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

## create-ledger

This code example creates an Amazon QLDB ledger in the Region.

### Usage

```cargo run --bin create-ledger -l LEDGER [-r REGION] [-v]```

where:

- _LEDGER_ is the name of the ledger to create.
- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## list-ledgers

This code example lists your Amazon QLDB ledgers in the Region.

### Usage

```cargo run --bin list-ledgers [-r REGION] [-v]```

where:

- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## qldb-helloworld

This code example creates a low-level Amazon QLDB session against a ledger in the Region.

Avoid using the QldbSession API directly. Instead, use a higher-level driver, such as the Amazon QLDB Driver for Rust.

### Usage

cargo run --bin qldb-helloworld -l LEDGER [-r REGION] [-v]

where:

- _LEDGER_ is the name of the ledger to create the session against.
- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon QLDB](https://docs.rs/aws-sdk-qldb)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls. 

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0