# AWS Step Functions examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Step Functions to
create and run state machines.

*AWS Step Functions makes it easy to coordinate the components of distributed 
applications as a series of steps in a visual workflow.* 

## Code examples

### API examples

* [Create a state machine](stepfunctions_basics.py)
(`CreateStateMachine`)
* [Delete a state machine](stepfunctions_basics.py)
(`DeleteStateMachine`)
* [Describe a state machine](stepfunctions_basics.py)
(`DescribeStateMachine`)
* [List state machine runs](stepfunctions_basics.py)
(`ListExecutions`)
* [List state machines](stepfunctions_basics.py)
(`ListStateMachines`)
* [Start a state machine run](stepfunctions_basics.py)
(`StartExecution`)
* [Stop a state machine run](stepfunctions_basics.py)
(`StopExecution`)
* [Update a state machine](stepfunctions_basics.py)
(`UpdateStateMachine`)

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
- Python 3.7 or later
- Boto3 1.14.47 or later
- PyTest 5.3.5 or later (to run unit tests)

### Command

This example shows how to implement basic Step Functions operations. For
a full example that can be run at a command prompt, see 
[python/cross_service/stepfunctions_messenger](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/python/cross_service/stepfunctions_messenger). 

### Example structure

The example contains the following file.

**stepfunctions_basics.py**

Shows how to use AWS Step Functions state machine APIs. 

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
