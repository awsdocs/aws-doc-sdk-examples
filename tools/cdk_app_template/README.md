# AWS Code Examples Repository - AWS CDK App Template

Use the template files in this folder to create the unique source code and the associated AWS CloudFormation template file for your finished AWS CDK app.

## Prerequisites

To set up your development machine in order to customize and run this AWS CDK template app, following the instructions for TypeScript in the "Prerequisites" section of the [Getting started with the AWS CDK](https://docs.aws.amazon.com/cdk/latest/guide/getting_started.html) topic in the *AWS CDK Developer Guide*.

## Files

This folder contains the following template files:

* ``.gitignore``: A list of files and folders that are generated as you create your AWS CDK app but should not be checked in to the AWS Code Examples Repository.
* ``cdk.json``: Information that the AWS CDK needs in order to find the corresponding AWS CDK app.
* ``package.json``: Information that Node Package Manager (NPM) needs, such as package names, dependencies, scripts, and versions.
* ``README.md``: This file.
* ``setup.ts``: Programmatic information about the AWS CDK app.
* ``tsconfig.json``: Information that the TypeScript compiler needs, such as root level files and various compiler options.

## Customization

To use these template files to create your AWS CDK app, modify the ``setup.ts`` file to include the AWS resources that you want the AWS CDK to generate, and 
update the ``package.json`` to include all required Amazon S3 package from the AWS Construct Library. 
To avoid errors, make sure all CDK dependencies have the same version. For example, ``"@aws-cdk/aws-s3: "1.79.0"``.  For more information, see the [AWS CDK Developer Guide](https://docs.aws.amazon.com/cdk/latest/guide).

To download the necessary dependent packages in order to run your AWS CDK app, first run the following command from within this folder:

``npm install``

To generate an AWS CloudFormation template file that represents your AWS CDK app logic, run the following commands from within this folder:

For Windows: ``rmdir /s /q cdk.out``

For Mac/*nix: ``rm -rf cdk.out``

(This makes sure that the ``cdk.out`` folder is regenerated when you run ``cdk synth`` in the next command)

``cdk synth > CloudFormation.yaml``

To run the AWS CDK app, which creates a corresponding stack in AWS CloudFormation and which in turn creates the specified AWS resources, run the following command from within this folder:

``cdk deploy``

The ``setup.ts`` file within this folder contains instructions for getting any custom inputs from the caller as desired and using those inputs as part of the ``cdk deploy`` command.

To destroy the AWS CDK app, which deletes the corresponding stack from AWS CloudFormation and which in turn deletes the specified AWS resources, run the following command from within this folder:

``cdk destroy``

## Merging

To merge your finished AWS CDK app's source code, merge **only** the following files from your development machine into its own self-contained folder within the [resources/cdk folder AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cdk):

* ``cdk.json``
* ``package.json``
* ``setup.ts``
* ``tsconfig.json``

**Important**: Update the [README.md in the resources/cdk folder AWS Code Examples Repository](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/resources/cdk) with a description of the resources your AWS CDK app provisions.

**Do not** merge the following folders or files from your development machine into the repository:

* ``cdk.out/``
* ``node_modules/``
* ``package-lock.json``
* This version of the ``README.md`` file

This is especially important for the ``node_modules/`` folder, as it could grow to several hundred megabytes or more in size during development. The ``.gitignore`` file in this folder contains a list of these folders and files.

You can regenerate these unmerged folders and files on your development machine as needed by running the preceding ``npm`` and ``cdk`` commands on your development machine (after of course installing the AWS CDK prerequisites).

Merge the ``CloudFormation.yaml`` file into the same location(s) as any code example file(s) with which this ``CloudFormation.yaml`` file is intended to be used.


## Using the AWS CLI or the AWS CloudFormation Console

Callers can use the AWS Command Line Interface (AWS CLI) or the AWS CloudFormation Console to run your AWS CloudFormation template file. (They can of course use the AWS CDK, but they would need to first install the AWS CDK prerequisites.)

To use the AWS CLI, run the following command from within the same folder as the ``CloudFormation.yaml`` file to create the corresponding stack in AWS CloudFormation, where ``SetupStack`` is some stack name that is unique within an individual AWS Region for the AWS account:

``aws cloudformation create-stack --stack-name SetupStack --template-body file://CloudFormation.yaml``

The ``setup.ts`` file within this folder contains instructions for getting any custom inputs from the caller as desired and using those inputs as part of the ``aws cloudformation create-stack`` command.

To delete the corresponding stack in AWS CloudFormation, run the following command:

``aws cloudformation delete-stack --stack-name SetupStack``

To use the AWS CloudFormation Console, do the following to create the corresponding stack:

1. Sign in to the AWS CloudFormation Console, at https://console.aws.amazon.com/cloudformation
1. Choose the desired AWS Region in which to create the corresponding stack.
1. Choose **Create stack, With new resources (standard)**.
1. On the **Create stack** page, for **Specify template**, choose **Upload a template file**.
1. Choose **Choose file**.
1. Browse to and open the ``CloudFormation.yaml`` file.
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

