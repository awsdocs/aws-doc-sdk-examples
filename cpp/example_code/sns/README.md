# Amazon SNS code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Simple Notification Service (Amazon SNS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites



Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon SNS](hello_sns/CMakeLists.txt#L4) (`ListTopics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a topic](create_topic.cpp#L21) (`CreateTopic`)
- [Delete a subscription](unsubscribe.cpp#L21) (`Unsubscribe`)
- [Delete a topic](delete_topic.cpp#L21) (`DeleteTopic`)
- [Get the properties of a topic](get_topic_attributes.cpp#L22) (`GetTopicAttributes`)
- [Get the settings for sending SMS messages](get_sms_type.cpp#L21) (`GetSMSAttributes`)
- [List the subscribers of a topic](list_subscriptions.cpp#L21) (`ListSubscriptions`)
- [List topics](list_topics.cpp#L21) (`ListTopics`)
- [Publish a message with an attribute](../cross-service/topics_and_queues/messaging_with_topics_and_queues.cpp#L52) (`Publish`)
- [Publish an SMS text message](publish_sms.cpp#L21) (`Publish`)
- [Publish to a topic](publish_to_topic.cpp#L21) (`Publish`)
- [Set the default settings for sending SMS messages](set_sms_type.cpp#L21) (`SetSMSAttributes`)
- [Subscribe a Lambda function to a topic](subscribe_lambda.cpp#L21) (`Subscribe`)
- [Subscribe a mobile application to a topic](subscribe_app.cpp#L21) (`Subscribe`)
- [Subscribe an SQS queue to a topic](../cross-service/topics_and_queues/messaging_with_topics_and_queues.cpp#L815) (`Subscribe`)
- [Subscribe an email address to a topic](subscribe_email.cpp#L10) (`Subscribe`)
- [Subscribe with a filter to a topic](../cross-service/topics_and_queues/messaging_with_topics_and_queues.cpp#L52) (`Subscribe`)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a serverless application to manage photos](../../example_code/cross-service/photo_asset_manager)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file in this folder. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

If the source file is in a different folder, instructions can be found in the README in that
folder.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon SNS

This example shows you how to get started using Amazon SNS.



### Tests

⚠ Running tests might result in charges to your AWS account.



```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest
```


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon SNS Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)
- [Amazon SNS API Reference](https://docs.aws.amazon.com/sns/latest/api/welcome.html)
- [SDK for C++ Amazon SNS reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-sns/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0