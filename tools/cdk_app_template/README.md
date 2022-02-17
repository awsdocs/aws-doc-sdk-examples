# AWS Code Examples Repository - AWS CDK App Template

Use the template files in this folder to create the unique source code and the associated AWS CloudFormation template file for your finished AWS CDK app.

## Prerequisites

To set up your development machine in order to customize and run this AWS CDK template app, following the instructions for TypeScript in the "Prerequisites" section of the [Getting started with the AWS CDK](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html) topic in the *AWS CDK Developer Guide*.

## Files

This folder contains the following template files:

* ``.gitignore``: A list of files and folders that are generated as you create your AWS CDK app but should not be checked into a repository.
* ``cdk.json``: Information that the AWS CDK needs in order to find the corresponding AWS CDK app.
* ``package.json``: Information that Node Package Manager (NPM) needs, such as package names, dependencies, scripts, and versions.
* ``README.md``: This file.
* ``setup.ts``: Programmatic information about the AWS CDK app.
* ``tsconfig.json``: Information that the TypeScript compiler needs, such as root level files and various compiler options.

## Customization

To use these template files to create your AWS CDK app, 
copy the files to your computer, 
modify the ``setup.ts`` file to include the AWS resources that you want the AWS CDK to generate, and 
update the ``package.json`` file to include all of the required AWS CDK packages. 

To download the necessary dependent packages in order to run your AWS CDK app, run the following command
(all of these commands should be run from the directory containin **cdk.json**):

`npm install`

To update all of your packages to the latest version, 
open **package.json** and change the version number of every reference
that starts with **aws-cdk** to the latest version.
Then run the following command:

`npm update`

To generate an AWS CloudFormation template file that represents your AWS CDK app logic, run the following command from within this folder:

`cdk synth`

This command produces the CloudFormation template **SetupStack.template.json** in **cdk.out**.

**Note**: You can pipe your CloudFormation template to a file, such as 'setup.yaml' using the following command:

`cdk synth` >> setup.yaml

If required, convert a text file to utf-8 format via PowerShell with the following command:

`Get-Content .\test.txt | Set-Content -Encoding utf8 test-utf8.txt`


To run the CloudFormation stack to create the resources, 
run the following command:

``cdk deploy``

The ``setup.ts`` file within this folder contains instructions for getting any custom inputs from the caller as desired and using those inputs as part of the ``cdk deploy`` command.

To destroy the AWS CDK app, which deletes the corresponding stack from AWS CloudFormation and which in turn deletes the specified AWS resources, run the following command from within this folder:

``cdk destroy``

## Merging

To merge your finished AWS CDK app's source code, merge **only** the following files from your development machine into its own self-contained folder within the [resources/cdk folder in the AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cdk):

* ``cdk.json``
* ``package.json``
* ``setup.ts``
* ``tsconfig.json``

**Important**: Update the [README.md in the resources/cdk folder of the AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cdk) with a description of the resources that your AWS CDK app provisions.

You can regenerate these unmerged folders and files on your development machine as needed by running the preceding ``npm`` and ``cdk`` commands on your development machine (after of course installing the AWS CDK prerequisites).

Merge the **SetupStack.template.json** file into the same location(s) as any code example file(s) with which this **SetupStack.template.json** file is intended to be used.

## Using the AWS CLI or the AWS CloudFormation Console

Callers can use the AWS Command Line Interface (AWS CLI) or the AWS CloudFormation Console to run your AWS CloudFormation template file. (They can of course use the AWS CDK, but they would need to first install the AWS CDK prerequisites.)

To use the AWS CLI, run the following command from within the same folder as the ``SetupStack.template.json`` file to create the corresponding stack in AWS CloudFormation, where ``SetupStack`` is some stack name that is unique within an individual AWS Region for the AWS account:

``aws cloudformation create-stack --stack-name SetupStack --template-body file://SetupStack.template.json``

The ``setup.ts`` file within this folder contains instructions for getting any custom inputs from the caller as desired and using those inputs as part of the ``aws cloudformation create-stack`` command.

To delete the corresponding stack in AWS CloudFormation, run the following command:

``aws cloudformation delete-stack --stack-name SetupStack``

To use the AWS CloudFormation Console, do the following to create the corresponding stack:

1. Sign in to the AWS CloudFormation Console, at https://console.aws.amazon.com/cloudformation
1. Choose the desired AWS Region in which to create the corresponding stack.
1. Choose **Create stack, With new resources (standard)**.
1. On the **Create stack** page, for **Specify template**, choose **Upload a template file**.
1. Choose **Choose file**.
1. Browse to and open the ``SetupStack.template.json`` file.
1. Choose **Next**.
1. On the **Specify stack details** page, for **Stack name**, type a unique name for the stack. (This stack name must be unique within an individual AWS Region for the AWS account.)
1. Specify any custom inputs, if required by the template's logic, and then choose **Next**. 
1. On the **Configure stack options** page, choose **Next**.
1. On the **Review** page, choose **Create stack**.

To delete the corresponding stack in the AWS CloudFormation Console, do the following:

1. Choose the name of the stack to delete.
1. Choose **Delete**.
1. Choose **Delete stack**.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0

