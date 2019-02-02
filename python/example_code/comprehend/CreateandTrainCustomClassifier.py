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
# snippet-sourcedescription:[CreateandTrainCustomClassifier.py demonstrates how to create and train a custom classifier.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Comprehend]
# snippet-keyword:[CreateandTrainCustomClassifier]
# snippet-keyword:[custom classifier]
# snippet-service:[comprehend]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[ (AWS)]
# snippet-start:[comprehend.python.CreateandTrainCustomClassifier.complete]

import boto3


# Instantiate Boto3 SDK:
client = boto3.client('comprehend', region_name='region')

# Create a document classifier
create_response = client.create_document_classifier(
    InputDataConfig={
        'S3Uri': 's3://S3Bucket/docclass/file name'
    },
    DataAccessRoleArn='arn:aws:iam::account number:role/resource name',
    DocumentClassifierName='SampleCodeClassifier1',
    LanguageCode='en'
)
print("Create response: %s\n", create_response)

# Check the status of the classifier
describe_response = client.describe_document_classifier(
    DocumentClassifierArn=create_response['DocumentClassifierArn'])
print("Describe response: %s\n", describe_response)

# List all classifiers in account
list_response = client.list_document_classifiers()
print("List response: %s\n", list_response)

              
# snippet-end:[comprehend.python.CreateandTrainCustomClassifier.complete]
  