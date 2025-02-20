# AWS IoT code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with AWS IoT.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT provides secure, bi-directional communication for Internet-connected devices (such as sensors, actuators, embedded devices, wireless devices, and smart appliances) to connect to the AWS Cloud over MQTT, HTTPS, and LoRaWAN._

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

- [Hello AWS IoT](hello_iot/CMakeLists.txt#L4) (`listThings`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](things_and_shadows_workflow/iot_things_and_shadows_workflow.cpp)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachThingPrincipal](attach_thing_principal.cpp#L22)
- [CreateKeysAndCertificate](create_keys_and_certificate.cpp#L23)
- [CreateThing](create_thing.cpp#L22)
- [CreateTopicRule](create_topic_rule.cpp#L22)
- [DeleteCertificate](delete_certificate.cpp#L22)
- [DeleteThing](delete_thing.cpp#L22)
- [DeleteTopicRule](delete_topic_rule.cpp#L22)
- [DescribeEndpoint](describe_endpoint.cpp#L22)
- [DescribeThing](describe_thing.cpp#L22)
- [DetachThingPrincipal](detach_thing_principal.cpp#L23)
- [ListCertificates](list_certificates.cpp#L23)
- [SearchIndex](search_index.cpp#L22)
- [UpdateIndexingConfiguration](update_indexing_configuration.cpp#L22)
- [UpdateThing](update_thing.cpp#L23)


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

#### Hello AWS IoT

This example shows you how to get started using AWS IoT.


#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT Thing.
- Generate a device certificate.
- Update an AWS IoT Thing with Attributes.
- Return a unique endpoint.
- List your AWS IoT certificates.
- Create an AWS IoT shadow.
- Write out state information.
- Creates a rule.
- List your rules.
- Search things using the Thing name.
- Delete an AWS IoT Thing.

<!--custom.basic_prereqs.iot_Scenario.start-->
<!--custom.basic_prereqs.iot_Scenario.end-->


<!--custom.basics.iot_Scenario.start-->
<!--custom.basics.iot_Scenario.end-->


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

- [AWS IoT Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for C++ AWS IoT reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-iot/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
