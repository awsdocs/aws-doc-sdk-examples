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
# snippet-sourcedescription:[rekognition-image-python-recognize-celebrities.py demonstrates how to recognize celebrities in an image loaded from a local file system.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[RecognizeCelebrities]
# snippet-keyword:[Local]
# snippet-keyword:[Celebrity]
# snippet-keyword:[Image]
# snippet-service:[rekognition]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-3]
# snippet-sourceauthor:[reesch (AWS)]
# snippet-start:[rekognition.python.rekognition-image-python-recognize-celebrities.complete]
import boto3
import json

if __name__ == "__main__":
    # Change photo to the path and filename of your image. 
    photo='moviestars.jpg'
    
    client=boto3.client('rekognition')

    with open(photo, 'rb') as image:
        response = client.recognize_celebrities(Image={'Bytes': image.read()})

    print('Detected faces for ' + photo)    
    for celebrity in response['CelebrityFaces']:
        print ('Name: ' + celebrity['Name'])
        print ('Id: ' + celebrity['Id'])
        print ('Position:')
        print ('   Left: ' + '{:.2f}'.format(celebrity['Face']['BoundingBox']['Height']))
        print ('   Top: ' + '{:.2f}'.format(celebrity['Face']['BoundingBox']['Top']))
        print ('Info')
        for url in celebrity['Urls']:
            print ('   ' + url)
        print
# snippet-end:[rekognition.python.rekognition-image-python-recognize-celebrities.complete]
        
