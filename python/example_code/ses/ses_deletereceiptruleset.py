import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.delete_receipt_rule_set(
  RuleSetName = 'RULE_SET_NAME'
)

print(response)
