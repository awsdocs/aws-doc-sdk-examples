### Publishv2.go

This example publishes a message to an Amazon SNS topic.

`go run Publishv2.go -m MESSAGE -t TOPIC-ARN`

- _MESSAGE_ is the message to publish.
- _TOPIC-ARN_ is the ARN of the topic to which the message is published.

The unit test accepts similar values in _config.json_.
