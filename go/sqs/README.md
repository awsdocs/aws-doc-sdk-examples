# AWS SDK for Go code examples for Amazon SQS

## Purpose

These examples demonstrate how to perform various Amazon SQS operations.
For information about Amazon SQS,
see the
[Amazon Simple Queue Service Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide).

## Prerequisites

You must have an AWS account and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the *AWS SDK for Go Developer Guide*.

## Caveats

- You should grant these code examples least privilege,
  or at most the minimum  permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running the examples might result in charges to your AWS account.

## About the unit tests

The unit tests should delete any resources that they create.
However, they might result in charges to your 
AWS account.

To run a unit test, enter the following:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files.

```
PASS
ok      PATH 1.904s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous shown output.

## About the code examples

All of the code examples perform the operations in your default AWS Region
and use your default credentials.
See [Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html) for details.

Each example is in its own directory, with a unit test.
Each example can be run on the command line, and most require at least one command-line argument.
To see the required command-line arguments, enter the following, where *FILENAME* is the name of the Go file.

`go run FILENAME`

The example displays an error message with information about the required command-line arguments.

### ChangeMsgVisibilty

This directory contains an example of changing the visibility of a message,
which prevents other consumers from processing the message.
For information about message visibility, see
[Amazon SQS Visibility Timeout](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html).

Use the following command to set the visibility of a message,
where:

- QUEUE is the name of the queue.
- MESSAGE-HANDLE is the receipt handle of the message.
  Get that value by using the **ReceiveMessages** example.
- VISIBILITY is how long, in seconds, the message is not available to other consumers.
  The example ensures that the value is between 0 and 43200 ( 12 hours).
  If omitted, it defaults to 30.

`go run ChangeMsgVisibility.go -q QUEUE -h MESSAGE-HANDLE [-v VISIBILITY]`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of the queue.
  The default is an empty string.
- **Visibility** is how long, in seconds, the message is not available to other consumers.
  The default is 30.
- **WaitTime** is the wait time, in seconds, the queue waits for the message to arrive.
  The default is 10.

The unit test:

1. Gets the values from *config.json* and stores them in a struct,
   ensuring that the **Visibility** value is between 0 and 43200 (12 hours)
   and the **WaitTime** value is between 0 (no long polling) and 20.
2. If the value of **Queue** is an empty string,
    it creates a queue with a random name starting with **myqueue-**.
3. If the value of **WaitTime** is greater than 0,
    it configures the queue for long polling with that value.
4. Sends a message to the queue.
5. Retrieves the message from the queue.
6. Sets the message visibility to the value of **Visibility**.
7. Deletes the message.
8. If it created the queue, it deletes the queue.

### ConfigureLPQueue

This directory contains an example of configuring a queue to use long polling,
where the queue waits for a message to arrive.
For information about long polling, see
[Amazon SQS Short and Long Polling](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-short-and-long-polling.html).

Use the following command to set the long polling value for a queue,
where:

- QUEUE is the name of the queue.
- WAIT-TIME is how long, in seconds, the queue waits for messages.
  The example ensures that the value is between 1 and 20.
  If omitted, it defaults to 10.

`go run ConfigureLPQueue.go -q QUEUE [-d WAIT-TIME]`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of the queue.
  The default is an empty string.
- **WaitTime** is the wait time for long polling.
  The default is 10.

The unit test:

1. Gets the values from *config.json* and stores them in a struct,
   ensuring that the **WaitTime** value is between 1 and 20.
2. If the value of **Queue** is an empty string,
   it creates a queue with a random name starting with **mylpqueue-**.
3. Configures the queue for long polling with the value of **WaitTime**.
4. If it created the queue, it deletes the queue.

### CreateLPQueue

This directory contains an example of creating a queue using long polling,
where the queue waits for a message to arrive.
For information about long polling, see
[Amazon SQS Short and Long Polling](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-short-and-long-polling.html).

Use the following command to create a queue using long polling,
where:

- QUEUE is the name of the queue.
- WAIT-TIME is the wait time, in seconds, the queue uses for long polling.
  The example ensures that the value is between 1 and 20.
  If omitted, it defaults to 10.

`go run CreateLPQueue.go -q QUEUE [-d WAIT-TIME]`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of the queue.
  The default is an empty string.
- **WaitTime** is the wait time for long polling.
  The default is 10.

The unit test:

1. Gets the values from *config.json* and stores them in a struct,
   ensuring that the **WaitTime** value is between 1 and 20.
2. If the value of **Queue** is an empty string,
   it creates a random name starting with **mylpqueue-**.
3. Creates the queue with the name in **Queue** and the long polling value of **WaitTime**.
4. If it created the queue, it deletes the queue.

### CreateQueue

This directory contains an example of creating a queue.
For information about creating queues, see
[Tutorials: Creating Amazon SQS Queues](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-tutorials-create-queues.html).

Use the following command to create a queue,
where:

- QUEUE is the name of the queue.

`go run CreateQueue.go -q QUEUE`

The unit test accepts the following configuration value in *config.json* and stores it in a struct:

- **Queue** is the name of the queue.

The unit test:

1. Gets the value from *config.json* and stores it in a struct.
    If the value of **Queue** is an empty string,
    it creates a random value for **Queue** that starts with **myqueue-**.
2. Creates a queue with the name from **Queue**.
3. If it created the queue with a random name, it deletes the queue.

### DeadLetterQueue

This directory contains an example of configuring an Amazon SQS queue for messages that could not be delivered to another queue.
For information about dead-letter queues, see
[Tutorial: Configuring an Amazon SQS Dead-Letter Queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-configure-dead-letter-queue.html).

Use the following command to create a dead-letter queue,
where:

- QUEUE is the name of the queue.
- DLQUEUE is the name of the dead-letter queue.

`go run DeadLetterQueue.go -q QUEUE -d DLQUEUE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **DLQueue** is the name of the dead-letter queue.
  The default is an empty string.
