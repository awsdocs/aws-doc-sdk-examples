# Amazon Polly code examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Polly and Tkinter to
create a lip-sync application that displays an animated face speaking along with the
speech synthesized by Amazon Polly. Lip-sync is accomplished by requesting a list
of visemes from Amazon Polly that match up with the synthesized speech.

* Get voice metadata from Amazon Polly and display it in a Tkinter application.
* Get synthesized speech audio and matching viseme speech marks from Amazon Polly.
* Play the audio with synchronized mouth movements in an animated face.
* Submit asynchronous synthesis tasks for long texts and retrieve the output from
an Amazon Simple Storage Service (Amazon S3) bucket.

*Amazon Polly is a Text-to-Speech (TTS) cloud service that converts text into lifelike 
speech.*

## Code examples

### Scenario examples

* [Create a lip-sync application](polly_wrapper.py)

### API examples

* [Get a lexicon](polly_wrapper.py)
(`GetLexicon`)
* [Get data about a speech synthesis task](polly_wrapper.py)
(`GetSpeechSynthesisTask`)
* [Get voices available for synthesis](polly_wrapper.py)
(`DescribeVoices`)
* [List pronunciation lexicons](polly_wrapper.py)
(`ListLexicons`)
* [Start a speech synthesis task](polly_wrapper.py)
(`StartSpeechSynthesisTask`)
* [Store a pronunciation lexicon](polly_wrapper.py)
(`PutLexicon`)
* [Synthesize speech from text](polly_wrapper.py)
(`SynthesizeSpeech`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.5 or later
- Boto3 1.16.49 or later
- Playsound 1.2.2 or later
- Requests 2.24.0 or later
- PyTest 6.0.2 or later (for unit tests)

### Command

Run this example at a command prompt with the following command.

```
python polly_lipsync.py
```

This example is a GUI application. Enter text into the input field; select engine,
language, and voice parameters; and click the **Say it** button to see and hear the
synthesized speech.

If you enter text that is too long for synchronous synthesis, you are asked for the
name of an existing Amazon S3 bucket. This bucket is used by Amazon Polly to store
the output of the asynchronous synthesis task. After the task completes, the 
application downloads the output and deletes the Amazon S3 object. 

### Example structure

The example contains two main components that separate UI code from service handling 
code. 

**polly_lipsync.py**

Encapsulates UI components. Creates a Tkinter window, adds widgets to it,
and manages interactions among widgets in response to user events.

**polly_wrapper.py**

Wraps parts of the Amazon Polly API to make requests to Amazon Polly and return data
to the UI component.  

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your 
[GitHub root]/python/example_code/polly folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Polly service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/polly.html)
- [Amazon Polly User Guide](https://docs.aws.amazon.com/polly/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
