# Amazon Comprehend detection, classification, and topic modeling examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Comprehend to inspect
documents and discover information about them.

* Detect elements of a document, such as languages used, key phrases, and personally
identifiable information (PII).
* Train a custom classifier that learns a set of labels on GitHub issues, and send 
new issues to the classifer for labeling.
* Detect common themes in a set of documents without the need for prior annotation.

*Amazon Comprehend uses natural language processing (NLP) to extract insights about 
the content of documents without the need of any special preprocessing.*

## Code examples

**Usage**

* [Running a topic modeling job on sample data](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_topic_modeler_demo.py)
* [Train a custom classifier and classify documents](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier_demo.py)
* [Use detection APIs with a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)

**Element detection**

* [Detecting entities, like people and places, in a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_entities`)
* [Detecting languages used in a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_dominant_language`)
* [Detecting key phrases in a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_key_phrases`)
* [Detecting personally identifiable information (PII) in a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_pii_entities`)
* [Detecting the overall sentiment expressed in a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_sentiment`)
* [Detecting the syntactical elements of a document](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_detect.py)
(`detect_syntax`)

**Custom classifier**

* [Creating a custom classifier](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`create_document_classifier`)
* [Deleting a custom classifier](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`delete_document_classifier`)
* [Getting metadata about a classification job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`describe_document_classification_job`)
* [Getting metadata about a custom classifier](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`describe_document_classifier`)
* [Listing classification jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`list_document_classification_jobs`)
* [Listing custom classifiers](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`list_document_classifiers`)
* [Starting an asynchronous classification job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_classifier.py)
(`start_document_classification_job`)

**Document themes**

* [Getting metadata about a topic detection job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_topic_modeler.py)
(`describe_topics_detection_job`)
* [Listing topic detection jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_topic_modeler.py)
(`list_topics_detection_jobs`)
* [Starting an asynchronous topic detection job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/comprehend/comprehend_topic_modeler.py)
(`start_topics_detection_job`)

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
- Boto3 1.15.4 or later
- Requests 2.24.0 or later
- PyTest 6.0.2 or later (to run unit tests)

### Command

There are three demonstrations in this set of examples:

* Detecting elements in a single document.
* Training and running a custom classifier to label GitHub issues.
* Detecting common themes in a set of documents.

**Element detection**

Run this example at a command prompt with the following command.

```
python comprehend_detect.py
``` 

**Custom GitHub issue classifier**

Run this example at a command prompt with the following command.

```
python comprehend_classifier_demo.py
``` 

**Document themes**

Run this example at a command prompt with the following command.

```
python comprehend_topic_modeler_demo.py
``` 

### Example structure

The examples contain the following files.

**comprehend_classifier.py**

Shows how to use the Amazon Comprehend custom classifier APIs to create, train, and 
manage a custom classifier, and use it to run classification jobs.  

**comprehend_classifier_demo.py**

Shows how to use Amazon Comprehend to train a custom multi-label classifier and run a 
job to classify documents.

* Trains a classifier on a set of GitHub issues with known labels.
* Sends a second set of GitHub issues to the classifier so they can be labeled.
* Reconciles classifier labels with existing issue labels.  

**comprehend_demo_resources.py**

Creates, manages, and deletes AWS resources used by the Amazon Comprehend
demonstrations.

* Creates an Amazon S3 bucket that contains training, input, and output data.
* Creates an AWS Identity and Access Management (IAM) role and policy that grants
permission to let Comprehend read from and write to the Amazon S3 bucket.
* Gets output data from Amazon S3 and extracts it from its compressed format into
usable Python objects.
* Cleans up the Amazon S3 bucket and IAM role and policy.   

**comprehend_detect.py**

Shows how to use Amazon Comprehend to detect entities, phrases, and more in a document.
The `usage_demo` function sends the `detect_sample.txt` contents to Comprehend to 
demonstrate the API.

**comprehend_topic_modeler.py**

Shows how to use Amazon Comprehend to run topic modeling jobs.

**comprehend_topic_modeler_demo.py**

Show how to use Amazon Comprehend to run a topic modeling job on sample data
retrieved from a public Amazon S3 bucket. The demonstration sends the data to 
Comprehend for topic modeling, waits for the job to complete, and extracts the 
resulting output into a list of Python dictionaries.

**detect_sample.txt**

Sample text that is used by the detection demonstration.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your [GitHub root]/python/example_code/comprehend 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Comprehend service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/comprehend.html)
- [Amazon Comprehend documentation](https://docs.aws.amazon.com/comprehend/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
