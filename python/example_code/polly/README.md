# Amazon Polly code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Polly.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Polly is a Text-to-Speech (TTS) cloud service that converts text into lifelike speech._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [DescribeVoices](polly_wrapper.py#L35)
- [GetLexicon](polly_wrapper.py#L267)
- [GetSpeechSynthesisTask](polly_wrapper.py#L229)
- [ListLexicons](polly_wrapper.py#L286)
- [PutLexicon](polly_wrapper.py#L249)
- [StartSpeechSynthesisTask](polly_wrapper.py#L147)
- [SynthesizeSpeech](polly_wrapper.py#L54)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a lip-sync application](../../example_code/polly)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create a lip-sync application

This example shows you how to create a lip-sync application with Amazon Polly.


<!--custom.scenario_prereqs.polly_LipSync.start-->

Start the example by running the following at a command prompt:

<!--custom.scenario_prereqs.polly_LipSync.end-->


<!--custom.scenarios.polly_LipSync.start-->

Start the example by running the following at a command prompt:

```
python polly_lipsync.py
```

This example is a GUI application. Enter text into the input field, then select engine,
language, and voice parameters. Choose the **Say it** button to see and hear the
synthesized speech.

If you enter text that is too long for synchronous synthesis, you are asked for the
name of an existing Amazon Simple Storage Service (Amazon S3) bucket. This bucket is used
by Amazon Polly to store the output of the asynchronous synthesis task. After the task
completes, the application downloads the output and deletes the S3 object.

<!--custom.scenarios.polly_LipSync.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Polly Developer Guide](https://docs.aws.amazon.com/polly/latest/dg/what-is.html)
- [Amazon Polly API Reference](https://docs.aws.amazon.com/polly/latest/dg/API_Reference.html)
- [SDK for Python Amazon Polly reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/polly.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0