# Receiving and deleting messages from the queue

import boto3

client = boto3.client('sqs')

i= 0
while i < 1:
    receive = client.receive_message(
        QueueUrl='<Provide your FIFO Queue URL>',
        MaxNumberOfMessages=1,
        AttributeNames=['All'],
    )
    print("~~Received~~" + "\n" +  
        "RequestId: " + receive['ResponseMetadata']['HTTPHeaders']['x-amzn-requestid'] + " | " +
        "Body: " + receive['Messages'][0]['Body'] + " | " +
        "MessageId: " + receive['Messages'][0]['MessageId'] + " | " +
        "TimeStamp: " + receive['ResponseMetadata']['HTTPHeaders']['date'] + "\n\n"
    )
    
    delete = client.delete_message(
        QueueUrl='<Provide your FIFO Queue URL>',
        ReceiptHandle=receive['Messages'][0]['ReceiptHandle']
    )
    print("~~Deleted~~" + "\n" +  
        "RequestId: " + receive['ResponseMetadata']['HTTPHeaders']['x-amzn-requestid'] 
    )
    
    i += 1
