[![Build Status](https://github.com/aws/aws-sdk-ruby/workflows/CI/badge.svg)](https://github.com/awsdocs/aws-doc-sdk-examples/actions)
[![GitHub Super-Linter](https://github.com/awsdocs/aws-doc-sdk-examples/actions/workflows/super-linter.yml/badge.svg)](https://github.com/marketplace/actions/super-linter)
[![Github forks](https://img.shields.io/github/forks/aws/aws-sdk-ruby.svg)](https://github.com/awsdocs/aws-doc-sdk-examples/network)
[![Github stars](https://img.shields.io/github/stars/aws/aws-sdk-ruby.svg)](https://github.com/awsdocs/aws-doc-sdk-examples/stargazers)

# AWS SDK Code Examples
This repository contains code examples that demonstrate how to use the AWSK SDK's to interact with AWS services.

Many examples are injected into the [AWS Documentation](https://docs.aws.amazon.com).

## Get started
To invoke this example code, you must have an AWS account. For more information about creating an account, see [AWS Free Tier](https://aws.amazon.com/free/). 

### Configuring the AWS SDKs

AWS SDKs must be configured with the *AWS Access Key ID*, *AWS Secret Key*. In some cases, you need a *Session ID*.

We recommend using the AWS Command Line Interface (AWS CLI) to complete the configuration.
Most AWS SDKs use the configuration created by the AWS CLI.
To configure the AWS CLI, use the following command:
```
   aws configure
```
The preceding command will create or update the file `$HOME/.aws/credentials`, which is used by the SDKs where applicable.
For more information, see [Configuration and credential file settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) in the AWS Command Line Interface User Guide.

Generally, the AWS SDKs look for configuration information in the following places, in this order:

1. Within `$HOME/.aws/credentials`, in the default profile
2. Within `$HOME/.aws/credentials`, in the profile defined by the `AWS_PROFILE` environment variable
3. The `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and (if defined,) `AWS_SESSION_TOKEN` environment variables.
4. An SDK-specific location such as the Java SDK system property `aws.accessKeyId` and `aws.secretKey`
5. Credentials specified directly to the SDK, such as `AWSBasicCredentials` in the Java V2 SDK

If you are unsure, see the documentation for the specific SDK that you're using.


## ⚠️ Important
Running these code examples might result in charges to your AWS account.
Many examples involve services that are charged on a per-use basis, such as Amazon Simple Email Service (Amazon SES).
Other examples might create resources that have long-term costs with services such as Amazon Simple Storage Service Glacier (Amazon S3 Glacier).
For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Some examples modify or delete resources, such as AWS Identity and Access Management (IAM) users and Amazon S3 bucket contents or previous versions.
It is your responsibility to do the following:

 * Be aware of the resources that these examples create or delete
 * Be aware of the costs that might be charged to your account as a result
 * Back up your important data

## SDKs and support status
Each SDK, and its respective examples, has one of the following support statuses:

* Currently maintained
* In preview 
* No longer maintained

Do not use preview examples in any type of production environment.
SDKs that are in preview might not be stable or consistent, and their respective SDK examples might not be up-to-date or fully tested.

Examples for SDKs that are no longer maintained are not likely to be current and might not follow current best practices.
These examples are retained for reference purposes.

This repo contains top-level folders of code examples for each SDK language.
For example, the `cpp` folder is for the AWS SDK for C++, and the `python` folder is for the AWS SDK for Python (Boto3).
Preview SDK paths are subject to change.

The README file in each SDK language folder explains how to build and run the examples in that folder.

Within each SDK language folder, the example_code folder contains examples organized by AWS service.
Each AWS service folder is named for its corresponding AWS CLI command. 
or example, the s3 folder contains Amazon S3 examples. For a list of AWS service commands, see [Available services](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/index.html#available-services) in the *AWS CLI Command Reference*.

## Examples for currently maintained SDKs
The code examples are organized by AWS SDK or AWS programming tool. The following list shows some of the top-level folders:

* **cpp** for the latest version of the AWS SDK for C++ (version 1)
* **dotnetv3** for the latest version of the AWS SDK for .NET (version 3.5 and later)
* **gov2** for the latest version of the AWS SDK for Go (version 2)
* **javav2** for the latest version of the AWS SDK for Java (version 2)
* **javascriptv3** for the latest version of the AWS SDK for JavaScript (version 3)
* **php** for the latest version of the AWS SDK for PHP (version 3)
* **python** for the latest version of the AWS SDK for Python (Boto3)
* **ruby** for the latest version of the AWS SDK for Ruby (version 3)


## Examples for SDKs currently in preview 
* **.kotlin_alpha** for the alpha version of the AWS SDK for Kotlin
* **swift** for the preview release of the AWS SDK for Swift
* **rust_dev_preview** for the developer preview version of the AWS SDK for Rust

## Examples for SDKs that have been deprecated

Code examples for previous AWS SDK versions are archived in this repository but are no longer maintained. These include the following:

* **dotnet** for versions of the AWS SDK for .NET prior to version 3.5
* **go** for AWS SDK for Go version 1
* **java** for AWS SDK for Java version 1
* **javascript** for AWS SDK for JavaScript version 2

As AWS SDK major version numbers increment, this repository will more consistently reflect the version numbers in these folders.
For example, imagine that the AWS SDK for Ruby moves to a version 4.
A new rubyv4 folder is added. In this scenario, if AWS officially announces that version 3 of the AWS SDK for Ruby is no longer supported,
then the previous ruby folder is deleted.

## Other examples
Other top-level folders include the following:

* **aws-cli** for script examples for use with the AWS Command Line Interface (AWS CLI).
* **cloudformation** for example templates for use with AWS CloudFormation.
* **iam_policies** for example policy documents for use with AWS Identity and Access Management (IAM).
* **lambda_functions** for example function code for use with AWS Lambda.
* **typescript** for TypeScript-based code examples for use with the AWS Cloud Development Kit (AWS CDK), and other AWS services.
  (For TypeScript-based code examples for use with the AWS SDK for JavaScript, see the **javascriptv3** folder.)

# Contributing
If you plan to contribute examples for use in the documentation, see the `Guidelines for contributing](CONTRIBUTING.md). 

Note that the AWS documentation team prefers to produce code examples that cover broader scenarios and use 
cases, versus simple code snippets that cover only individual API calls.

* To propose a new code example for our consideration, [create a request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=&labels=code+sample+request&template=request-new-code-example.md&title=%5BNEW+EXAMPLE+REQUEST%5D+%3C%3CProvide+a+title+for+this+proposal%3E%3E).
* To submit a code example you have written, [create a Pull Request](https://github.com/awsdocs/aws-doc-sdk-examples/compare) and follow the steps in the PR template.

## Additional Information

The `scripts/` folder

The **scripts** folder contains scripts that the AWS documentation team uses internally to build the code examples into various AWS documentation sets.


## Default branch name change

We have changed the default branch for this repo from **master** to **main**.

If the parent branch of your fork or branch is **master**,
the following instructions tell you how to change the parent branch to **main**.

To show the parent branch,
where **BRANCH** is the name of your branch:

1. Navigate to the root of your branch or fork.
2. Make sure your branch is the current branch (**git checkout BRANCH**).
3. Run **git branch --contains**.

### Changing a branch parent branch from master to main
To change the parent branch for your branch to **main**,
navigate to the root of your branch and enter the following commands,
where *BRANCH* is the name of your branch:

```		
   git branch -m master main
   git fetch origin
   git branch -u origin/main main
   git remote set-head origin -a
   git remote update --prune
```

### Changing a fork's default branch from master to main
GitHub will notify you when a parent branch has changed.
To change your fork's default branch to **main**:

1. Navigate to main web page of your fork.
2. You should see a "The default branch on the parent repository has been renamed" message.
3. Select the **branch settings** link.
4. Change **master** to **main**.

# Questions or issues?
If you have any questions, or if you experience an issue when retargeting your branch or fork,
create a new GitHub issue and include as much detail as possible.


# Copyright and license

All content in this repository, unless otherwise stated, is 
Copyright © Amazon Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the [Apache
license, version 2.0](https://www.apache.org/licenses/LICENSE-2.0) (the "License"). The full
license text is provided in the `LICENSE` file accompanying this repository.
