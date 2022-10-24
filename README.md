[![Build Status](https://github.com/aws/aws-sdk-ruby/workflows/CI/badge.svg)](https://github.com/awsdocs/aws-doc-sdk-examples/actions)
[![GitHub Super-Linter](https://github.com/awsdocs/aws-doc-sdk-examples/actions/workflows/super-linter.yml/badge.svg)](https://github.com/marketplace/actions/super-linter)
![[]](https://img.shields.io/badge/license-MIT%2FApache--2.0-blue)

# AWS SDK Code Examples
This repository contains code examples that demonstrate how to use the AWSK SDK's to interact with AWS services.

Many examples are injected into the [AWS Documentation](https://docs.aws.amazon.com).

## Get started
To invoke this example code, you must have an AWS account. For more information about creating an account, see [AWS Free Tier](https://aws.amazon.com/free/). 

You must also have AWS credentials configured, specifically a *AWS Access Key ID* and *AWS Secret Key*. In some cases, you need a *Session ID*. We recommend you accomplish this using the AWS CLI. To learn more, see [CLI Configuration basics](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html)

## ⚠️ Important
These code examples interact with services that may incur charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Additionally, example code may theoretically modify or delete existing AWS resources. As a matter of due diligence:
 * Be aware of the resources that these examples create or delete.
 * Be aware of the costs that might be charged to your account as a result.
 * Back up your important data.


## How this repository is organized
Code examples for each language's SDK can be found within the following sub-directories:

| SDK        | folder                                | SDK version | SDK status  |
|------------|---------------------------------------|-------------|-------------|
| .NET       | [dotnetv3/](dotnetv3)                 | 3.5+        | ![[]](https://img.shields.io/badge/-GA-blue)           |
| .NET       | [dotnet/](dotnet)                     | <3.5        | ![[]](https://img.shields.io/badge/-deprecated-red)  |
| C++        | [cpp/](cpp)                           | 1           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Go         | [gov2/](gov2)                         | 2           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Go         | [go/](go)                             | 1           | ![[]](https://img.shields.io/badge/-deprecated-red)  |
| Java       | [javav2/]()                           | 2           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Java       | [java/](java)                         | 1           | ![[]](https://img.shields.io/badge/-deprecated-red)  |
| JavaScript | [javascriptv3/](javascriptv3)         | 3           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| JavaScript | [javascript/](javascript)             | 2           | ![[]](https://img.shields.io/badge/-deprecated-red)  |
| Kotlin     | [kotlin/](kotlin)                     |             | ![[]](https://img.shields.io/badge/-preview-brightgreen)       |
| PHP        | [php/](php)                           | 3           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Python     | [python/](python)                     | 3           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Ruby       | [ruby/](ruby)                         | 3           | ![[]](https://img.shields.io/badge/-GA-blue)          |
| Rust       | [rust_dev_preview/](rust_dev_preview) |             | ![[]](https://img.shields.io/badge/-preview-brightgreen) |
| Swift      | [swift/](swift)                       |             | ![[]](https://img.shields.io/badge/-preview-brightgreen)     |

At the top level of each directory, a README explains how to build and run that set of examples.

Within each directory, an `example_code/` folder contains examples organized by AWS service. For example `ruby/example_code/ec2` contains example code using the SDK for Ruby.

## Other examples
Additionally, this repository contains sample code for non-SDK AWS tooling:

| folder                                | service                                  |
|---------------------------------------|------------------------------------------|
| [aws-cli/](aws-cli)                   | AWS Command Line Interface (AWS CLI)     |
| [cloudformation/](cloudformation)     | AWS CloudFormation                       |
| [iam_policies/](iam_policies)         | AWS Identity and Access Management (IAM) |
| [lambda_functions/](lambda_functions) | AWS Lambda                               |
| [typescript/](typescript)             | AWS Cloud Development Kit (AWS CDK)      |

# Contributing
This repository thrives on your contributions! To get involved, see the [Guidelines for contributing](CONTRIBUTING.md). 

# Copyright and license

All content in this repository, unless otherwise stated, is 
Copyright © Amazon Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the [Apache
license, version 2.0](https://www.apache.org/licenses/LICENSE-2.0) (the "License"). The full
license text is provided in the `LICENSE` file accompanying this repository.
