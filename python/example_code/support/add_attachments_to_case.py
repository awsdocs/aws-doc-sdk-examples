# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# 
# http://aws.amazon.com/apache2.0/
# 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[add_attachments_to_case.py demonstrates how to use AWS Support API to add attachments to an existing support case.]
# snippet-service:[support]
# snippet-keyword:[Python]
# snippet-keyword:[AWS Support]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AddAttachmentsToSet]
# snippet-keyword:[AddCommunicationToCase]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-03-21]
# snippet-sourceauthor:[walkerk1980]
# snippet-start:[support.python.add_attachments_to_case.complete]

import boto3

# your support case id
case_id='case-123456789012-muen-2019-e23abb614ab25793'

# file name to attach to support case
file_name="test.jpg"

# body of communication to add to case
communication_body = 'add attachment test'

support = boto3.client('support', region_name = 'us-east-1')

with open(file_name, mode='rb') as file:
    file_data = file.read()

attachment1 = { 'fileName' : file_name ,'data' : file_data }

attachment_set = support.add_attachments_to_set(
    attachments=[
        attachment1,
    ]
) 

add_communication_response = support.add_communication_to_case(
    caseId = case_id,
    communicationBody = communication_body,
    attachmentSetId = attachment_set['attachmentSetId'],
)

if add_communication_response['result']:
    print('Communication with attachment successfully added.')

# snippet-end:[support.python.add_attachments_to_case.complete]
