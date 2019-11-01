import json
import boto3
from aws_xray_sdk.core import xray_recorder #You will need to have the XRay SDK. For Lambda you will need this as a layer
from aws_xray_sdk.core import patch_all


def lambda_handler(event, context):
    #Sending messages to the queue
    
    patch_all()
    client = boto3.client('sqs')
    
    #Config
    i=0
    limit = 1000
    
    while i < limit:
        response = client.send_message(
            QueueUrl='<enter your SQS URL>',
            MessageBody='this is message ' + str(i)
        )
        print(
            "RequestId: " + response['ResponseMetadata']['HTTPHeaders']['x-amzn-requestid'] + " | " +
            "TimeStamp: " + response['ResponseMetadata']['HTTPHeaders']['date'] + " | "
            "MessageId: " + response['MessageId']
        )
        i += 1
