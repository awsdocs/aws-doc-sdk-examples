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
# snippet-sourcedescription:[rekognition-image-get-celebrity-info.py demonstrates how to get information about a detected celebrity.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Rekognition]
# snippet-keyword:[GetCelebrityInfo]
# snippet-service:[rekognition]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-3]
# snippet-sourceauthor:[reesch (AWS)]
# snippet-start:[rekognition.python.rekognition-image-python-get-celebrity-info.complete]
import boto3

if __name__ == "__main__":

    # Change the value of id to an ID value returned by RecognizeCelebrities or GetCelebrityRecognition

    id="nnnnnnnn"

    client=boto3.client('rekognition')

    #Display celebrity info
    print('Getting celebrity info for celebrity: ' + id)
    response=client.get_celebrity_info(Id=id)

    print (response['Name'])  
    print ('Further information (if available):')
    for url in response['Urls']:
        print (url) 
# snippet-end:[rekognition.python.rekognition-image-python-get-celebrity-info.complete]
