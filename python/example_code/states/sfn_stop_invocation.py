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

# The Amazon Resource Name (ARN) of the execution to stop.
# Example - arn:aws:states:us-west-2:112233445566:execution:HelloWorld-StateMachine
EXECUTION_ARN = 'executionARN'


sfn = boto3.client('stepfunctions')

response = sfn.stop_execution(
    executionArn=EXECUTION_ARN,
)

#display the arn that identifies the execution
print(response)


#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[sfn_stop_invocation.py demonstrates how to stop a statemachine action using its ARN]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Step Functions]
#snippet-service:[states]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-10-26]
#snippet-sourceauthor:[jschwarzwalder (AWS)]