# Amazon Transcribe C++ SDK code examples

## Purpose
The code examples in this directory demonstrate how to work with the Amazon Transcribe using the AWS SDK for C++.

Amazon Transcribe is an automatic speech recognition service that makes it easy to add speech to text capabilities to any application.

## Code examples
This is a workspace where you can find AWS SDK for C++ Transcribe examples.

### Scenario

- [Transcribing from an audio file](./get_transcript.cpp) - Shows streaming transcription by mimicking live audio using a flat file. This example uses the following API classes:
   
   - TranscribeStreamingServiceClient
   - StartStreamTranscriptionHandler
   - StartStreamTranscriptionRequest


## ⚠ Important
- We recommend that you grant your code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

## Running the Examples
Before using the code examples, first complete the installation and setup steps of [Getting Started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for C++ Developer Guide.
The Getting Started section covers how to obtain and build the SDK, and how to build your own code utilizing the SDK with a sample "Hello World"-style application. 

Next, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html) for information on the structure of the code examples, building, and running the examples.

This example relies on curl as the HTTP client.  If you are using Windows, additional steps are required to build the SDK for C++ with curl support.  For more information, see [Building the AWS SDK for C++ on Windows](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/setup-windows.html) in the AWS SDK for C++ Developer Guide.  Additionally, code execution must be able to locate the curl dll.

To run this code example, your AWS user must have permissions to perform these actions with Transcribe.  
The AWS managed policy named "AmazonTranscribeFullAccess" may be used to bulk-grant permissions for this example.  
For more information on attaching policies to IAM user groups, 
see [Attaching a policy to an IAM user group](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_groups_manage_attach-policy.html).



## Resources
- [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html) 

