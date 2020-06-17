# Amazon DynamoDB getting started examples

## Purpose

Demonstrate how to use the AWS SDK for Python (Boto3) to create an Amazon DynamoDB 
table for storing movies, load movies into the table from a JSON-formatted file, 
and update and query movies in various ways.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto 3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

## Running the code

The files in this example are designed to be used as part of the 
[Getting Started Developing with Python and DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Python.html) step-by-step
tutorial in the *Amazon DynamoDB Developer Guide*.

The functions in this example are configured to connect to a locally installed
version of DynamoDB as described in [Setting Up DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html).
To run the code against your AWS account instead, remove the 
`endpoint_url="http://localhost:8000"` parameter from the resource creation 
function in each file so that the code looks like the following. 

```
dynamodb = boto3.resource('dynamodb')
``` 

Each file can be run separately at a command prompt. For example, create the
Movies table by running the following from a command prompt window.

```
python MoviesCreateTable.py
```  

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/dynamodb/GettingStarted 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon DynamoDB examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/dynamodb.html)
- [Boto3 Amazon DynamoDB service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  "AWS Regional Table" on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
