# Cross-service examples

The examples in this folder use multiple Amazon Web Services to perform more
complex scenarios.

## Examples

### [AnniversarySNS](./Anniversary_SNS)

**Services used:** Lambda, DynamoDB, SNS

In this example, a lambda function, automatically executed by AWS
CloudFormation scheduled events, scans a DynamoDB table for employees having a
one-year work anniversary. If found, it uses the Amazon Simple Notification
Service to send the employee a text message.

