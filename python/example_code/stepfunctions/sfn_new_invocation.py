# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

#The Amazon Resource Name (ARN) of the state machine to execute.
STATE_MACHINE_ARN = 'sfArn'

#The name of the execution
EXECUTION_NAME = 'name'

#The string that contains the JSON input data for the execution
INPUT = "{}"

sfn = boto3.client('stepfunctions')

response = sfn.start_execution(
    stateMachineArn=STATE_MACHINE_ARN,
    name=EXECUTION_NAME,
    input=INPUT
)

#display the arn that identifies the execution
print(response.get('stateMachineArn'))
