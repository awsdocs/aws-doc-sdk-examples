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

#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[rekognition-image-python-describe-collection.py demonstrates how to get a description of an Amazon Rekognition collection.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Rekognition]
#snippet-keyword:[DescribeCollection]
#snippet-keyword:[Collection]
#snippet-service:[rekognition]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-3]
#snippet-sourceauthor:[reesch (AWS)]
#snippet-start:[rekognition.python.rekognition-image-python-describe-collection.complete]

import boto3
from botocore.exceptions import ClientError
from os import environ

if __name__ == "__main__":

    # Change collectionID to the name of the desired collection.
    collectionId='MyCollection'
    print('Attempting to describe collection ' + collectionId)
    client=boto3.client('rekognition')

    try:
        response=client.describe_collection(CollectionId=collectionId)
        print("Collection Arn: "  + response['CollectionARN'])
        print("Face Count: "  + str(response['FaceCount']))
        print("Face Model Version: "  + response['FaceModelVersion'])
        print("Timestamp: "  + str(response['CreationTimestamp']))

        
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print ('The collection ' + collectionId + ' was not found ')
        else:
            print ('Error other than Not Found occurred: ' + e.response['Error']['Message'])
    print('Done...')
#snippet-end:[rekognition.python.rekognition-image-python-describe-collection.complete]




