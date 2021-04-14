### DeleteMessagev2.go

This example deletes a message from an Amazon SQS queue.

`go run DeleteMessagev2.go -q QUEUE-NAME -m MESSAGE-HANDLE`

- _QUEUE-NAME_ is the name of the queue from which the message is deleted.
- _MESSAGE-HANDLE_ is the handle of the message to delete.

The unit test accepts similar values in _config.json_.
