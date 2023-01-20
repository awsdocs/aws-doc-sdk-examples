# API Gateway create and deploy a REST API example

## Overview

Shows how to use the AWS SDK for Python (Boto3) with Amazon API Gateway to
create a REST API that integrates with Amazon DynamoDB.

These instructions are additional steps you must take to set up and run this example 
after you have followed the instructions in the [README](../README.md) in the `api-gateway` folder.

## Running the code

This example requires a DynamoDB table with a specific key schema and an 
AWS Identity and Access Management (IAM) role that grants permission to let
API Gateway perform actions on the table. These resources are managed by
an AWS CloudFormation stack that is defined in the accompanying `setup.yaml` file. 

### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` flag at 
a command prompt.

```
python aws_service.py deploy
```

### Run the usage demonstration

Run the usage example with the `demo` flag at a command prompt.

```
python aws_service.py demo
``` 

### Destroy resources

Destroy example resources by running the script with the `destroy` flag at a command 
prompt.

```
python aws_service.py destroy
``` 

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
