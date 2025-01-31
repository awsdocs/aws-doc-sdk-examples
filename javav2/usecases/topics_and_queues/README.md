# ***Publish and subscribe to topics using filters and queues using the AWS SDK for Java V2***

## Overview


Publish and subscribe is a mechanism for passing information. It’s used in social media, and it’s also used internally in software applications. A producer publishes a message, and the subscribers receive the message. In software, publish and subscribe notifications make message passing flexible and robust. The producers of messages are decoupled from the consumers of messages.

Use the sample code in this folder to explore publishing and subscribing to a topic by using filters and queues. This tutorial does not create a complete end-to-end application. Instead, you can use it to play around with a publish and subscribe architecture.

You can create an Amazon SNS topic and subscribe an Amazon SQS queue to the topic. You can enable FIFO (First-In-First-Out) queueing, and you can add filtered subscriptions. Then, you can publish messages to the topic and see the results in the queues.

You can publish and subscribe using Amazon SNS alone. But combining Amazon SNS with Amazon SQS gives you more flexibility in how the messages are consumed.

Amazon SNS is a push service. It pushes to endpoints such as email addresses, mobile application endpoints, or SQS queues. (For a full list of endpoints, see [SNS event destinations](https://docs.aws.amazon.com/sns/latest/dg/sns-event-destinations.html)).

With Amazon SQS, messages are received from a queue by polling. With polling, the subscriber receives messages by calling a receive message API. Any code can poll the queue. Also, the messages stay in the queue until you delete them. This gives you more flexibility in how the messages are processed.

The AWS SDK for Java v2 sample code builds a command line application that asks you for input. The following shows the interface for the Java implementation.

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

### Create an SQS queue

Now, configure an SQS queue to subscribe to your topic. Separate queues for each subscriber can be helpful. For 
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

The following diagram shows the topic and queue options.
![Diagram of the options](images/fifo_topics_diagram.png)

After you create the topic and subscribe the queue, the application lets you publish messages to the topic.


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


##  ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Run the examples

### Prerequisites

Before using the code examples, make sure to properly set up your development environment. For information, see [Setting up the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).


###  Instructions

This example uses Apache Maven to set up the AWS SDK for Java v2. The POM file is located in this folder. For information, see [Set up an Apache Maven project](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html).


Running this example requires AWS Identity and Access Management (IAM) permissions for both SNS and SQS.

## Additional resources

* [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
* [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
* [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
* [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
* [AWS SDK for Java V2 Developer Guide](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)

