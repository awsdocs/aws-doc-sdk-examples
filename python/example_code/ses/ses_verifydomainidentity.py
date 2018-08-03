import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.verify_domain_identity(
  Domain='DOMAIN_NAME'
)

print(response)
