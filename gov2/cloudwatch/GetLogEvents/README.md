### GetLogEventsv2.go

This example displays events stored in Amazon CloudWatch log groups.

`go run GetLogEventsv2.go -g LOG-GROUP -s LOG-STREAM`

- _LOG-GROUP_ is the name of the CloudWatch Log Group.
- _LOG-STREAM_ is within a Log Group, contains events.

The unit test accepts similar values in _config.json_.