import json
import boto3


def lambda_handler(event, context):
    #Sending messages to the queue
    
    client = boto3.client('sqs', endpoint_url='https://sqs.us-east-1.amazonaws.com:443') #used for VPC endpoints. For FIFO queues, you will need to enable Content-Based Deduplication, or  provide this as an extra argument
    
    #Config
    i=0
    limit = 1000
    
    while i < limit:
        response = client.send_message(
            QueueUrl='<Enter your Queue URL>',
            MessageBody='this is message ' + str(i)
        )
        print(
            "RequestId: " + response['ResponseMetadata']['HTTPHeaders']['x-amzn-requestid'] + " | " +
            "TimeStamp: " + response['ResponseMetadata']['HTTPHeaders']['date'] + " | "
            "MessageId: " + response['MessageId']
        )
        i += 1
