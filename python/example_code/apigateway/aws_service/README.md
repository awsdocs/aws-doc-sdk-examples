# Amazon API Gateway AWS service integration example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon API Gateway to
create a REST API that integrates with Amazon DynamoDB.

*API Gateway enables you to create and deploy your own REST and WebSocket APIs 
at any scale.*

## Code examples

* [Adding a resource to a REST API](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`create_resource`)
* [Adding an integration method to a REST API](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`add_integration_method`)
* [Creating a REST API](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`create_rest_api`)
* [Deleting a REST API](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`delete_rest_api`)
* [Deploying a REST API](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`create_deployment`)
* [Listing REST APIs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/apigateway/aws_service/aws_service.py)
(`get_rest_apis`)

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
- Requests 2.24.0 or later
- PyTest 6.0.2 or later (to run unit tests)

### Command

This example requires a DynamoDB table with a specific key schema and an 
AWS Identity and Access Management (IAM) role that grants permission to let
API Gateway perform actions on the table. These resources are managed by
an AWS CloudFormation stack that is defined in the accompanying `setup.yaml` file. 

#### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` flag at 
a command prompt.

```
python aws_service.py deploy
```

#### Run the usage demonstration

Run the usage example with the `demo` flag at a command prompt.

```
python aws_service.py demo
``` 

#### Destroy resources

Destroy example resources by running the script with the `destroy` flag at a command 
prompt.

```
python aws_service.py destroy
``` 

### Example structure

The example contains the following files.

**aws_service.py**

Shows how to use Amazon API Gateway to create a REST API that is backed by a DynamoDB
database. The example uses API Gateway integration with DynamoDB, so after the REST API
is created and deployed, no user code is needed to serve the REST API. The `usage_demo`
script in this file shows how to accomplish the following actions:

1. Create a REST API served by API Gateway.
1. Add resources to the REST API that represent a user profile.
1. Add integration methods so the REST API uses a DynamoDB table to store user profile
data. 
1. Use the Requests package to call the REST API to add and retrieve user profiles.

**setup.yaml**

Contains a CloudFormation script that is used to create the resources needed for 
the demo. Pass the `deploy` or `destroy` flag to the `aws_service.py` script to 
create or remove these resources:  

* A DynamoDB table that contains user profile information.
* An IAM role that grants API Gateway permission to read from and write to the 
table.

The `setup.yaml` file was built from the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
source script here: 
[/resources/cdk/python_example_code_apigateway_aws_service/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/python_example_code_apigateway_aws_service/setup.ts). 

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your 
[GitHub root]/python/example_code/apigateway/aws_service folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon API Gateway service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/apigateway.html)
- [Amazon API Gateway documentation](https://docs.aws.amazon.com/apigateway/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
