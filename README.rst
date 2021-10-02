.. Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

NOTICE
======

We have changed the default branch for this repo from **master** to **main**.

If the parent branch of your fork or branch is **master**,
the following instructions tell you how to change the parent branch to **main**.

To show the parent branch,
where **BRANCH** is the name of your branch:

1. Navigate to the root of your branch or fork.
2. Make sure your branch is the current branch (**git checkout BRANCH**).
3. Run **git branch --contains**.

Changing a branch parent branch from master to main
---------------------------------------------------

To change the parent branch for your branch to **main**,
navigate to the root of your branch and enter the following commands,
where *BRANCH* is the name of your branch:

.. code-block:: sh
		
   git branch -m master main
   git fetch origin
   git branch -u origin/main main
   git remote set-head origin -a
   git remote update --prune

Changing a fork's default branch from master to main
----------------------------------------------------

GitHub will notify you when a parent branch has changed.
To change your fork's default branch to **main**:

1. Navigate to main web page of your fork.
2. You should see a "The default branch on the parent repository has been renamed" message.
3. Select the **branch settings** link.
4. Change **master** to **main**.

Questions or Issues?
--------------------

If you have any questions, or run across any issue retargeting your branch or fork,
open an issue and give us as much detail as possible.

Welcome
=======
This is the repository for various code examples used in the public 
`AWS documentation <https://docs.aws.amazon.com>`_. For more information, see
`Tools to Build on AWS <https://aws.amazon.com/getting-started/tools-sdks/>`_.

Finding code examples
=====================

The code examples are organized by AWS SDK or AWS programming tool. For example, among the top-level folders:

* **cpp** for the latest version of the AWS SDK for C++ (version 1)
* **dotnetv3** for the latest version of the AWS SDK for .NET (version 3.5 and later)
* **gov2** for the latest version of the AWS SDK for Go (version 2)
* **javav2** for the latest version of the AWS SDK for Java (version 2)
* **javascriptv3** for the latest version of the AWS SDK for JavaScript (version 3)
* **php** for the latest version of the AWS SDK for PHP (version 3)
* **python** for the latest version of the AWS SDK for Python (Boto3)
* **ruby** for the latest version of the AWS SDK for Ruby (version 3)

We also have code examples for two SDKs currently in alpha,
**DO NOT USE THIS EXAMPLE CODE IN PRODUCTION**:

* **.kotlin_alpha** for the alpha version of the AWS SDK for Kotlin.
* **.rust_alpha** for the alpha version of the AWS SDK for Rust.

Code examples for older AWS SDK versions are archived in this repository but no longer maintained. These include:

* **dotnet** for versions of the AWS SDK for .NET prior to version 3.5
* **go** for AWS SDK for Go version 1
* **java** for AWS SDK for Java version 1
* **javascript** for AWS SDK for JavaScript version 2

As AWS SDK major version numbers increment, this repository will begin to more consistently reflect their version numbers among these folders to make these distinctions clearer. For example, if and when the AWS SDK for Ruby moves to a version 4, a new **rubyv4** folder will be added. Then when AWS officially announces that AWS SDK for Ruby version 3 has been deprecated, the **ruby** folder will be deleted.

Other top-level folders include:

* **aws-cli** for script examples for use with the AWS Command Line Interface (AWS CLI).
* **cloudformation** for example templates for use with AWS CloudFormation.
* **iam_policies** for example policy documents for use with AWS Identity and Access Management (IAM).
* **lambda_functions** for example function code for use with AWS Lambda.
* **typescript** for TypeScript-based code examples for use with the AWS Cloud Development Kit (CDK), and other AWS services. (For TypeScript-based code examples for use with the AWS SDK for JavaScript, see the **javascriptv3** folder.)

Note that the **scripts** folder contains scripts that the AWS documentation team uses internally to build the code examples into various AWS documentation sets.

Building and running code examples
==================================

Inside each language-specific directory, we include a **README** file that explains how to
build and run the examples in the directory.

The example code in the language-specific directories is organized by
the AWS service command in the `AWS Command Line Interface (AWS CLI) Command Reference <https://awscli.amazonaws.com/v2/documentation/api/latest/index.html>`_ (**s3** for `Amazon S3 <https://aws.amazon.com/s3>`_ examples, and so
on).

Proposing new code examples
===========================

To propose a new code example for the AWS documentation team to consider working on, `create a 
request <https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=&labels=code+sample+request&template=request-new-code-example.md&title=%5BNEW+EXAMPLE+REQUEST%5D+%3C%3CProvide+a+title+for+this+proposal%3E%3E>`_.

Note that the AWS documentation team prefers to produce code examples that cover broader scenarios and use 
cases, versus simple code snippets that cover only individual API calls.

Submitting code examples for use in AWS documentation
=====================================================

If you plan to contribute examples for use in the documentation (the purpose of this repository),
read this section carefully so that we can work together effectively. 
For process instructions and additional guidance, see the `Guidelines for contributing <CONTRIBUTING.md>`_. 

Copyright and License
=====================

All content in this repository, unless otherwise stated, is 
Copyright © Amazon Web Services, Inc. or its affiliates. All rights reserved.

Except where otherwise noted, all examples in this collection are licensed under the `Apache
license, version 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_ (the "License"). The full
license text is provided in the ``LICENSE`` file accompanying this repository.
