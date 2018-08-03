import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.send_templated_email(
  Source='EMAIL_ADDRESS',
  Destination={
    'ToAddresses': [
      'EMAIL_ADDRESS',
    ],
    'CcAddresses': [
      'EMAIL_ADDRESS',
    ]
  },
  ReplyToAddresses=[
    'EMAIL_ADDRESS',
  ],
  Template='TEMPLATE_NAME',
  TemplateData='{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }'
)

print(response)
