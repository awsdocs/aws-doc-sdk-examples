import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.create_receipt_rule(
  RuleSetName   = 'RULE_SET_NAME',
  Rule          = {
    'Name'      : 'RULE_NAME',
    'Enabled'   : True | False,
    'TlsPolicy' : 'Require' | 'Optional',
    'Recipients': [
      'DOMAIN | EMAIL_ADDRESS',
    ],
    'Actions'   : [
      {
        'S3Action'         : {
          'BucketName'     : 'S3_BUCKET_NAME',
          'ObjectKeyPrefix': 'email'
        }
      }
    ],
  }
)

print(response)
