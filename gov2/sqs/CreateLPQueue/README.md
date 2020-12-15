### CreateLPQueue/CreateLPQueuev2.go

This example creates a long-polling Amazon SQS queue.

`go run CreateLPQueuev2.go -q QUEUE-NAME [-w WAIT-TIME]`

- _QUEUE-NAME_ is the name of the queue to create.
- _WAIT-TIME_ is how long, in seconds, to wait.
  The example ensures the value is between 1 and 20;
  the default is 10.

The unit test accepts similar values in _config.json_.
