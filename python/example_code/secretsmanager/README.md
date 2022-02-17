# AWS Secrets Manager examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Secrets Manager to
create, manage, and use secrets.

* Create a new secret and update it with string and byte values.
* Update the stage of a secret.
* Use an existing secret with Amazon Relational Database Service (Amazon RDS) Data 
Service to access a serverless Amazon Aurora cluster and database.

*AWS Secrets Manager helps you to securely encrypt, store, and retrieve credentials for 
your databases and other services.*
 
## Code examples

### Scenario examples

* [Connect to an Aurora database by using a secret](secretsmanager_basics.py)
* [Create and manage a secret](secretsmanager_basics.py)

### API examples

* [Create a secret](secretsmanager_basics.py)
(`CreateSecret`)
* [Delete a secret](secretsmanager_basics.py)
(`DeleteSecret`)
* [Describe a secret](secretsmanager_basics.py)
(`DescribeSecret`)
* [Get a random password](secretsmanager_basics.py)
(`GetRandomPassword`)
* [Get a secret value](secretsmanager_basics.py)
(`GetSecretValue`)
* [List secrets](secretsmanager_basics.py)
(`ListSecrets`)
* [Put a value in a secret](secretsmanager_basics.py)
(`PutSecretValue`)
* [Update the stage of a secret version](secretsmanager_basics.py)
(`UpdateSecretVersionStage`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.5 or later
- Boto3 1.15.4 or later
- PyTest 6.0.2 or later (to run unit tests)

### Command

There are two demonstrations in this set of examples:

* Creating and managing a secret.
* Connecting to an Aurora database by using credentials stored in a secret.

#### Secret management

Run this example with the `demo-secret` action at a command prompt.

```
python secretsmanager_basics.py demo-secret
``` 

#### Connect to an Aurora database

This example requires AWS resources that can be deployed by the 
AWS CloudFormation stack that is defined in the accompanying `setup.yaml` file.
This stack manages the following resources:

* An Amazon Aurora cluster and accompanying infrastructure.
* A Secrets Manager secret that contains credentials needed to access the Amazon
Aurora cluster.  

##### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` action at 
a command prompt.

```
python secretsmanager_basics.py deploy
```

##### Run the demonstration

Run the usage example with the `demo-aurora` action at a command prompt.

```
python secretsmanager_basics.py demo-aurora
``` 

##### Destroy resources

Destroy example resources by running the script with the `destroy` action at a command 
prompt.

```
python secretsmanager_basics.py destroy
``` 

### Example structure

The example contains the following files.

**secretsmanager_basics.py**

Shows how to create and manage Secrets Manager secrets.

The `create_and_manage_secret_demo` script performs the following actions:

1. Creates a secret.
1. Updates the secret value with a randomly generated password.
1. Updates the secret value with a Base64-encoded binary value.
1. Lists secrets for the current account.
1. Deletes the secret.

The `aurora_demo` script uses an existing secret to run SQL statements on an Amazon 
Aurora cluster:

1. Creates a database.
1. Creates and populates a table.
1. Queries the table. 

**setup.yaml**

Contains a CloudFormation script that is used to create the resources needed for 
the demo. Pass the `deploy` or `destroy` action to the `secretsmanager_basics.py` script 
to create or remove these resources:  

* An Amazon Aurora cluster and associated infrastructure. 
* A Secrets Manager secret.

The `setup.yaml` file was built from the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
source script here: 
[/resources/cdk/python_example_code_secretsmanager_demo/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/python_example_code_secretsmanager_secret/setup.ts). 

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your 
[GitHub root]/python/example_code/secretsmanager folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Secrets Manager service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/secretsmanager.html)
- [Boto3 Amazon RDS Data Service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds-data.html)
- [AWS Secrets Manager documentation](https://docs.aws.amazon.com/secretsmanager/)
- [Using the Data API for Aurora Serverless](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/data-api.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
