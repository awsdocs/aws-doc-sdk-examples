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
# snippet-sourcedescription:[rekognition-image-python-image-orientation-bounding-box.py demonstrates how to get face bounding box locations for celebrities recognized in an image.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[RecognizeCelebrities]
# snippet-keyword:[Bounding Box]
# snippet-keyword:[Local]
# snippet-keyword:[Image]
# snippet-service:[rekognition]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-3]
# snippet-sourceauthor:[reesch (AWS)]
# snippet-start:[rekognition.python.rekognition-image-python-image-orientation-bounding-box.complete]
import boto3
import io
from PIL import Image

# Calculate positions from from estimated rotation 
def ShowBoundingBoxPositions(imageHeight, imageWidth, box, rotation): 
    left = 0
    top = 0
      
    if rotation == 'ROTATE_0':
        left = imageWidth * box['Left']
        top = imageHeight * box['Top']
    
    if rotation == 'ROTATE_90':
        left = imageHeight * (1 - (box['Top'] + box['Height']))
        top = imageWidth * box['Left']

    if rotation == 'ROTATE_180':
        left = imageWidth - (imageWidth * (box['Left'] + box['Width']))
        top = imageHeight * (1 - (box['Top'] + box['Height']))

    if rotation == 'ROTATE_270':
        left = imageHeight * box['Top']
        top = imageWidth * (1- box['Left'] - box['Width'] )

    print('Left: ' + '{0:.0f}'.format(left))
    print('Top: ' + '{0:.0f}'.format(top))
    print('Face Width: ' + "{0:.0f}".format(imageWidth * box['Width']))
    print('Face Height: ' + "{0:.0f}".format(imageHeight * box['Height']))


if __name__ == "__main__":

    # Replace photo with the image file you want to use.
    photo='moviestars.jpg'
    client=boto3.client('rekognition')
 

    #Get image width and height
    image = Image.open(open(photo,'rb'))
    width, height = image.size

    print ('Image information: ')
    print (photo)
    print ('Image Height: ' + str(height)) 
    print('Image Width: ' + str(width))    


    # call detect faces and show face age and placement
    # if found, preserve exif info
    stream = io.BytesIO()
    if 'exif' in image.info:
        exif=image.info['exif']
        image.save(stream,format=image.format, exif=exif)
    else:
        image.save(stream, format=image.format)    
    image_binary = stream.getvalue()
   
    response = client.recognize_celebrities(Image={'Bytes': image_binary})

    if 'OrientationCorrection'  in response:
        print('Orientation: ' + response['OrientationCorrection'])
    else: 
        print('No estimated orientation. Check Exif')    
    
    print()
    print('Detected celebrities for ' + photo) 

    for celebrity in response['CelebrityFaces']:
        print ('Name: ' + celebrity['Name'])
        print ('Id: ' + celebrity['Id'])
        
        if 'OrientationCorrection'  in response:            
            ShowBoundingBoxPositions(height, width, celebrity['Face']['BoundingBox'], response['OrientationCorrection'])

        print()

# snippet-end:[rekognition.python.rekognition-image-python-image-orientation-bounding-box.complete]
    