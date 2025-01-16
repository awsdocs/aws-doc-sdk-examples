# Publish and Subscribe to Topics Scenario - Technical specification

This document contains the technical specifications for _Topics and Queues Workflow_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example workflow.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Metadata](#metadata)

## Resources and User Input

- One Amazon Simple Notification Service (Amazon SNS) topic created in the scenario.
- Two Amazon Simple Queue Service (Amazon SQS) queues created in the scenario.

The sample code builds a command line application that asks you for input. This is implemented in multiple programming languages, and the interface can vary slightly between languages. The following shows the interface for the C++ implementation.

### Create an SNS topic

```
Would you like to work with FIFO topics? (y/n) 
```

You configure FIFO (First-In-First-Out) topics when you create them. Choosing a FIFO topic enables other options, too. To learn more, see [FIFO topics example use case](https://docs.aws.amazon.com/sns/latest/dg/fifo-example-use-case.html).


```
Use content-based deduplication instead of a deduplication ID? (y/n)
```

Deduplication is only available for FIFO topics. Deduplication prevents the subscriber from responding more than once to events that are determined to be duplicates. If a message gets published to an SNS FIFO topic and it’s found to be a duplicate within the five-minute deduplication interval, the message is accepted but not delivered. For more information, see [Message deduplication for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html).

Content-based deduplication uses a hash of the content as a deduplication ID. If content-based deduplication is not enabled, you must include a deduplication ID with each message.

```
Enter a name for your SNS topic:
```

Topic names can have 1-256 characters. They can contain uppercase and lowercase ASCII letters, numbers, underscores, and hyphens. If you chose a FIFO topic, the application automatically adds a “.fifo” suffix, which is required for FIFO topics.

### Create two SQS queues

Now, configure two SQS queues to subscribe to your topic. Separate queues for each subscriber can be helpful. For
instance, you can customize how messages are consumed and how messages are filtered.

```
Enter a name for an SQS queue.
```

Queue names can have 1-80 characters. They can contain uppercase and lowercase ASCII letters, numbers, underscores, and hyphens. If you chose a FIFO topic, the application automatically adds a “.fifo” suffix, which is required for FIFO queues.


```
Filter messages for "<queue name>.fifo"s subscription to 
the topic "<topic name>.fifo"?  (y/n)
```

If you chose FIFO topics, you can add a filter to the queue’s topic subscription. There are many ways to filter a topic. In this example code, you have the option to filter by a predetermined selection of attributes. For more information about filters, see [Message filtering for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-filtering.html).


```
You can filter messages by one or more of the following "tone" attributes.
1. cheerful
2. funny
3. serious
4. sincere
Enter a number (or enter zero to stop adding more)
```

If you add a filter, you can select one or more “tone” attributes to filter by. When you’re done, enter “0’” to continue.

The application now prompts you to add the second queue. Repeat the previous steps for the second queue.

### Publish messages

After you create the topic and subscribe both queues, the application lets you publish messages to the topic.


```
Enter a message text to publish.
```

All configurations include a message text.


```
Enter a message group ID for this message.
```

If this is a FIFO topic, then you must include a group ID. The group ID can contain up to 128 alphanumeric characters `(a-z, A-Z, 0-9)` and punctuation `(!"#$%&'()*+,-./:;<=>?@[\]^_``{|}~)`.
For more information about group IDs, see [Message grouping for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-grouping.html).


```
Enter a deduplication ID for this message.
```

If this is a FIFO topic and content-based deduplication is not enabled, then you must enter a deduplication ID. The message deduplication ID can contain up to 128 alphanumeric characters `(a-z, A-Z, 0-9)` and punctuation `(!"#$%&'()*+,-./:;<=>?@[\]^_``{|}~)`.


```
Add an attribute to this message? (y/n) y
```

If you added a filter to one of the subscriptions, you can choose to add a filtering attribute to the message.


```
1. cheerful
2. funny
3. serious
4. sincere
Enter a number for an attribute: 
```

Select a number for an attribute.


```
Post another message? (y/n)
```

You can post as many messages as you want.

When you are done posting messages, the application polls the queues and displays their messages.

### Clean up resources

```
Delete the SQS queues? (y/n) 

Delete the SNS topic? (y/n) 

```

## Metadata

| action / scenario            | metadata file        | metadata key                 |
|------------------------------|----------------------|------------------------------|
| `CreateTopic`                | sns_metadata.yaml    | sns_CreateTopic              |
| `DeleteTopic`                | sns_metadata.yaml    | sns_DeleteTopic              |
| `Publish`                    | sns_metadata.yaml    | sns_Publish                  |
| `Subscribe`                  | sns_metadata.yaml    | sns_Subscribe                |
| `Unsubscribe`                | sns_metadata.yaml    | sns_Unsubscribe              |
| `CreateQueue`                | sqs_metadata.yaml    | sqs_CreateQueue              |
| `DeleteMessageBatch`         | sqs_metadata.yaml    | sqs_DeleteMessageBatch       |
| `DeleteQueue`                | sqs_metadata.yaml    | sqs_DeleteQueue              |
| `GetQueueAttributes`         | sqs_metadata.yaml    | sqs_GetQueueAttributes       |
| `ReceiveMessage`             | sqs_metadata.yaml    | sqs_ReceiveMessage           |
| `SetQueueAttributes`         | sqs_metadata.yaml    | sqs_SetQueueAttributes       |
| `Topics and queues scenario` | sqs_metadata.yaml    | sqs_Scenario_TopicsAndQueues |

