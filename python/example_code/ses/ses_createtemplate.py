import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.create_template(
  Template = {
    TemplateName : 'TEMPLATE_NAME',
    SubjectPart  : 'SUBJECT_LINE',
    TextPart     : 'TEXT_CONTENT',
    HtmlPart     : 'HTML_CONTENT'
  }
)


print(response)
