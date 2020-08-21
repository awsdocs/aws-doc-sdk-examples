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
# snippet-sourcedescription:[rekognition-image-python-delete-collection.py demonstrates how to delete an Amazon Rekognition collection.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[DeleteCollection]
# snippet-keyword:[Collection]
# snippet-service:[rekognition]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-3]
# snippet-sourceauthor:[reesch (AWS)]
# snippet-start:[rekognition.python.rekognition-image-python-delete-collection.complete]

import boto3
from botocore.exceptions import ClientError
from os import environ

if __name__ == "__main__":

    # Replace collectionId with the ID of the collection that you want to delete.
    collectionId='MyCollection'
    print('Attempting to delete collection ' + collectionId)
    client=boto3.client('rekognition')
    statusCode=''
    try:
        response=client.delete_collection(CollectionId=collectionId)
        statusCode=response['StatusCode']
        
    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print ('The collection ' + collectionId + ' was not found ')
        else:
            print ('Error other than Not Found occurred: ' + e.response['Error']['Message'])
        statusCode=e.response['ResponseMetadata']['HTTPStatusCode']
    print('Operation returned Status Code: ' + str(statusCode))
    print('Done...')

    # snippet-end:[rekognition.python.rekognition-image-python-delete-collection.complete]



