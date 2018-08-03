import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.list_templates(
  MaxItems='10'
)

print(response)
