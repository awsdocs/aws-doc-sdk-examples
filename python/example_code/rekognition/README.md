# Amazon Rekognition Image, Video, and Custom labels examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Rekognition to
recognize people, objects, and text in images and videos. 

* Detect faces, celebrities, objects, and text in an image.
* Create a collection of indexed faces and search for faces in your collection 
that match a reference image.
* Detect faces, celebrities, and objects in a video.
* Create a notification channel so your code can determine when a video
detection job has completed.

Also included are utilities that you can use with Amazon Rekognition Custom Labels.

*Amazon Rekognition makes it easy to add image and video analysis to your applications. 
You provide an image or video to the Amazon Rekognition API, and the service 
identifies objects, people, text, scenes, and activities.*

## Code examples

**Cross-service examples**

* [Detect faces, people, and objects in a video](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_video_detection.py)

**Usage examples**

* [Build a collection and find faces in it](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
* [Detect and display elements in images](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)

**API examples**

* [Compare faces in an image against a reference image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`compare_faces`)
* [Create a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`create_collection`)
* [Delete a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`delete_collection`)
* [Delete faces from a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`delete_faces`)
* [Describe a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`describe_collection`)
* [Detect faces in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`detect_faces`)
* [Detect labels in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`detect_labels`)
* [Detect moderation labels in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`detect_moderation_labels`)
* [Detect text in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`detect_text`)
* [Index faces to a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`index_faces`)
* [List faces in a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`list_faces`)
* [List collections](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`list_collections`)
* [Recognize celebrities in an image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_image_detection.py)
(`recognize_celebrities`)
* [Search for faces in a collection](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`search_faces`)
* [Search for faces in a collection compared to a reference image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/rekognition_collections.py)
(`search_faces_by_image`)

**Utilities**

* [Create a Custom Labels image-level manifest file from a CSV file](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/rekognition/custom_labels_csv_to_manifest.py)

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
- Pillow 7.2.0 or later 
- PyTest 5.3.5 or later (to run unit tests)

### Command

There are three demonstrations in this set of examples:

* Detecting items in a single image.
* Building a collection of indexed faces and searching for matches.
* Detecting items in a video.

**Image detection**

Run this example at a command prompt with the following command.

```commandline
python rekognition_image_detection.py
``` 

**Face collection**

Run this example at a command prompt with the following command.

```commandline
python rekognition_collections.py
``` 

**Video detection**

Run this example at a command prompt with the following command.

```commandline
python rekognition_video_detection.py
``` 

### Example structure

The example contains the following files.

**rekognition_collections.py**

Shows how to use Amazon Rekognition collection APIs. The `usage_demo` script creates 
a collection, indexes faces from a series of images that each contain pictures of 
the same group of people, and searches for matches against reference images.  

**rekognition_image_detection.py**

Shows how to use Amazon Rekognition image detection APIs. The `usage_demo` script 
detects faces, objects, text, and more by passing images to Amazon Rekognition. 

**rekognition_objects.py**

A set of classes that encapsulate data returned from Amazon Rekognition APIs,
such as faces, labels, and people. These classes are used to transform data from 
the service format to an object format.

**rekognition_video_detection.py**

Shows how to use Amazon Rekognition video detection APIs. The `usage_demo` script 
starts detection jobs that detect things like faces, objects, and people in a video. 
Because video detection is performed asynchronously, the demo also shows how to create 
a notification channel that uses Amazon Simple Notification Service (Amazon SNS) and
Amazon Simple Queue Service (Amazon SQS) to let the code poll for a job completion 
message.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/rekognition 
folder.

```commandline
python -m pytest
```

## Additional information

- [Boto3 Amazon Rekognition service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rekognition.html)
- [Amazon Rekognition documentation](https://docs.aws.amazon.com/rekognition)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
