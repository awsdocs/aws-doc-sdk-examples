### DeadLetterQueuev2.go

This example configures an Amazon SQS queue for messages 
that could not be delivered to another queue.

`go run DeadLetterQueuev2.go -q QUEUE-NAME -d DEAD-LETTER-QUEUE-NAME`

- _QUEUE-NAME_ is the name of the queue from which the dead letters are sent.
- _DEAD-LETTER-QUEUE-NAME_ is the name of the queue to which the dead letters are sent.

The unit test accepts similar values in _config.json_.