- **Queue** is the name of the queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
   it creates a queue with a name beginning with **myqueue-**.
3. If the value of **DLQueue** is an empty string,
   it creates a dead-letter queue with a name beginning with **mydlqueue-**.
4. Configures the dead-letter queue to receive undelivered messages from the queue.
5. If it created the queue, it deletes the queue.
6. If it created the dead-letter queue, it deletes the dead-letter queue.   

### DeleteMessage

This directory contains an example of deleting a message from a queue.
For information about deleting messages, see
[Tutorial: Receiving and Deleting a Message from an Amazon SQS Queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-receive-delete-message.html).

Use the following command to delete a message from a queue,
where:

- QUEUE is the name of the queue.
- MESSAGE-HANDLE is the receipt handle of the message.

`go run DeleteMessage.go -q QUEUE -m MESSAGE-HANDLE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **MsgHandle** is the receipt handle of the message.
  The default is an empty string.
- **Queue** is the name of the queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
   it creates a queue with a random name beginning with **myqueue-**.
3. If the value of **MsgHandle** is an empty string,
   it creates a message and sends it to the queue.
4. It deletes the message.
5. If it created the queue, it deletes the queue.

### DeleteQueue

This directory contains an example of deleting a queue.
For information about deleting queues, see
[Tutorial: Deleting an Amazon SQS Queue](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-delete-queue.html).

Use the following command to delete a queue,
where:

- QUEUE is the name of the queue.

`go run DeleteQueue.go -q QUEUE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of the queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
   it creates a queue with a random name beginning with **myqueue-**.
3. It deletes the queue.

### GetQueueURL

This directory contains an example of retrieving the URL of a queue.

Use the following command to retrieve the URL of a queue,
where:

- QUEUE is the name of the queue.

