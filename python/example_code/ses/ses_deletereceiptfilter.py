import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.delete_receipt_filter(
  FilterName = 'NAME'
)

print(response)
