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

- [CreateTopic](create_topic.cpp#L21)
- [DeleteTopic](delete_topic.cpp#L21)
- [GetSMSAttributes](get_sms_type.cpp#L21)
- [GetTopicAttributes](get_topic_attributes.cpp#L22)
- [ListSubscriptions](list_subscriptions.cpp#L21)
- [ListTopics](list_topics.cpp#L21)
- [Publish](publish_to_topic.cpp#L21)
- [SetSMSAttributes](set_sms_type.cpp#L21)
- [Subscribe](subscribe_email.cpp#L10)
- [Unsubscribe](unsubscribe.cpp#L21)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a serverless application to manage photos](../../example_code/cross-service/photo_asset_manager)
- [Publish an SMS text message](publish_sms.cpp)
- [Publish messages to queues](../cross-service/topics_and_queues/messaging_with_topics_and_queues.cpp)


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



#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Publish an SMS text message

This example shows you how to publish SMS messages using Amazon SNS.


<!--custom.scenario_prereqs.sns_PublishTextSMS.start-->
<!--custom.scenario_prereqs.sns_PublishTextSMS.end-->


<!--custom.scenarios.sns_PublishTextSMS.start-->
<!--custom.scenarios.sns_PublishTextSMS.end-->

#### Publish messages to queues

This example shows you how to do the following:

- Create topic (FIFO or non-FIFO).
- Subscribe several queues to the topic with an option to apply a filter.
- Publish messages to the topic.
- Poll the queues for messages received.

<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.start-->
<!--custom.scenario_prereqs.sqs_Scenario_TopicsAndQueues.end-->


<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.start-->
<!--custom.scenarios.sqs_Scenario_TopicsAndQueues.end-->

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