# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[CreateTrainandRunCustomEntityDetector.py demonstrates how to create and identify the custom entities in a document.]
# snippet-service:[comprehend]
# snippet-keyword:[Amazon Comprehend]
# snippet-keyword:[Python]
# snippet-sourcedate:[2019-03-13]
# snippet-sourceauthor:[AWS]

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

# snippet-start:[comprehend.python.CreateTrainandRunCustomEntityDetector.complete]
import boto3
import time
import uuid

comprehend = boto3.client('comprehend')
response = comprehend.create_entity_recognizer(
    RecognizerName="Recognizer-Name-Goes-Here-{}".format(str(uuid.uuid4())),
    LanguageCode="en",
    DataAccessRoleArn="Role ARN",
    InputDataConfig={
        "EntityTypes": [
            {
                "Type": "ENTITY_TYPE"
            }
        ],
        "Documents": {
            "S3Uri": "s3://Bucket Name/Bucket Path/documents"
        },
        "Annotations": {
            "S3Uri": "s3://Bucket Name/Bucket Path/annotations"
        }
    }
)
recognizer_arn = response['EntityRecognizerArn']

response = comprehend.list_entity_recognizers()

trained = False
while not trained:

    response = comprehend.describe_entity_recognizer(
        EntityRecognizerArn=recognizer_arn
    )
    status = response['EntityRecognizerProperties']['Status']
    if status == 'IN_ERROR':
        exit(1)
    elif status == 'TRAINED':
        trained = True
        continue

    time.sleep(10)
    
    response = comprehend.start_entities_detection_job(
        EntityRecognizerArn=recognizer_arn,
        JobName='Detection-Job-Name-{}'.format(str(uuid.uuid4())),
        LanguageCode='en',
        DataAccessRoleArn='ROLE_ARN',
        InputDataConfig={
            'InputFormat': 'ONE_DOC_PER_LINE',
            'S3Uri': 's3://BUCKET_NAME/BUCKET_PATH/documents'
        },
        OutputDataConfig={
            'S3Uri': 's3://BUCKET_NAME/BUCKET_PATH/output'
        })
# snippet-end:[comprehend.python.CreateTrainandRunCustomEntityDetector.complete]
