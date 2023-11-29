# Create a photo asset management application with the SDK for PHP

## Overview

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they will be retrieved from Amazon Simple Storage Service (Amazon S3), zipped, and the user is sent a link to the zip.

This PHP implementation shows how to deploy custom handlers written in PHP to AWS Lambda.
After the deploy step below, make sure to follow the additional steps for items unique to a PHP deployment.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

- An AWS account.

## Deploy

Follow the instructions in the [PAM application CDK README](../../../applications/photo-asset-manager/cdk/README.md), using `PAM_LANG=PHP`.

## Additional Steps

Open the console and go the AWS Lambda. Search for the four functions matching the pattern of `{PAM_NAME}-{PAM_LANGUAGE}-PAM-PamLambdas{Handler}*` where `{Handler}` matches `DetectLabels`, `Labels`, `PrepareDownload`, and `Upload`.
For each function, open the function and go to the `Configuration` tab. Edit the Environment variables and add an entry for `LD_LIBRARY_PATH=/var/task/bin/lib/`. Save the Environment variables for each Lambda function and then your Photo Asset Manager application should work. Go to the Cloudfront distribution URL given during the deploy step to continue.

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial. Follow the steps in [README for the Photo Asset Manager CDK](../../../applications/photo-asset-manager/cdk/README.md) to clean up the resources for the CDK stacks.

## Next steps

Congratulations! You have created and deployed a photo asset management application.

## Additional resources

- [AWS SDK for PHP Developer Guide](https://aws.amazon.com/developer/language/php/)
- [AWS SDK for PHP API Documentation](https://docs.aws.amazon.com/aws-sdk-php/v3/api/)
