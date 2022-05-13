# IAM code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to manage AWS Identity and Access Management (IAM) resources.

*IAM is a web service for securely controlling access to AWS services. With IAM, you can centrally manage users,
security credentials such as access keys, and permissions that control which AWS resources users and applications can
access.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform
  the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information,
  see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

* [Listing roles](src/iam-service-lib.rs) (ListRoles)
* [Listing users](src/iam-service-lib.rs) (ListUsers)
* [Listing groups](src/iam-service-lib.rs) (ListGroups)
* [Attaching role policies](src/iam-service-lib.rs) (AttachRolePolicy)
* [Getting an account password policy](src/iam-service-lib.rs) (GetAccountPasswordPolicy)
* [Listing SAML providers](src/iam-service-lib.rs) (ListSAMLProviders)
* [Listing role policies](src/iam-service-lib.rs) (ListRolePolicies)
* [Getting a role](src/iam-service-lib.rs) (GetRole)
* [Listing policies](src/iam-service-lib.rs) (ListPolicies)
* [Creating a service-linked role](src/iam-service-lib.rs) (CreateServiceLinkedRole)
* [Creating a role](src/iam-service-lib.rs) (CreateRole)
* [Listing attached role policies](src/iam-service-lib.rs) (ListAttachedRolePolicies)
* [Getting a policy](src/iam-service-lib.rs) (GetPolicy)

### Scenario

* [Getting started with IAM](src/bin/iam-getting-started.rs)

## Running the code

Run the scenario with the following command:

```
cargo run --bin iam_getting_started
```

### Prerequisites

- You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).
- Install Rust and Cargo as described [in the Rust documentation](https://doc.rust-lang.org/book/ch01-01-installation.html)

## Tests

⚠️ Running the tests might result in charges to your AWS account.

Run the tests with the following command:

```
cargo test --test test-iam-service-lib -- --include-ignore
```

## Additional resources

* [AWS Identity and Access Management documentation](https://docs.aws.amazon.com/iam)
* [AWS Identity and Access Management API Reference](https://docs.aws.amazon.com/IAM/latest/APIReference/welcome.html)
* [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
