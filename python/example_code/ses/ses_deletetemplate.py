import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.delete_template(
  TemplateName = 'TEMPLATE_NAME'
)

print(response)
