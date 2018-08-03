import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.create_receipt_rule_set(
  RuleSetName = 'NAME',
)

print(response)
