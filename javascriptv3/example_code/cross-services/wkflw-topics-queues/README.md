# Publish and subscribe to topics using filters and queues

## Overview

Publish and subscribe is a mechanism for passing information. It’s used in social media, and it’s also used internally in software applications. A producer publishes a message, and the subscribers receive the message. In software, publish and subscribe notifications make message passing flexible and robust. The producers of messages are decoupled from the consumers of messages.

Use the sample code in this folder to explore publishing and subscribing to a topic by using filters and queues. This tutorial does not create a complete end-to-end application. Instead, you can use it to play around with a publish and subscribe architecture.

You can create an Amazon Simple Notification Service (Amazon SNS) topic and subscribe two Amazon Simple Queue Service (Amazon SQS) queues to the topic. You can enable FIFO (First-In-First-Out) queueing, and you can add filtered subscriptions. Then, you can publish messages to the topic and see the results in the queues.

You can publish and subscribe using Amazon SNS alone. But combining Amazon SNS with Amazon SQS gives you more flexibility in how the messages are consumed.

Amazon SNS is a push service. It pushes to endpoints such as email addresses, mobile application endpoints, or SQS queues. (For a full list of endpoints, see [SNS event destinations](https://docs.aws.amazon.com/sns/latest/dg/sns-event-destinations.html)).

With Amazon SQS, messages are received from a queue by polling. With polling, the subscriber receives messages by calling a receive message API. Any code can poll the queue. Also, the messages stay in the queue until you delete them. This gives you more flexibility in how the messages are processed.

The sample code builds a command line application that asks you for input. This is implemented in multiple programming languages, and the interface can vary slightly between languages. This folder contains the JavaScript implementation.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

- NodeJS

## Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6).

1. Install dependencies. `npm i`
2. Run the example. `node index.js`

## Tests

⚠ Running tests might result in charges to your AWS account.

To find instructions for running these tests, see the [README](../../../README.md#Tests)
in the `javascriptv3` folder.

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SQS Developer Guide](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [Amazon SQS API Reference](https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) Amazon SNS reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/sns)
- [SDK for JavaScript (v3) Amazon SQS](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/sqs/)
