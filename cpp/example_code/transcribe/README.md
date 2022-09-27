# Amazon Transcribe code examples for the SDK for C++
## Overview
The code examples in this directory demonstrate how to work with Amazon Transcribe using the AWS SDK for C++.

Amazon Transcribe is an automatic speech recognition service that makes it easy to add speech to text capabilities to any application.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
- [Transcribing from an audio file](./get_transcript.cpp) - Shows streaming transcription by mimicking live audio using a flat file. This example uses the following API classes:
   
   - TranscribeStreamingServiceClient
   - StartStreamTranscriptionHandler
   - StartStreamTranscriptionRequest

## Run the Examples
Before using the code examples, first complete the installation and setup steps of [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
This section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample Hello World-style application. 

Next, for information about code example structure and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

This example relies on curl as the HTTP client. If you are using Windows, additional steps are required to build the SDK for C++ with curl support.  For more information, see [Building the AWS SDK for C++ on Windows](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/setup-windows.html) in the AWS SDK for C++ Developer Guide.  Additionally, code execution must be able to locate the curl dll.

To run this code example, your AWS user must have permissions to perform these actions with Amazon Transcribe.  
The AWS managed policy named "AmazonTranscribeFullAccess" can be used to grant permissions in bulk for this example.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).

## Additional resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 
- [Amazon Transcribe Documentation](https://docs.aws.amazon.com/transcribe/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
