# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import boto3

# The Amazon Resource Name (ARN) of the state machine to execute.
# Example - arn:aws:states:us-west-2:112233445566:stateMachine:HelloWorld-StateMachine
STATE_MACHINE_ARN = 'statemachineArn'

# Updated States Language definition of the state machine
HELLO_WORLD_SF_DEF = '{"StartAt": "HelloWorld", "States": ' \
                     '{"HelloWorld": {"Type": "Pass", "Result": "Hello World!", "End": true}}}'

# Arn of the IAM role to use for this state machine
# Replace the value with a valid RoleArn
ROLE_ARN = 'roleArn'

sfn = boto3.client('stepfunctions')

response = sfn.update_state_machine(
    stateMachineArn=STATE_MACHINE_ARN,
    definition=HELLO_WORLD_SF_DEF,
    roleArn=ROLE_ARN
)

print(response)



#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[sfn_update_state_machine.py demonstrates how to update statemachine]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Step Functions]
#snippet-service:[states]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-11-26]
#snippet-sourceauthor:[nprajilesh]