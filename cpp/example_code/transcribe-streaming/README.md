# Amazon Transcribe Streaming code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Transcribe Streaming.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Transcribe Streaming produces real-time transcriptions for your media content._

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

### Single actions

Code excerpts that show you how to call individual service functions.

- [StartStreamTranscription](get_transcript.cpp#L28)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Transcribe an audio file](get_transcript.cpp)


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



#### Transcribe an audio file

This example shows you how to generate a transcription of a source audio file using Amazon Transcribe streaming.


<!--custom.scenario_prereqs.transcribe-streaming_Scenario_StreamEvents_File.start-->
<!--custom.scenario_prereqs.transcribe-streaming_Scenario_StreamEvents_File.end-->


<!--custom.scenarios.transcribe-streaming_Scenario_StreamEvents_File.start-->
<!--custom.scenarios.transcribe-streaming_Scenario_StreamEvents_File.end-->

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

- [Amazon Transcribe Streaming Developer Guide](https://docs.aws.amazon.com/transcribe/latest/dg/streaming.html)
- [Amazon Transcribe Streaming API Reference](https://docs.aws.amazon.com/transcribe/latest/APIReference/Welcome.html)
- [SDK for C++ Amazon Transcribe Streaming reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-transcribe/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
