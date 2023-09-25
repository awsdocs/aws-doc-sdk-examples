#  Create a photo asset management application with the SDK for C++

## Overview

This example shows you how to use the AWS SDK for C++ to create a photo management application using AWS services and a serverless architecture.

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images that are analyzed with label detection. Those labels are then stored in an Amazon DynamoDB table. Users can later request a bundle of images associated with a list of labels. When images are requested, they are retrieved from Amazon Simple Storage Service (Amazon S3), zipped, and the user is sent a link to download the resulting zip file.
For more details on the complete application, see the [PAM application directory](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/applications/photo-asset-manager) in this repository.

![Pam overview diagram](README_images/pam_overview.png)

### Frontend
The frontend is a single React app that uses the Cloudscape Design System. With the React app, users can authenticate with an Amazon Cognito flow. The app is deployed to an Amazon Simple Storage Service (Amazon S3) bucket using the provided AWS Cloud Development Kit (AWS CDK) stacks. It is publicly exposed by using an Amazon CloudFront distribution. The name is dynamically created during the AWS CDK deployment.

![Pam UUI screenshot](README_images/pam_ui.png)

### Backend

The backend of the PAM application is implemented with these AWS Lambda functions:

- **Labels** - Serverless API endpoint that returns the labels and label count of images in the S3 storage bucket.
- **Upload** - Serverless API endpoint that returns a presigned URL for uploading an image.
- **DetectLabelsFunction** - Amazon S3 event function that is invoked when an image object is created in an S3 storage bucket. The new image is analyzed using Amazon Rekognition, and label information is stored in a DynamoDB table.
- **DownloadFunction** - Amazon API Gateway function that combines images into a zip file in an S3 storage bucket with a presigned URL for download, and sends a notification message using Amazon Simple Notification Service (Amazon SNS).

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Prerequisites

To run the code in this example, you need the following:

+ An AWS account.
+ Docker Desktop.
+ NodeJS 18+.
+ AWS SDK for C++. For SDK installation instructions, see [Get started with the AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html).

## Create the resources

Follow the instructions in the
[README for the Photo Asset Manager application CDK](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/applications/photo-asset-manager/cdk/README.md).
to use the AWS Cloud Development Kit (AWS CDK) or AWS Command Line Interface
(AWS CLI) to create and manage the resources used in this example. You must be running Docker in order to complete the steps for the AWS CDK.

## Build the code

The code is automatically built and uploaded when the resources are created.

The code is built using Docker and the provided [Dockerfile](Dockerfile).
The built code is located within the Docker container at `/pam_lambda/build/cpp_pam_lambdas.zip`.

To build and copy the code to the local file system, use the following commands.

```bash
docker build . -f Dockerfile -t cpp_pam_image
docker run --name pam_lambda cpp_pam_image
docker cp pam_lambda:/pam_lambda/build/cpp_pam_lambdas.zip .
```

### Application notes
* The maximum pixel size for analysis is 10000x10000. Larger images will not be analyzed.
* Some email clients do not support the long length download URLs. You might need to remove spaces from the URL before downloading.

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.
Follow the instructions in the [README for the Photo Asset Manager application CDK](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/applications/photo-asset-manager/cdk/README.md)
to clean up the resources for the CDK stacks.

## Next steps

Congratulations! You have created and deployed a Photo Asset Management application.

## Additional resources

- [AWS SDK for C++ Developer Guide](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/welcome.html)
- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/lambda-csharp.html)
- [Amazon API Gateway Developer Guide](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)
- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon DynamoDB Developer Guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/)
- [Amazon Simple Storage Service User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon Simple Notification Service Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)

For more AWS multiservice examples, see
[cross-service](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/cpp/example_code/cross-service).
