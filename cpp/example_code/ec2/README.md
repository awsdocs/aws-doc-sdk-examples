# Amazon EC2 code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

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

- [Hello Amazon EC2](hello_ec2/CMakeLists.txt#L4) (`DescribeSecurityGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](allocate_address.cpp#L23)
- [AssociateAddress](associate_address.cpp#L20)
- [AuthorizeSecurityGroupIngress](authorize_security_group_ingress.cpp#L26)
- [CreateKeyPair](create_key_pair.cpp#L23)
- [CreateSecurityGroup](create_security_group.cpp#L22)
- [CreateTags](create_tags.cpp#L21)
- [DeleteKeyPair](delete_key_pair.cpp#L22)
- [DeleteSecurityGroup](delete_security_group.cpp#L22)
- [DescribeAddresses](describe_addresses.cpp#L24)
- [DescribeAvailabilityZones](describe_availability_zones.cpp#L23)
- [DescribeInstances](describe_instances.cpp#L24)
- [DescribeKeyPairs](describe_key_pairs.cpp#L24)
- [DescribeRegions](describe_regions_and_zones.cpp#L24)
- [DescribeSecurityGroups](describe_security_groups.cpp#L24)
- [MonitorInstances](monitor_instance.cpp#L23)
- [RebootInstances](reboot_instance.cpp#L22)
- [ReleaseAddress](release_address.cpp#L22)
- [RunInstances](run_instances.cpp#L23)
- [StartInstances](start_stop_instance.cpp#L27)
- [StopInstances](start_stop_instance.cpp#L72)
- [TerminateInstances](terminate_instances.cpp#L20)
- [UnmonitorInstances](monitor_instance.cpp#L70)


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

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.



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

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for C++ Amazon EC2 reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-ec2/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0