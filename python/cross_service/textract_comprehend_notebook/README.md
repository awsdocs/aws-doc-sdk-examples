## Cross-service example: Using Textract and Comprehend to detect entities in extracted text

### Purpose

This AWS cross-service example guides you through the process of using Amazon Textract and Amazon Comprehend to extract text from an image and then detect entities in that extracted text. The guide also connects to Amazon S3 to retrieve an image stored there. The AWS SDK for Python is used to connect to the following services:

* Amazon S3
* Amazon Textract
* Amazon Comprehend

### Prerequisites

To complete the tutorial, you need the following:

* An AWS account. For more information see [AWS SDKs and Tools Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/overview.html)
* Python 3.5 or later
* The AWS Python SDK (Boto3)
* The Python libraries PIL and Pandas
* Create the resources. See [Creating the resources](https://quip-amazon.com/ERHdAdsHeZPx/README-formats-in-Code-Examples-repo#BaY9CA40Fzv).

### ⚠️ Important

- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. 
- Running the unit tests might result in charges to your AWS account. [optional]

### Creating the resources

For this example, you require the following resources:

* AWS Access Credentials
* An S3 bucket
* An image in the S3 bucket containing text

You can create these resource by using the console or the Python SDK.

### Building the code

In order to build the code, just follow the directions in the notebook.

### Deleting the resources

Remember to delete any resources you are no longer using, including your S3 bucket.