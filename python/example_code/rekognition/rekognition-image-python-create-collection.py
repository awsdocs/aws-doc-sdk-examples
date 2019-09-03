# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[rekognition-image-python-create-collection.py demonstrates how to create an Amazon Rekognition collection.]
# snippet-service:[rekognition]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-3]
# snippet-sourceauthor:[reesch (AWS)]

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

# snippet-start:[rekognition.python.rekognition-image-python-create-collection.complete]

import boto3

if __name__ == "__main__":

    # Replace collectionID with the name of the collection that you want to create.
    maxResults = 2
    collectionId = 'MyCollection'

    # Create a collection
    print('Creating collection:' + collectionId)

    client=boto3.client('rekognition')
    response = client.create_collection(CollectionId=collectionId)

    print('Collection ARN: ' + response['CollectionArn'])
    print('Status code: ' + str(response['StatusCode']))
    print('Done...')

# snippet-end:[rekognition.python.rekognition-image-python-create-collection.complete]
