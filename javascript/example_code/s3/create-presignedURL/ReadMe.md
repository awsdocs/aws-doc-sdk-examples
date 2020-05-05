# README for Generating Pre-signed URLs

## Purpose

This example uses a Node.js function to generate a pre-signed URL that uploads a specified file to a specified S3 bucket. A pre-signed URL grants temporary access to users who don’t have permission to directly run AWS operations in your account. 

## Prerequisites

To build and run this example, you need the following:
- Node.js. For more information about installing Node.js, see the [Node.js website](https://nodejs.org).
- The AWS SDK for JavaScript.  For more information about installing the AWS SDK for JavaScript, see see *Step 3* on the [Getting Started in Node.js in the JavaScript SDK Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html).
- AWS credentials, either configured in a local AWS credentials file, or by setting the AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY environment variables. For more information, see  [Setting Credentials in the JavaScript SDK Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/setting-credentials.html).
- Create an S3 bucket. For more information, see [Working with Amazon S3 Buckets](https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html#create-bucket-intro).

## Assumptions
Anyone with valid security credentials can create a pre-signed URL. However, the pre-signed URL must be created by someone who has permission to perform the operation that the pre-signed URL is based on.

## Running the code
1. Create a local folder.
2. In the command line navigate to the folder you created and enter the following command (in command line) to create a package.json:
```javascript
npm init
```
3. Copy the code example file to the local folder. 
4. Update the parameters as described in the code example file.
5. Enter the following at the command line:
```javascript
node <code example filename>
```

## Running the unit test
The following assumes you've just run the code as described above.

1. Install jest. For more information, see the [Jest website](https://jestjs.io/).
2. Update the package.json 'scripts' key with the value-property key "test":"jest", as below:
```json
"scripts": { "test": "jest" } 
```
3. Copy the test file into the local folder you created.
*Note*:The test filename is located in the 'test' folder. 
*Note*: The test filename is identical to the example code filename, but has '.test' before the '.js' file extension.
6. Run the following in the command line:
```javascript
npm test
```
    
## Additional information

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see [Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/) on the AWS website. 
- Running this code might result in charges to your AWS account.


Copyright Amazon.com.
SPDX-License-Identifier: Apache-2.0



