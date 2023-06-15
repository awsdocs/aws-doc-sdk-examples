# Create a photo asset management application with the SDK for JavaScript

## Overview

This example shows you how to use the AWS SDK for JavaScript (v3) to create a photo asset management application using AWS services and a serverless architecture.

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon Simple Storage Service (Amazon S3) Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they are retrieved from Amazon S3, zipped, and the user is sent a link to the zip.

 <img width="4400" alt="PAM Diagram" src="https://user-images.githubusercontent.com/2723491/226400489-8ce85f78-fd53-4bcb-adda-42a230964a4c.png">

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

- An AWS account.
- NodeJS 18+.
- Docker Desktop.

## Structure

The PAM example has both shared resources and language-specific AWS Lambda functions. The CDK files let you deploy the shared resources and choose which set of functions to deploy to Lambda. This README is for the JavaScript Lambda functions.

## Create the resources

Follow the instructions in the [README for the Photo Asset Manager application](../../../../applications/photo-asset-manager/cdk/README.md) to use the AWS Cloud Development Kit (AWS CDK) or AWS Command Line Interface (AWS CLI) to create and manage the resources used in this example. You must be running Docker in order to complete the steps for this CDK.

## Build the code

The Lambda handlers are written in ESM and deployed as ESM with the added step of being bundled into one file.

This bundling happens automatically when you deploy the `<PAM_NAME>-<PAM_LANG>-PAM` stack. The CDK bundling step will run `npm run build` and then deploy the output to Lambda. The CDK step is declared [here](../../../../applications/photo-asset-manager/cdk/lib/backend/strategies.ts).

## Usage

Follow the steps in the [README for the Photo Asset Manager front-end](../../../../applications/photo-asset-manager/elros-pam/README.md).

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial. Follow the steps in [README for the Photo Asset Manager CDK](../../../../applications/photo-asset-manager/cdk/README.md) to clean up the resources for the CDK stacks.

## Next steps

Congratulations! You have created and deployed a photo asset management application.

## Additional resources

- [More cross-service examples like this](../)
- [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/welcome.html)
- [AWS SDK for JavaScript (v3) API Documentation](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html)
- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/lambda-nodejs.html)
- [Amazon API Gateway Developer Guide](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)
- [Amazon Rekognition Developer Guide](https://docs.aws.amazon.com/rekognition/latest/dg/what-is.html)
- [Amazon DynamoDB Developer Guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/)
- [Amazon Simple Storage Service User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon Simple Notification Service Developer Guide](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)

## Questions

### Why Vitest?

- Supports ESM without any additional configuration.
- Uses Vite to transform files during testing. This allows named imports from _all_ CJS modules and other features.
- Uses the popular Chai assertion library.

### Why Rollup and not Vite?

Vitest is being used as the test runner, but Vite is made for browser development. It uses Rollup under the hood for bundling libraries, but configuration is simpler when Rollup is used independently.

When bundling with Vite, it bundles for the browser, so any NodeJS natives without polyfills will cause an error to be thrown. Turning on Vite's server-side rendering feature gets around this, but it makes it less clear what we're trying to do. The configuration got to a point where it seemed more opaque than helpful. Falling back to pure Rollup clarified things.

---

Have more questions? [Create an issue](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new/choose).
