# Create a photo analyzer application using the AWS SDK for JavaScript (v3)

## Purpose
Demonstrate the use of AWS Rekognition to analyze images located in an Amazon Simple Storage Service (Amazon S3) bucket.

Analyze many images and generate reports for each image in an Amazon S3 bucket, breaking the image down into a series of labels. Then, use Amazon Simple Email Service (Amazon SES) to send emails with a link to each report. The following AWS services are used:

- [AWS Rekognition](https://aws.amazon.com/rekognition/)
- [Amazon Simple Storage Services (S3)](https://aws.amazon.com/s3/)
- [Amazon Simple Email Services (SES)](https://aws.amazon.com/ses/)

## Prerequisites

To build this example, complete the following steps:

- Create an AWS account. For more information, see [AWS SDKs and Tools Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/overview.html).
- Follow the [prerequisites](../../../README.md#prerequisites) in the top level `javascriptv3` folder.
- Install the AWS CLI. For more information, see [Install or update to the latest version of the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html).

## ⚠ Important

- We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the _AWS Identity and Access Management User Guide_.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account. We recommend you destroy the resources when you are finished. For instructions, see [Destroying the resources](#destroying-the-resources).
- Running the unit tests might result in charges to your AWS account.

## Create the resources using Amazon CloudFormation

1. Run `npm run deploy`. This will use the AWS CLI to deploy the resources you need.

### Verifying an email address on Amazon SES

1. Follow the instructions for [creating an email address identity](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html#verify-email-addresses-procedure)

## Bundling and running the app 

This is a static site consisting only of HTML, CSS, and client-side JavaScript. However, a build step is required to enable the modules to work natively in the browser. The build step also uses your local credentials to fetch the CloudFormation outputs and supplies them as environment variables to Webpack.

A helper script has been provided to bundle the code and start an HTTP server. You'll need to provide the email address you verified in SES, and an AWS region as environment variables. Replace `<EMAIL_ADDRESS>` and `<REGION>` with your verified email and preferred AWS region (defaults to 'us-east-1').

1. Run `VERIFIED_EMAIL_ADDRESS="<EMAIL_ADDRESS>" REGION="<REGION>" npm run dev`
    There will be terminal output that looks something like the below:
    ```
    > @aws-doc-sdk-examples/aws-sdk-v3-cross-service-examples-photo-analyzer@1.0.0 dev
    > npm run build && npx http-server ./dist/


    > @aws-doc-sdk-examples/aws-sdk-v3-cross-service-examples-photo-analyzer@1.0.0 build
    > webpack --mode development

    asset main.js 2.07 MiB [compared for emit] (name: main)
    asset vendors-node_modules_aws-sdk_credential-provider-cognito-identity_dist-es_loadCognitoIdentity_js.main.js 55.5 KiB [compared for emit] (id hint: vendors)
    orphan modules 351 KiB [orphan] 347 modules
    runtime modules 7.25 KiB 10 modules
    javascript modules 1.15 MiB 544 modules
    json modules 17.3 KiB
    ../../../node_modules/@aws-sdk/client-cognito-identity/package.json 3.21 KiB [built] [code generated]
    ../../../node_modules/@aws-sdk/client-s3/package.json 3.99 KiB [built] [code generated]
    ../../../node_modules/@aws-sdk/client-rekognition/package.json 3.1 KiB [built] [code generated]
    ../../../node_modules/@aws-sdk/client-ses/package.json 3.02 KiB [built] [code generated]
    ../../../node_modules/@aws-sdk/util-endpoints/dist-es/lib/aws/partitions.json 4 KiB [built] [code generated]
    webpack 5.94.0 compiled successfully in 848 ms
    Starting up http-server, serving ./dist/

    http-server version: 14.1.1

    http-server settings: 
    CORS: disabled
    Cache: 3600 seconds
    Connection Timeout: 120 seconds
    Directory Listings: visible
    AutoIndex: visible
    Serve GZIP Files: false
    Serve Brotli Files: false
    Default File Extension: none

    Available on:
    http://127.0.0.1:8080
    http://192.168.254.17:8080
    http://11.129.21.155:8080
    Hit CTRL-C to stop the server
    ```
2. Open a web browser to the described address.
3. Select a file to upload.
4. Click 'Upload image'.
5. Provide the email address you previously verified.
6. Click 'Analyze photos'.
7. You should receive one or more email with links to the generated CSV.

## Destroying the resources

1. Empty the created S3 buckets.
    a. `aws s3 rm s3://bucket-name --recursive`
2. Run `npm run destroy`.