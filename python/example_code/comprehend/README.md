# Amazon Comprehend code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Comprehend.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Comprehend uses natural language processing (NLP) to extract insights about the content of documents without the need of any special preprocessing._

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

- [CreateDocumentClassifier](comprehend_classifier.py#L43)
- [DeleteDocumentClassifier](comprehend_classifier.py#L134)
- [DescribeDocumentClassificationJob](comprehend_classifier.py#L206)
- [DescribeDocumentClassifier](comprehend_classifier.py#L89)
- [DescribeTopicsDetectionJob](comprehend_topic_modeler.py#L87)
- [DetectDominantLanguage](comprehend_detect.py#L33)
- [DetectEntities](comprehend_detect.py#L53)
- [DetectKeyPhrases](comprehend_detect.py#L77)
- [DetectPiiEntities](comprehend_detect.py#L101)
- [DetectSentiment](comprehend_detect.py#L125)
- [DetectSyntax](comprehend_detect.py#L148)
- [ListDocumentClassificationJobs](comprehend_classifier.py#L228)
- [ListDocumentClassifiers](comprehend_classifier.py#L113)
- [ListTopicsDetectionJobs](comprehend_topic_modeler.py#L109)
- [StartDocumentClassificationJob](comprehend_classifier.py#L151)
- [StartTopicsDetectionJob](comprehend_topic_modeler.py#L36)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Detect document elements](comprehend_detect.py)
- [Detect entities in text extracted from an image](../../cross_service/textract_comprehend_notebook)
- [Run a topic modeling job on sample data](comprehend_topic_modeler.py)
- [Train a custom classifier and classify documents](comprehend_classifier.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Detect document elements

This example shows you how to do the following:

- Detect languages, entities, and key phrases in a document.
- Detect personally identifiable information (PII) in a document.
- Detect the sentiment of a document.
- Detect syntax elements in a document.

<!--custom.scenario_prereqs.comprehend_Usage_DetectApis.start-->
<!--custom.scenario_prereqs.comprehend_Usage_DetectApis.end-->

Start the example by running the following at a command prompt:

```
python comprehend_detect.py
```


<!--custom.scenarios.comprehend_Usage_DetectApis.start-->
<!--custom.scenarios.comprehend_Usage_DetectApis.end-->

#### Detect entities in text extracted from an image

This example shows you how to use Amazon Comprehend to detect entities in text extracted by Amazon Textract from an image that is stored in Amazon S3.


<!--custom.scenario_prereqs.cross_TextractComprehendDetectEntities.start-->
<!--custom.scenario_prereqs.cross_TextractComprehendDetectEntities.end-->


<!--custom.scenarios.cross_TextractComprehendDetectEntities.start-->
<!--custom.scenarios.cross_TextractComprehendDetectEntities.end-->

#### Run a topic modeling job on sample data

This example shows you how to do the following:

- Run an Amazon Comprehend topic modeling job on sample data.
- Get information about the job.
- Extract job output data from Amazon S3.

<!--custom.scenario_prereqs.comprehend_Usage_TopicModeler.start-->
<!--custom.scenario_prereqs.comprehend_Usage_TopicModeler.end-->

Start the example by running the following at a command prompt:

```
python comprehend_topic_modeler.py
```


<!--custom.scenarios.comprehend_Usage_TopicModeler.start-->
<!--custom.scenarios.comprehend_Usage_TopicModeler.end-->

#### Train a custom classifier and classify documents

This example shows you how to do the following:

- Create an Amazon Comprehend multi-label classifier.
- Train the classifier on sample data.
- Run a classification job on a second set of data.
- Extract the job output data from Amazon S3.

<!--custom.scenario_prereqs.comprehend_Usage_ComprehendClassifier.start-->
<!--custom.scenario_prereqs.comprehend_Usage_ComprehendClassifier.end-->

Start the example by running the following at a command prompt:

```
python comprehend_classifier.py
```


<!--custom.scenarios.comprehend_Usage_ComprehendClassifier.start-->
<!--custom.scenarios.comprehend_Usage_ComprehendClassifier.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Comprehend Developer Guide](https://docs.aws.amazon.com/comprehend/latest/dg/what-is.html)
- [Amazon Comprehend API Reference](https://docs.aws.amazon.com/comprehend/latest/APIReference/welcome.html)
- [SDK for Python Amazon Comprehend reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/comprehend.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0