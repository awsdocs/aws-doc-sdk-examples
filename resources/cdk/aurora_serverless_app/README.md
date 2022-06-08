# Aurora serverless sample application resources

## Overview

Creates AWS resources for Amazon Aurora sample applications. The scripts in this
example create the following resources:
 
* An AWS Secrets Manager secret that contains administrator credentials in a format 
that can be used by an Aurora MySQL database. 
* An Aurora MySQL database configured to use the credentials from the secret.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Deploying resources

You can use the AWS Cloud Development Kit (AWS CDK) or the AWS Command Line Interface
(AWS CLI) to deploy and destroy the resources for this example.

### Deploying with the AWS CDK

To deploy with the AWS CDK, you must install [Node.js](https://nodejs.org) and the 
[AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html).

This example was built and tested with AWS CDK 2.25.0.

Deploy AWS resources by running the following at a command prompt in the
`resources/cdk/aurora_serverless_app` folder:

```
npm install
cdk deploy
```

The stack takes a few minutes to deploy. When it completes, it prints output like 
the following:

```
Outputs:
doc-example-aurora-app.ClusterArn = arn:aws:rds:us-west-2:0123456789012:cluster:doc-example-aurora-app-docexampleauroraappcluster-1bqmf5EXAMPLE
doc-example-aurora-app.DbName = auroraappdb
doc-example-aurora-app.SecretArn = arn:aws:secretsmanager:us-west-2:0123456789012:secret:docexampleauroraappsecret8B-rEHdtEXAMPLE-111222
```

You can destroy the resources by running the following:

```
cdk destroy
```

### Deploying with the AWS CLI 

To deploy with the AWS CLI you must first install the 
[AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html).

Deploy AWS resources by running the following at a command prompt in the 
`resources/cdk/aurora_serverless_app` folder:

```
aws cloudformation create-stack --template-body file://setup.yaml --stack-name YOUR_STACK_NAME
```

The stack takes a few minutes to deploy. You can check status by running the following:

```
aws cloudformation describe-stacks --stack-name YOUR_STACK_NAME
```

When the stack is ready, it shows `StackStatus` of `CREATE_COMPLETE`.

You can get the outputs from the stack by running the following:

```
aws cloudformation describe-stacks --stack-name STACK_NAME --query Stacks[0].Outputs --output text
```

This results in output like the following: 

```
SecretArn  arn:aws:secretsmanager:us-west-2:0123456789012:secret:docexampleauroraappsecret8B-6N2njEXAMPLE-111222
ClusterArn arn:aws:rds:us-west-2:0123456789012:cluster:aurora-test-stack-docexampleauroraappcluster12345-kh39pEXAMPLE
DbName     auroraappdb
```

You can destroy the resources by running the following:

```
aws cloudformation delete-stack --stack-name YOUR_STACK_NAME
```

## Using the resources

After the database and secret are created, you can use the AWS Management Console,
AWS CLI, or an AWS SDK to run SQL commands to create tables and manage data in the 
Aurora database.

For example, use the AWS CLI to run a statement with the Amazon RDS Data Service to 
create a table in the Aurora database. Replace the `resource-arn` and `secret-arn`
values with the values output when you deployed the resource stack.

```
aws rds-data execute-statement ^
    --resource-arn "arn:aws:rds:us-west-2:0123456789012:cluster:doc-example-aurora-app-docexampleauroraappcluster-1bqmf5EXAMPLE" ^
    --database "auroraappdb" ^
    --secret-arn "arn:aws:secretsmanager:us-west-2:0123456789012:secret:docexampleauroraappsecret8B-6N2njEXAMPLE-111222" ^
    --sql "CREATE TABLE Persons (PersonID int, LastName varchar(255), FirstName varchar(255));"
```

> *Note:* Because Aurora is serverless, your cluster may need to warm up before it can 
> run statements. If you receive a BadRequestException about a communications link 
> failure, wait a short time and run the statement again. 

You can insert a row into the `Persons` table by substituting SQL like the following:

```
    --sql "INSERT INTO Persons VALUES (1, 'Owusu', 'Efua');"
```

Or get all data from the `Persons` table:

```
    --sql "SELECT * FROM Persons;"
```

## Additional resources

* [AWS CDK V2 Developer Guide](https://docs.aws.amazon.com/cdk/v2/guide/home.html)
* [AWS CLI User Guide for Version 2](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
* [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
