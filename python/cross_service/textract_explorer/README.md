# Amazon Textract explorer example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Textract to detect text, 
form, and table elements in a document image. The input image and Textract output are
shown in a Tkinter application that lets you explore the detected elements. 

* Submit a document image to Textract and explore the output of detected elements.
* Submit images directly to Textract or through an Amazon Simple Storage Service 
(Amazon S3) bucket.
* Use asynchronous APIs to start a job that publishes a notification to an Amazon 
Simple Notification Service (Amazon SNS) topic when the job completes.
* Poll an Amazon Simple Queue Service (Amazon SQS) queue for a job completion message
and display the results. 

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8.8 or later
- Boto3 1.16.49 or later
- Pillow 8.1.1 or later
- PyTest 6.0.2 or later (to run unit tests)

## Cautions

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

The asynchronous APIs used in this example require an Amazon S3 bucket to contain 
input images, an Amazon SNS topic to publish notifications, and an Amazon SQS queue
that the application can poll for notification messages. These resources are managed by
an AWS CloudFormation stack that is defined in the accompanying `setup.yaml` file. 

### Deploy resources

Deploy prerequisite resources by running the example script with the `deploy` flag at 
a command prompt.

```
python textract_demo_launcher.py deploy
```

### Run the usage demonstration

Run the usage example with the `demo` flag at a command prompt.

```
python textract_demo_launcher.py demo
``` 

### Destroy resources

Destroy example resources by running the script with the `destroy` flag at a command 
prompt.

```
python textract_demo_launcher.py destroy
``` 

### Example structure

The example contains the following files.

**textract_app.py**

A Tkinter application that displays document images, starts Textract synchronous and
asynchronous detection processes, and shows the hierarchy of detected elements.
Elements can be clicked to explore the hierarchy and draw bounding polygons on the
input image.

**textract_demo_launcher.py**

Launches the Textract demo.

* Run with the `deploy` option to deploy prerequisite
resources defined in the `setup.yaml` CloudFormation stack. 
* Run with the `demo` option to show the Tkinter application.
* Run with the `destroy` option to destroy prerequisite resources.    

**textract_wrapper.py**

Wraps Textract, Amazon S3, Amazon SNS, and Amazon SQS functions that are used by the 
application.

**setup.yaml**

Contains a CloudFormation script that is used to create the resources needed for 
the demo.  

* An Amazon S3 bucket that contains input document images.
* An Amazon SNS topic that receives notification of job completion.
* An IAM role that grants permission to publish to the topic.
* An Amazon SQS queue that is subscribed to receive messages from the topic.

The `setup.yaml` file was built from the 
[AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
source script here: 
[/resources/cdk/textract_example_s3_sns_sqs/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/textract_example_s3_sns_sqs/setup.ts). 

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your 
[GitHub root]/python/cross_service/textract_explorer folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon Textract reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/textract.html)
- [Amazon Textract Documentation](https://docs.aws.amazon.com/textract/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
