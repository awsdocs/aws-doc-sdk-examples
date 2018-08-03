import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.list_receipt_filters()

print(response)
