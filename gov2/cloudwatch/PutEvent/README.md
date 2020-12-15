### PutEventv2.go

This example sends an Amazon CloudWatch event to Amazon EventBridge.

`go run PutEventv2.go -l LAMBDA-ARN -f EVENT-FILE`

- _LAMBDA-ARN_ is the ARN of the AWS Lambda function of which the event is concerned.
- _EVENT-FILE_ is the local file specifying details of the event to send to Amazon EventBridge.

The unit test accepts similar values in _config.json_.
