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
#snippet-sourcedescription:[rekognition-image-python-show-bounding-boxes-s3.py demonstrates how to detect display bounding boxes around faces detected in an image loaded from an S3 Bucket.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Rekognition]
#snippet-keyword:[DetectFaces]
#snippet-keyword:[Bounding Box]
#snippet-keyword:[S3 Bucket]
#snippet-keyword:[Image]
#snippet-service:[rekognition]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-3]
#snippet-sourceauthor:[reesch (AWS)]
#snippet-start:[rekognition.python.rekognition-image-python-show-bounding-boxes-s3.complete]
import boto3
import io
from PIL import Image, ImageDraw, ExifTags, ImageColor

if __name__ == "__main__":
     
    # Change bucket to the S# bucket that contains the image file.
    # Change photo to the image filename. 
    bucket="bucket"
    photo="photo"
    client=boto3.client('rekognition')

    # Load image from S3 bucket
    s3_connection = boto3.resource('s3')
    s3_object = s3_connection.Object(bucket,photo)
    s3_response = s3_object.get()

    stream = io.BytesIO(s3_response['Body'].read())
    image=Image.open(stream)
    
    #Call DetectFaces 
    response = client.detect_faces(Image={'S3Object': {'Bucket': bucket, 'Name': photo}},
        Attributes=['ALL'])

    imgWidth, imgHeight = image.size  
    draw = ImageDraw.Draw(image)  
                    

    # calculate and display bounding boxes for each detected face       
    print('Detected faces for ' + photo)    
    for faceDetail in response['FaceDetails']:
        print('The detected face is between ' + str(faceDetail['AgeRange']['Low']) 
              + ' and ' + str(faceDetail['AgeRange']['High']) + ' years old')

        box = faceDetail['BoundingBox']
        left = imgWidth * box['Left']
        top = imgHeight * box['Top']
        width = imgWidth * box['Width']
        height = imgHeight * box['Height']
                

        print('Left: ' + '{0:.0f}'.format(left))
        print('Top: ' + '{0:.0f}'.format(top))
        print('Face Width: ' + "{0:.0f}".format(width))
        print('Face Height: ' + "{0:.0f}".format(height))

        points = (
            (left,top),
            (left + width, top),
            (left + width, top + height),
            (left , top + height),
            (left, top)

        )
        draw.line(points, fill='#00d400', width=2)

        # Alternatively can draw rectangle. However you can't set line width.
        #draw.rectangle([left,top, left + width, top + height], outline='#00d400') 


    image.show()

    #snippet-end:[rekognition.python.rekognition-image-python-show-bounding-boxes-s3.complete]

