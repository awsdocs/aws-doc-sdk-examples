# AWS Step Functions messenger example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Step Functions to
create and run state machines.

* Create a state machine that retrieves and updates message records from an 
Amazon DynamoDB table.
* Update the state machine definition to also send messages to Amazon Simple Queue Service 
(Amazon SQS).
* Start and stop state machine runs.
* Connect to AWS Lambda, DynamoDB, and Amazon SQS from a state machine by using service
integrations.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.14.47 or later
- PyTest 5.3.5 or later (to run unit tests)

## Cautions

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

### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` flag at 
a command prompt.

```
python stepfunctions_demo.py deploy
```

### Run the usage demonstration

Run the usage example with the `demo` flag at a command prompt.

```
python stepfunctions_demo.py demo
``` 

### Destroy resources

Destroy example resources by running the script with the `destroy` flag at a command 
prompt.

```
python stepfunctions_demo.py destroy
``` 

## Example structure

The example contains the following files.

**state_definitions.py**

Constructs state definitions used for the demonstration by inserting resource 
identifiers that are retrieved from the CloudFormation stack and 
optionally including a state that sends messages to Amazon SQS. Definitions are built 
as Python dicts and must be transformed to JSON format before they are used in 
Step Functions. 

**stepfunctions_statemachine.py**

Shows how to use AWS Step Functions state machine APIs. 

**stepfunctions_demo.py**

Shows how to create a Step Functions state machine that continuously reads message 
records from an Amazon DynamoDB database and sends them to an Amazon Simple Queue 
Service (Amazon SQS) queue.

1. Creates a state machine that calls a Lambda function to get messages and update
them as sent.
1. Runs the state machine and verifies that it updates items as expected in the 
DynamoDB table.
1. Updates the state machine with a new definition that includes a state that sends
message to Amazon SQS.
1. Runs the state machine again and verifies that it now sends messages to Amazon SQS. 

**setup.yaml**

Contains a CloudFormation script that is used to create the resources needed for 
the demo. Pass the `deploy` or `destroy` flag to the `stepfunctions_demo.py` script to 
create or remove these resources:

* A DynamoDB table.
* A Lambda function.
* An Amazon SQS queue.
* AWS Identity and Access Management (IAM) roles.

The `setup.yaml` file was built from the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
source script here: 
[/resources/cdk/python_example_code_stepfunctions_demo/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/python_example_code_stepfunctions_demo/setup.ts). 

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/stepfunctions 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Step Functions service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/stepfunctions.html)
- [AWS Step Functions documentation](https://docs.aws.amazon.com/step-functions)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
