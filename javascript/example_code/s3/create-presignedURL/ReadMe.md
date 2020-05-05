# Copyright Amazon.com.
# SPDX-License-Identifier: Apache-2.0

# README for Generating Pre-signed URLs

## Purpose

This example using a Node.js function to generate a pre-signed URL that uploads a specified S3 bucket. A pre-signed URL allows you to grant temporary access to users who don’t have permission to directly run AWS operations in your account. A pre-signed URL is signed with your credentials and can be used by any user.

## Prerequisites

To build and run this example, you need the following:

- Node.js. For more information about installing Node.js, see the [https://nodejs.org](Node.js website).

- AWS credentials, either configured in a local AWS credentials file, or by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables. For more information, see *Step 1* on the [Getting Started in Node.js in the JavaScript SDK Developer Guide](_https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html_).

- The AWS SDK for JavaScript.  For more information about installing the AWS SDK for JavaScript, see see *Step 3* on the [Getting Started in Node.js in the JavaScript SDK Developer Guide](_https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html_).

- Create an S3 bucket. For more information, see [Working with Amazon S3 Buckets](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html#create-bucket-intro).

## Assumptions
Anyone with valid security credentials can create a pre-signed URL. However, in order for you to successfully upload an object, the pre-signed URL must be created by someone who has permission to perform the operation that the presigned URL is based on.


## Running the code

1. Copy the sample code to a new file in a local folder, saving it as 's3_presignedURL.js'. 
2. Enter the following at the command line:
```javascript
node s3_presignedURL.js
```

## Running the tests

1. Install Jest. For more information, see the [https://jestjs.io/](Jest website).
2. Copy the test code to a new file in a local folder, saving it as 's3_presignedURL.test.js'.
3. Enter the following in the command line:
```javascript
npm test
```

## Additional information

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see [Grant Least Privilege](_https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege_) in the *AWS Identity and Access Management User Guide*.

- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [Region Table](_https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/_) on the AWS website. 

- Running this code might result in charges to your AWS account.

- <Start typing additional information here.>


