import boto3

# Create SES client
ses = boto3.client('ses')

response = ses.delete_receipt_rule(
  RuleName='RULE_NAME',
  RuleSetName='RULE_SET_NAME'
)

print(response)
