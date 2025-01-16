## AWS IoT Things and Shadows Scenario
This example demonstrates various interactions with the AWS Internet of things (IoT) Core service using the AWS SDK. 
The program guides you through a series of steps, showcasing AWS IoT capabilities and providing a comprehensive example for developers.



### Scenario Steps

#### Create an AWS IoT thing:

An AWS IoT thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.

#### Generate and attach a device certificate:

Device certificates play a crucial role in securing communication between devices (things) and the AWS IoT platform.

#### Perform various operations on the AWS IoT thing:

* Update an AWS IoT thing with attributes.
* Get an AWS IoT endpoint.
* List your certificates.
* Create an IoT shadow, which refers to a digital representation or virtual twin of a physical IoT device.
* Write out the state information in JSON format.
* Retrieve and display the state information of the thing's shadow in JSON format.
* Create a rule.
* List rules.
* Search things.

#### Clean up resources:

* Optionally detach and delete the certificate associated with the IoT thing.
* Delete the rule.
* Delete the thing.

*Note: An 	Amazon Simple Notification Service (Amazon SNS) topic and an AWS Identity and Access Management (IAM) role are needed to create an
AWS IOT rule. These resources are created with an AWS CloudFormation template.*

## Scenario

### Prerequisites

#### Build system requirements.

* CMake - A C++ cross-platform build system.
* AWS SDK for C++.

For CMake installation instructions, go to the following link [Get the Software](https://cmake.org/download/).

For AWS SDK for C++ installation instructions, go to the following link [Get started with the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html).

*Note: you can speed up the SDK build by only building the libraries needed by this scenario. To do this, pass the 
following argument to CMake.* 

`-DBUILD_ONLY="cloudformation;iot;iot-data"`

### Build and Run the Scenario

These instructions build and run the executable as a command-line application.

*Note: Many Integrated Development Environments (IDEs), such as Visual Studio and CLion, support CMake and can also be used to build and run the scenario.*

Open a terminal in the directory `cpp/example_code/iot/things_and_shadows_workflow`.

Execute the following commands

```shell
mkdir build
cd build
cmake ..  -DCMAKE_BUILD_TYPE=Debug
cmake --build . --config=Debug
./run_iot_things_and_shadows_workflow
```


## Additional resources

- [AWS IoT Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for C++ AWS IoT reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-iot/html/annotated.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0







