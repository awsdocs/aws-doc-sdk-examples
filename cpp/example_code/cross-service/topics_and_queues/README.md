# ***Publish and subscribe to topics using filters and queues***

## Overview


What is publish and subscribe? Publish and subcribe is a mechanism for passing information. It is used in social media, and it is used internally in software applications. A producer publishes a message, and the subscribers receive the message. In software, publish and subscribe notifications makes message passing flexible and robust. The producers of messages are decoupled from the consumers of messages.

The sample code in this folder gives you the chance to explore publishing and subscribing to a topic using filters and queues. This tutorial does not create a complete end-to-end application. Instead, it allows you to play around with a publish and subscribe architecture.  You can create a Simple Notification Service (SNS) topic and subscribe two Simple Queue Service (SQS) queues to the topic. You can enable FIFO (First-In-First-Out) queueing, and you can add filtered subscriptions. You can then publish messages to the topic and see the results in the queues.

Publish and subscribe can be accomplished with just SNS. But by combining SNS with SQS, you have greater flexibility about how the messages are consumed. SNS is a push service, pushing to endpoints, such as an email address, a mobile application endpoint, or an SQS queue (for the full list of endpoints, see [SNS event destinations](https://docs.aws.amazon.com/sns/latest/dg/sns-event-destinations.html)).  With SQS, on the other hand, messages are retrieved by polling. Any code can poll the queue. Also, the messages stay in the queue until you delete them. This gives you greater flexibility about how the messages are processed.

The sample code builds a command-line application which will then ask you for input.

### Create an SNS topic.

```
Would you like to work with FIFO topics? (y/n) 
```

FIFO (First-In-First-Out) topics are configured at creation. Choosing a FIFO topics enables other options as well. You can read about a FIFO topic use case in the developer documentation. [FIFO topics example use case](https://docs.aws.amazon.com/sns/latest/dg/fifo-example-use-case.html).


```
Would you like to use content-based deduplication instead of a deduplication ID? (y/n)
```

Deduplication is only available for FIFO topics.
Content-based deduplication uses a hash of the content as a deduplication ID. If content-based deduplication is not enabled, you must include a deduplication ID with each message. If a message is successfully published to an SNS FIFO topic, any message published and determined to be a duplicate, within the five-minute deduplication interval, is accepted but not delivered. For more information about deduplication, see [Message deduplication for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html).


```
Enter a name for your SNS topic:
```

Topic names must be made up of only uppercase and lowercase ASCII letters, numbers, underscores, and hyphens, and must be between 1 and 256 characters long. If you chose FIFO topics, the application automatically appends a “.fifo” suffix, which is a requirement for FIFO topics.

### Create 2 SQS queues.

At this point you will configure the 2 SQS queues which will be subscribed to the topic you just created.

```
Enter a name for an SQS queue.
```

Queue names must be made up of only uppercase and lowercase ASCII letters, numbers, underscores, and hyphens, and must be between 1 and 80 characters long. If you chose FIFO topic, the application automatically appends a “.fifo” suffix to the queue, which is a requirement for FIFO queues.


```
Would you like to filter messages for "<queue name>.fifo"s subscription to 
the topic "<topic name>.fifo"?  (y/n)
```

If you selected FIFO topics, you can add a filter to the queue’s subscription to the topic. There are many ways to filter a topic. In this case, you will have an option to filter by a predetermined selection of attributes. For more information on filters, see [Message filtering for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-filtering.html).


```
You can filter messages by one or more of the following "tone" attributes.
1. cheerful
2. funny
3. serious
4. sincere
Enter a number (or enter zero to not add anything more)
```

If you chose to add a filter, you can then add one or more “tone” attributes as filter selections. When you are done adding attributes, enter ‘0’ to continue.

The application will now prompt you to add the second queue, repeating the previous choices.

Below is a diagram of the topic and queue options.
![Diagram show options for the application](images/fifo_topics_diagram.png)

After you have created the topic and subscribed 2 queues, the application will let you publish messages to the topic.


```
Enter a message text to publish.
```

All configurations will include a message text.


```
Enter a message group ID for this message.
```

If this is a FIFO topic, then you must include a group ID. The group ID can contain up to 128 alphanumeric characters `(a-z, A-Z, 0-9)` and punctuation `(!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~).`
For more information about group IDs, see [Message grouping for FIFO topics](https://docs.aws.amazon.com/sns/latest/dg/fifo-message-grouping.html).


```
Enter a deduplication ID for this message.
```

If this is a FIFO topic and content-based deduplication is not enabled, then you must enter a deduplication ID. The message deduplication ID can contain up to 128 alphanumeric characters `(a-z, A-Z, 0-9)` and punctuation `(!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~).`


```
Would you like to add an attribute to this message? (y/n) y
```

If you have added a filter to one of the subscriptions, you will. have the option to add a filtering attribute to the message.


```
1. cheerful
2. funny
3. serious
4. sincere
Enter a number for an attribute: 
```

Select a number for an attribute.


```
Would you like to post another message? (y/n)
```

You can post as many messages as you like.

When you are done posting message, the application will poll the queues and display their messages.


##  ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).



## Prerequisites

Before using the code examples, first complete the installation and setup steps of [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.


##  Instructions

This example uses the CMake build system. For information about the CMake build system, see https://cmake.org/.
Many Integrated Development Environments (IDEs) support CMake. If your preferred IDE supports CMake, follow the IDE's instructions to open this CMake project.

This project can also be built from a command-line interface using the following commands.


```
mkdir build 
cd build
cmake —build 
```

This builds the executable named “run_topics_and_queues”.

Running this example requires permissions for both SNS and SQS.

## Additional resources

[Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
[Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
[Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
[Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
[AWS SDK for C++ Developer Guide](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/welcome.html)
