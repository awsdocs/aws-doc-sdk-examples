
# Publishing and subscribing to topics using filters and queues

## Overview

The sample code for this tutorial gives you the chance to explore publishing and subscribing to a topic using filters and queues. 
This tutorial does not create a complete end-to-end application. Instead, it allows you to play around with the particular publish and 
subscribe architecture.  You can create a Simple Notification Service (SNS) topic and subscribe two Simple Queue Service (SQS) queues to 
the topic. You can enable FIFO (First-In-First-Out) queueing, and you can add filtered subscriptions. You can then publish messages 
to the topic and see the results in the queues.

What is publish and subscribe? It is a mechanism for passing information. It is used in social media, and it is used internally in 
software applications. A producer publishes a message, and the subscribers receive the message. In software, publish and subscribe 
notifications makes message-passing flexible and robust. The producers of messages are decoupled from the consumers of messages.

Publish and subscribe can be accomplished with just SNS. But by combining SNS with SQS, you have greater flexibility about how 
the messages are consumed. SNS is a push service. It pushes to endpoints, such as an email address, a mobile application endpoint, 
or an SQS queue (for the full list of endpoints, see  [SNS event destinations](https://docs.aws.amazon.com/sns/latest/dg/sns-event-destinations.html)). With SQS, however, messages are retrieved by polling. 
Any code can poll the queue. Also, the messages stay in the queue until you delete them. This gives you greater flexibility 
about how the messages are processed.

Your choices options for topics and queues are the following.

1. FIFO or non-FIFO topics.
2. For FIFO topics, content-based deduplication or a user-provided deduplication ID.
3. For FIFO topics, filtered of unfiltered subscriptions.

![diagram of options for topics and queues](images/fifo_topics_diagram.png)

After you have created the topic and 2 queues, the application will let you post messages to the topic. You will always include a message text in when posting a message. Depending on the configuration, you may have to enter a Group ID (required for FIFO topics), a deduplication ID (when content-based deduplication ID is not enabled), and optionally a topic attribute (filtered subscriptions).

After you have finished posting the messages, the application will retrieve the messages from the queue and print them to the screen. You can see the effects of ordering, filtering and deduplication.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

* Install the [AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html).


## Additional resources
* [AWS SDK for C++ Developer Guide](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/welcome.html) 
