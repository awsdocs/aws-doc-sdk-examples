# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[rekognition-image-python-show-bounding-boxes-local.py demonstrates how to draw bounding boxes around faces in images. The image file is local.]
# snippet-service:[rekognition]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
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

# snippet-start:[rekognition.python.rekognition-image-python-show-bounding-boxes-local.complete]

import boto3
import io
from PIL import Image, ImageDraw, ExifTags, ImageColor

if __name__ == "__main__":

    # Change photo to the path and filename of your image.
    photo = 'photo'

    # Open image and get image data from stream.
    image = Image.open(open(photo,'rb'))
    stream = io.BytesIO()
    image.save(stream, format=image.format)
    image_binary = stream.getvalue()

    client = boto3.client('rekognition')
    response = client.detect_faces(Image={'Bytes': image_binary},
                                   Attributes=['ALL'])

    draw = ImageDraw.Draw(image)

    # Calculate and display a bounding box around each detected face
    print('Detected faces for ' + photo)
    for faceDetail in response['FaceDetails']:
        print('The detected face is between ' +
              str(faceDetail['AgeRange']['Low']) +
              ' and ' + str(faceDetail['AgeRange']['High']) +
              ' years old')

        box = faceDetail['BoundingBox']
        imgWidth, imgHeight = image.size
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
        draw.line(points, fill='#00d400', width=5)

        # Alternatively can draw rectangle. However you can't set line width.
        # draw.rectangle([left,top, left + (width * box['Width']), top +(height * box['Height'])], outline='yellow')

    image.show()

# snippet-end:[rekognition.python.rekognition-image-python-show-bounding-boxes-local.complete]
