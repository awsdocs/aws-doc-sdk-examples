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

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[RunCustomClassifier.py demonstrates how to identify custom classifiers in a corpus of documents .]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Comprehend]
# snippet-keyword:[RunCustomClassifier]
# snippet-keyword:[custom classifier]
# snippet-service:[comprehend]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[ (AWS)]
# snippet-start:[comprehend.python.RunCustomClassifier.complete]

import boto3


# Instantiate Boto3 SDK:
client = boto3.client('comprehend', region_name='region')

start_response = client.start_document_classification_job(
    InputDataConfig={
        'S3Uri': 's3://srikad-us-west-2-input/docclass/file name',
        'InputFormat': 'ONE_DOC_PER_LINE'
    },
    OutputDataConfig={
        'S3Uri': 's3://S3Bucket/output'
    },
    DataAccessRoleArn='arn:aws:iam::account number:role/resource name',
    DocumentClassifierArn=
    'arn:aws:comprehend:region:account number:document-classifier/SampleCodeClassifier1'
)

print("Start response: %s\n", start_response)

# Check the status of the job
describe_response = client.describe_document_classification_job(JobId=start_response['JobId'])
print("Describe response: %s\n", describe_response)

# List all classification jobs in account
list_response = client.list_document_classification_jobs()
print("List response: %s\n", list_response)

              
# snippet-end:[comprehend.python.RunCustomClassifier.complete]
  