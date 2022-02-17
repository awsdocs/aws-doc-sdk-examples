# Amazon Transcribe examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Transcribe to
transcribe an audio file to a text file. Learn how to:

* Run a transcription job against an audio file in an Amazon S3 bucket.
* Create and refine a custom vocabulary to improve the accuracy of the transcription.
* List and manage transcription jobs and custom vocabularies.

*Amazon Transcribe provides transcription services for your audio files. It uses 
advanced machine learning technologies to recognize spoken words and transcribe them 
into text.* 

## Code examples

### Scenario examples

* [Create and refine a custom vocabulary](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
* [Transcribe audio and get job data](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/getting_started.py)

### API examples

* [Create a custom vocabulary](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`CreateVocabulary`)
* [Delete a custom vocabulary](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`DeleteVocabulary`)
* [Delete a transcription job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`DeleteTranscriptionJob`)
* [Get a custom vocabulary](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`GetVocabulary`)
* [Get a transcription job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`GetTranscriptionJob`)
* [List custom vocabularies](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`ListVocabularies`)
* [List transcription jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`ListTranscriptionJobs`)
* [Start a transcription job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`StartTranscriptionJob`)
* [Update a custom vocabulary](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/python/example_code/transcribe/transcribe_basics.py)
(`UpdateVocabulary`)

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
- Python 3.7 or later
- Boto3 1.14.47 or later
- Requests 2.23.0 or later 
- PyTest 5.3.5 or later (to run unit tests)

### Command

Run this example at a command prompt with the following command.

```
python transcribe_basics.py
``` 

This example uses a public domain 
[audio file](https://en.wikisource.org/wiki/File:Jabberwocky.ogg) downloaded from 
Wikipedia and converted from .ogg to .mp3 format. The file contains a reading of 
the poem *Jabberwocky* by Lewis Carroll.

### Example structure

The example contains the following files.

**transcribe_basics.py**

Shows how to use transcription and vocabulary APIs. The `usage_demo`
function runs a transcription job on an audio file of *Jabberwocky*
by Lewis Carroll. Because *Jabberwocky* contains many nonsense words, the initial
transcript has some accuracy problems. The demo shows how to create and refine a 
custom vocabulary of nonsense words. When the job is run again with the custom
vocabulary, the accuracy of the transcript is improved.

**.media/Jabberwocky.mp3**

The audio file that contains the reading of *Jabberwocky*. This file is uploaded to
Amazon S3 at the beginning of the demo.

**jabber-vocabulary-table.txt**

A custom vocabulary that includes nonsense words from *Jabberwocky* and pronunciation
hints for some of the words. This vocabulary is uploaded to Amazon Transcribe as part
of the demo.

**getting_started.py**

Shows how to start a job and get information about the job. This script is included
in the 
[Getting started](https://docs.aws.amazon.com/transcribe/latest/dg/getting-started-python.html) 
section of the Amazon Transcribe Developer Guide.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/transcribe 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Transcribe service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/transcribe.html)
- [Amazon Transcribe documentation](https://docs.aws.amazon.com/transcribe/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
