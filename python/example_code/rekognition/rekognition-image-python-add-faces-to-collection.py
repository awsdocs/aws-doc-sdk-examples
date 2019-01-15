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
#snippet-sourcedescription:[rekognition-image-python-add-faces-to-collection.py demonstrates how to add faces, detected in an image, to an Amazon Rekognition collection.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Rekognition]
#snippet-keyword:[IndexFaces]
#snippet-keyword:[Collection]
#snippet-keyword:[Image]
#snippet-service:[rekognition]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-3]
#snippet-sourceauthor:[reesch (AWS)]
#snippet-start:[rekognition.python.rekognition-image-python-add-faces-to-collection.complete]

import boto3

if __name__ == "__main__":
    # replace bucket, collectionId, and photo with your values.
    bucket='bucket'
    collectionId='MyCollection'
    photo='photo'
    
    client=boto3.client('rekognition')

    response=client.index_faces

    response=client.index_faces(CollectionId=collectionId,
                                Image={'S3Object':{'Bucket':bucket,'Name':photo}},
                                ExternalImageId=photo,
                                MaxFaces=1,
                                QualityFilter="AUTO",
                                DetectionAttributes=['ALL'])

    print ('Results for ' + photo) 	
    print('Faces indexed:')						
    for faceRecord in response['FaceRecords']:
         print('  Face ID: ' + faceRecord['Face']['FaceId'])
         print('  Location: {}'.format(faceRecord['Face']['BoundingBox']))

    print('Faces not indexed:')
    for unindexedFace in response['UnindexedFaces']:
        print(' Location: {}'.format(unindexedFace['FaceDetail']['BoundingBox']))
        print(' Reasons:')
        for reason in unindexedFace['Reasons']:
            print('   ' + reason)

#snippet-end:[rekognition.python.rekognition-image-python-add-faces-to-collection.complete]