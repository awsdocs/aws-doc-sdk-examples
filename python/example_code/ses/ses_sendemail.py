import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.send_email(
  Source          = 'SENDER_EMAIL_ADDRESS',
  Destination     = {
    'CcAddresses' : [
      'EMAIL_ADDRESS',
    ],
    'ToAddresses' : [
      'EMAIL_ADDRESS',
    ],
    'BccAddresses': [
      'EMAIL_ADDRESS',
    ]
  },

  Message = {
    'Subject'    : {
      'Data'     : 'TEST_EMAIL',
      'Charset'  : 'UTF-8'
    },
    'Body'       : {
      'Text'     : {
        'Data'   : 'TEXT_FORMAT_BODY',
        'Charset': 'UTF-8'
      },
      'Html'     : {
        'Data'   : 'HTML_FORMAT_BODY',
        'Charset': 'UTF-8'
      }
    }
  },

  ReplyToAddresses = [
    'EMAIL_ADDRESS',
  ],
)

print(response)
