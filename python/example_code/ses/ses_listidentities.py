import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.list_identities(
  IdentityType = 'EMAIN_ADDRESS' | 'DOMAIN',
  MaxItems = 10
)

print(response)
