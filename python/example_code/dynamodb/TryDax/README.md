# Amazon DynamoDB Accelerator (DAX) examples

## Purpose

Demonstrates how to use the AWS SDK for Python (Boto3) and the Amazon DAX Client for  
Python to read items from an Amazon DynamoDB table. Retrieval, query, and scan speeds
are measured for both Boto3 and DAX clients to show some of the performance 
advantages of using DAX.

DAX is a DynamoDB-compatible caching service that provides fast in-memory performance 
and high availability for applications that demand microsecond latency. For more
information, see [In-Memory Acceleration with DynamoDB Accelerator (DAX)](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.html). 

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- An Amazon Virtual Private Cloud (VPC)
- A DAX cluster set up in your VPC 
- An Amazon Elastic Compute Cloud (EC2) instance running in your VPC with the
  following installed
    - Python 3.6 or later
    - Boto 3 1.11.10 or later
    - Amazon DAX Client for Python 1.1.7 or later
- PyTest 5.3.5 or later (to run unit tests)

## Running the code

The files in this example are designed to be used as part of the Python sample 
application tutorial in the  
[Developing with the DAX Client](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.client.html) 
section of the *Amazon DynamoDB Developer Guide*.

When run on your local computer, only the Boto3 client works. To run the 
scripts with the DAX client, you must run them on an EC2 instance within your VPC, 
as described in the tutorial.

Each file can be run separately at a command prompt. For example, create the
table by running the following from a command prompt window.

```
python 01-create-table.py
```  

On an EC2 instance, run the get item, query, and scan test scripts with the DAX client
by specifying a DAX cluster endpoint as the first positional argument. For example,
to run the query test script with the DAX client, run the following from a command 
prompt window.

```
python 04-query-test.py YOUR-CLUSTER-NAME.111111.clustercfg.dax.usw2.cache.amazonaws.com:8111
```

## Running the tests

The unit tests in this module use the botocore Stubber, which captures requests before 
they are sent to AWS and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/dynamodb/TryDax 
folder from a command prompt window.

```    
python -m pytest
```

## Additional information

- [Boto 3 Amazon DynamoDB examples](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/dynamodb.html)
- [Boto 3 Amazon DynamoDB service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)
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
