### ChangeMsgVisibility/ChangeMsgVisibilityv2.go

This example sets the visibility timeout for a message in an Amazon SQS queue.

`go run ChangeMsgVisibilityv2.go -q QUEUE-NAME -h RECEIPT-HANDLE -v VISIBILITY`

- _QUEUE-NAME_ is the name of the queue.
- _RECEIPT-HANDLE_ is the receipt handle of the message.
- _VISIBILITY_ is the duration, in seconds, that the message is not visible to other consumers.
  The example ensures the value is between 0 and 12 hours;
  the default is 30 seconds.

The unit test accepts similar values in _config.json_.
