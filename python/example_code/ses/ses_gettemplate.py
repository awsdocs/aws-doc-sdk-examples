import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.get_template(
  TemplateName = 'TEMPLATE_NAME'
)

print(response)
