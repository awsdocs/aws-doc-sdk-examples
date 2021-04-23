# Amazon Textract React example

## Purpose

Shows how to use the AWS SDK for JavaScript, Version 3, to build a React application
that uses Amazon Textract to extract data from a document image and display it in
an interactive web page. This example runs in a web browser and requires an 
authenticated Amazon Cognito identity for credentials. It uses Amazon Simple Storage 
Service (Amazon S3) for storage, and for notifications it polls an Amazon Simple 
Queue Service (Amazon SQS) queue that is subscribed to an Amazon Simple Notification 
Service (Amazon SNS) topic.

Amazon Textract is a machine learning service that analyzes scanned document images. 
Amazon Textract extracts text, form, and table information from typed and handwritten 
documents like medical records, financial reports, and tax forms.   

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
- This example uses React and Bootstrap, popular libraries for building user interfaces 
that run in a web browser. Use of these libraries is for illustrative purposes only 
and does not indicate endorsement.
- Running this code might result in charges to your AWS account.

## Prerequisites and setup

1. You must have an AWS account and have your default credentials and AWS Region
configured as described in the [AWS Tools and SDKs Shared Configuration and
Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).

1. Install the dependencies listed in `package.json`. This package was tested with
npm version 6.14.9.

    ```
    cd javascriptv3/example_code/cross-services/textract-react 
    npm install
    ```

1. Create AWS resources needed for the example by running the `setup.yaml` AWS
CloudFormation script. Substitute your own stack name for `<stack-name>` in the command.
This name must be unique within your AWS account. 

    ```
    aws cloudformation create-stack --stack-name <stack-name> --template-body file://setup.yaml --capabilities CAPABILITY_IAM
    ```

    The `setup.yaml` file was built from the 
    [AWS Cloud Development Kit (AWS CDK)](https://docs.aws.amazon.com/cdk/) 
    source script here: 
    [/resources/cdk/textract_example_s3_sns_sqs_cognito/setup.ts](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/resources/cdk/textract_example_s3_sns_sqs_cognito/setup.ts). 

1. Verify the stack is completely deployed before the next step by running the 
following command:

    ```
    aws cloudformation describe-stacks --stack-name <stack-name>
    ```
    
    The stack is deployed when it reports a StackStatus of CREATE_COMPLETE.

1. Set up additional items needed for the example by running the `DeployDestroy.js` 
script with the `deploy` option and pass the stack name you used in the previous step.

    ```
    node DeployDestroy.js deploy <stack-name>
    ```
    
    This script puts output data from the CloudFormation stack into the `Config.js` file 
    and uploads a default image to Amazon S3. 
    
## Running the code

The application runs in a web browser.

1. Start the development web server by running the following commands.

    ```
    cd src
    npm start
    ```

1. If your browser does not open automatically, open a browser and navigate to 
[http://localhost:3000](http://localhost:3000).

1. Select **Sign in**. This navigates you to an Amazon Cognito hosted UI where you
can sign in or sign up to create an Amazon Cognito identity. If you create a new
identity, you must provide a valid email address to receive a verification code. An
Amazon Cognito authenticated role is required to access the asynchronous Amazon
Textract APIs used by this example.

1. After you sign in to Amazon Cognito, you are redirected back to the example 
application.

1. Select **Load** to load the default image.

1. Select **Extract** to extract data from the image. Extracted data is displayed
in the **Data explorer** panel.

1. Select elements in the **Data explorer** panel to see bounding polygons drawn
over the image.

## Destroy resources

Follow these steps to destroy example resources.
 
1. Run the `DeployDestroy.js` script with the `destroy` flag at a command 
prompt.

    ```
    node DeployDestroy.js destroy
    ```
    
    This script deletes the default image from the Amazon S3 bucket, removes users from
    the Amazon Cognito user pool, and removes identities from the Amazon Cognito identity
    pool.

1. Delete the CloudFormation stack and all of its resources by running the following
at a command prompt with the stack name you used to create the stack:

    ```
    aws cloudformation delete-stack --stack-name <stack-name>
    ```
    
1. Verify the stack is completely destroyed by running the following command:

    ```
    aws cloudformation describe-stacks --stack-name <stack-name>
    ```
    
    The stack is destroyed when CloudFormation returns a ValidationError and reports
    that the stack does not exist.

## Example structure

This example uses an MVC (model view controller) pattern that separates the user
interface components built with React from the model component that calls AWS 
services. A main application render function is subscribed to the model so that it is
informed whenever the underlying data changes.

This example uses [Bootstrap](https://getbootstrap.com/) for styling and layout.
Bootstrap uses class names to specify how an element is styled, such as 
`btn btn-primary` to denote a primary button.

### React components

The following files each contain JSX that defines elements of the React application. 

* App.js
* ExplorerCard.js
* ExplorerTree.js
* ExtractButtons.js
* ImageDisplay.js
* ImageLoader.js
* LoginCard.js

### Model components

**AwsFactory.js**

Creates AWS clients that are initialized with Amazon Cognito identity credentials.

**TextractModel.js**

Handles calls to AWS services, formats returned data, and informs the render function
when data changes so the application can render any changes.

### Utility components

**Config.js**

Generated by the `DeployDestroy.js` script. Contains deployment information from 
the CloudFormation stack, such as the Amazon S3 bucket name and Amazon SQS queue URL.

**index.js**

The main script called when the page is loaded. Gets the session token from the URL
query parameters, creates AWS clients, and subscribes the main render function to
the model.

**Utils.js**

Maps data elements to UI elements like colors and filters.

**public/index.html**

The HTML for the page. Because this is a React application, the HTML is minimal.

## Running the tests

Launch the test runner in interactive watch mode by running the following in the 
project folder at a command prompt:

```
npm test
```

## Additional information

- [SDK for JavaScript Amazon Textract reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-textract/index.html)
- [Amazon Textract Documentation](https://docs.aws.amazon.com/textract/)

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