`go run GetQueueURL.go -q QUEUE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of the queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
   it creates a new queue with a random name starting with **myqueue-**.
3. Gets and displays the URL of the queue.
4. If it created a queue, it deletes the queue.

### ListQueues

This directory contains an example of listing queues.

Use the following command to list your queues.

`go run ListQueues.go`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Confirm** specifies whether to create a new queue and see if it shows up in the list of queues.
  The default is false.
- **SleepSeconds** specifies how long, in seconds, to wait after creating a queue and listing it.
  the default is 60.
  
NOTE: If you set **Confirm** to true,
the unit test sleeps **SleepSeconds** before listing the queues,
which adds a considerable amount of time to running the unit test.

The unit test:

1. Gets the values from *config.json* and stores them in a struct,
   ensuring that the **SleepSeconds** value is between 0 and 60.
2. If **Confirm** is true,
    it sleeps **Confirm** seconds after creating the queue and before listing the queues.
    In addition, it displays a message when it finds the created queue in the list.
    If **Confirm** is false,
    it won't likely find the created queue in the list.
4. If it created a queue, it deletes the queue.

### ReceiveLPMessage

This directory contains an example of receiving a message in a long polling queue.
For information about long polling, see
[Amazon SQS Short and Long Polling](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-short-and-long-polling.html).

Use the following command to get a message from a long polling queue,
where:

- QUEUE is the name of the long polling queue.
- VISIBILITY is how long, in seconds, the message is hidden from other consumers.
   The example ensures that the value is between 1 and 43200 (12 hours).
   If omitted, it defaults to 5.
- WAIT-TIME is how long, in seconds, the queue waits for messages.
   The example ensures that the value is between 0 and 20.
   If omitted, it defaults to 10.

`go run ReceiveLPMessage.go -q QUEUE [-v VISIBILITY] [-w WAIT-TIME]`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Message** is the message sent to the queue.
   The default is an empty string.
- **Queue** is the name of the queue.
   The default is an empty string.
- **Visibility** is how long, in seconds, the message is hidden from other consumers.
   The default is 5.
- **WaitTime** is how long, in seconds, the queue waits for messages.
   The default is 10.

The unit test:

1. Gets the values from *config.json* and stores them in a struct,
   ensuring that the **Visibility** value is between 1 and 43200
   and the **WaitTime** value is between 0 and 20.
2. If the **Queue** value is an empty string,
    creates a new queue with a random name starting with **mylpqueue-**.
3. If the **Message** value is an empty string,
    creates a message with the current date and time.
4. Sends the message to the queue.
5. Retrieves the message from the queue.
6. Tests whether the message received is the message sent.
7. Deletes the message.
8. If it created a queue, it deletes the queue.

### ReceiveMessage

This directory contains an example of receiving a message from a queue.

Use the following command to receive a message from a queue,
where:

- QUEUE is the name of the queue.
- VISIBILITY is how long, in seconds, the message is not available to other consumers.
   The example ensures that the value is between 0 and 43200 (12 hours).
   If omitted, it defaults to 5.

`go run ReceiveMessage.go -q QUEUE [-t VISIBILITY]`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Message** is the body of the message to send and receive.
   The default is an empty string.
- **Queue** is the name of the queue.
   The default is an empty string.
- **SleepSeconds** is how long to wait, in seconds, after sending the message, before attempting to read the message.
   The example uses this in lieu of configuring the queue for long polling.
- **Visibility** is how long, in seconds, the message is not available to other consumers.
   The default is 5.
   
The unit test:

1. Gets the values from *config.json* and stores them in a struct,
    ensuring that the **Visibility** value is between 0 and 43200.
2. If the value of **Queue** is an empty string,
    it creates a queue with a random name starting with **myqueue-**.
3. If the value of **Message** is an empty string,
    it creates a message with the current date and time.
4. Sends the message.
5. Waits **SleepSeconds** seconds.
6. It receives the message.
7. It deletes the message.
8. If it created a queue, it deletes the queue.

### SendMessage

This directory contains an example of sending a message to a queue.

Use the following command to send a message to a queue,
where:

- QUEUE is the name of a queue.

`go run SendMessage.go -q QUEUE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the name of a queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
    it creates a new queue with a random name starting with **myqueue**.
3. It sends a message to the queue.
4. It receives the message.
5. It deletes the message.
6. If it created a queue, it deletes the queue.

### SendReceiveLongPolling

This directory contains an example of sending a message to and receiving a message in a long polling queue.

Use the following command to send a message to and receive a message in a long polling queue.
where:

- QUEUE is the name of a long polling queue.

`go run SendReceiveLongPolling.go -q QUEUE`

The unit test accepts the following configuration values in *config.json* and stores them in a struct:

- **Queue** is the URL of the queue.
  The default is an empty string.

The unit test:

1. Gets the values from *config.json* and stores them in a struct.
2. If the value of **Queue** is an empty string,
    it creates a new queue with a random name starting with **mylpqueue-**.
3. It sends a message to the queue.
4. It receives the message in the queue.
5. It deletes the message.
6. If it created a queue, it deletes the queue.
