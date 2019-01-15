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
#snippet-sourcedescription:[rekognition-image-python-delete-faces-from-collection.py demonstrates how to delete a face from an Amazon Rekognition collection.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Rekognition]
#snippet-keyword:[DeleteFaces]
#snippet-keyword:[Collection]
#snippet-service:[rekognition]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-3]
#snippet-sourceauthor:[reesch (AWS)]
#snippet-start:[rekognition.python.rekognition-image-python-delete-faces-from-collection.complete]

import boto3

if __name__ == "__main__":

    # Change collectionID to the collection that contains the face.
    # Change "xxxxxx..." to the ID of the face that you want to delete.

    collectionId='MyCollection'
    faces=[]
    faces.append("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")

    client=boto3.client('rekognition')

    response=client.delete_faces(CollectionId=collectionId,
                               FaceIds=faces)
    
    print(str(len(response['DeletedFaces'])) + ' faces deleted:') 							
    for faceId in response['DeletedFaces']:
         print (faceId)
#snippet-end:[rekognition.python.rekognition-image-python-delete-faces-from-collection.complete]