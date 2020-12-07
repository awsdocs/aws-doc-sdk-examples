### Subscribe.go

This example subscribes a user, by email address, to an Amazon SNS topic.

`go run Subscribe.go -m EMAIL-ADDRESS -t TOPIC-ARN`

- _EMAIL-ADDRESS_ is the email address of the user subscribing to the topic.
- _TOPIC-ARN_ is the ARN of the topic.

The unit test accepts a similar value in _config.json_.
