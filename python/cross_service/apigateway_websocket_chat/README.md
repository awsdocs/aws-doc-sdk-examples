# Amazon API Gateway websocket chat example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon API Gateway V2 to
create a websocket API that integrates with AWS Lambda and Amazon DynamoDB.

* Create a websocket API served by API Gateway.
* Define a Lambda handler that stores connections in DynamoDB and posts messages to
other chat participants.
* Connect to the websocket chat application and send messages with the Websockets
package.

You can create a similar API Gateway websocket chat application by using 
[AWS Chalice](https://github.com/aws/chalice).
For a tutorial, see 
[Chat Server Example](https://aws.github.io/chalice/tutorials/wschat.html).

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.5 or later
- Boto3 1.15.4 or later
- Websockets 8.1 or later
- PyTest 6.0.2 or later (to run unit tests)

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

This example requires AWS resources that can be deployed by the 
AWS CloudFormation stack that is defined in the accompanying `setup.yaml` file.
This stack manages the following resources:

* A DynamoDB table with a specific key schema.
* A Lambda function that handles API Gateway websocket request events. 
* An AWS Identity and Access Management (IAM) role that grants permission to let 
Lambda run the function and perform actions on the table.  

### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` flag at 
a command prompt.

```
python websocket_chat.py deploy
```

### Run the usage demonstration

Create and deploy the API Gateway websocket API by running with the `demo` flag at 
a command prompt.

```
python websocket_chat.py demo
``` 

### Run the chat demonstration

See an automated demo of how to use the Websockets package to connect and send 
messages to the chat application by running with the `chat` flag at a command prompt.

```
python websocket_chat.py chat
``` 

*Note:* The Lambda handler for the chat application writes to an
Amazon CloudWatch log. Checking this log can help you troubleshoot issues and give 
additional insight into the application.

### Destroy resources

Destroy example resources by running the script with the `destroy` flag at a command 
prompt.

```
python websocket_chat.py destroy
``` 

### Example structure

The example contains the following files.

**lambda_chat.py**

Shows how to implement an AWS Lambda function as part of a websocket chat application.
The function handles messages from an Amazon API Gateway websocket API and uses an
Amazon DynamoDB table to track active connections by taking the following actions. 

* A `$connect` request adds a connection ID and the associated user name to the
DynamoDB table.
* A `sendmessage` request scans the table for connections and uses the API 
Gateway Management API to post the message to all other connections.
* A `$disconnect` request removes the connection record from the table.

**websocket_chat.py**

Shows how to use API Gateway V2 to create a websocket API that is backed by a 
Lambda function. The `usage_demo` and `chat_demo` scripts in this file show how to 
accomplish the following actions.

1. Create a websocket API served by API Gateway.
1. Add resources to the websocket API that represent websocket connections and 
chat messages.
1. Add integration methods so the websocket API uses a Lambda function to handle 
incoming requests. 
1. Use the Websockets package to connect users to the chat application and send 
messages to other chat participants.

**setup.yaml**

Contains a CloudFormation script that is used to create the resources needed for 
the demo. Pass the `deploy` or `destroy` flag to the `websocket_chat.py` script to 
create or remove these resources:  

* A DynamoDB table
* A Lambda function 
* An IAM role

The `setup.yaml` file was built from the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
source script here: 
[/resources/cdk/python_example_code_apigateway_websocket/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/python_example_code_apigateway_websocket/setup.ts). 

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your 
[GitHub root]/python/example_code/apigateway/websocket folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon API Gateway V2 service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/apigatewayv2.html)
- [Boto3 Amazon API Gateway Management API service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/apigatewaymanagementapi.html)
- [Amazon API Gateway documentation](https://docs.aws.amazon.com/apigateway/)
- [AWS Lambda documentation](https://docs.aws.amazon.com/lambda/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
