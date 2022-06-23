# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to create a fully serverless REST API for Rekognition Content Moderation Solution with URL Support.
"""

import json
import boto3
import urllib3
import io


client = boto3.client('rekognition')
manager = urllib3.PoolManager()


def getModerationForUrl(url):
    try:
        extensions = ['jpg', 'jpeg', 'png']
        if not any(url.lower().endswith(ext) for ext in extensions):
            return 400, "Amazon Rekognition supports only the following image formats: jpg, jpeg, png"
        
        
        response = manager.request('GET', url, preload_content=False)
        if response.status == 404:
            return 404, "Image not found"
    
        try:
            reader = io.BufferedReader(response, 8)
            readBytes = reader.read()
        finally:
            if(reader is not None):
                reader.close()
     
        if(len(readBytes) > 5242880):
            return 400, "Amazon Rekognition does not support images more than 5MB in this implementation. Use images stored on Amazon S3. See here: https://docs.aws.amazon.com/rekognition/latest/dg/limits.html"
        
        response = client.detect_moderation_labels(Image={'Bytes': readBytes}, MinConfidence=60)
        return 200, response['ModerationLabels']
    
    except Exception as e:
        return 503, "Unexpected error: " + str(e)

def lambda_handler(event, context):
    print(f'event: {json.dumps(event)}')

    body = event.get('body')
    if body is None:
        raise KeyError("payload is missing")
        
    url = json.loads(body)['url']

    if url is None:
        raise KeyError("url is missing from the payload")
        
    moderationResponse = getModerationForUrl(url)
    print(f'returning moderationResponse: {json.dumps(moderationResponse)}')
   
    return {
        'statusCode': moderationResponse[0],
        'body': json.dumps(moderationResponse[1])
    }
