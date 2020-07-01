/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/using-lambda-function-prep.html.
Purpose:
lambda-function-setup.js demonstrates how to create an AWS Lambda function.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (in code)
- ZIP_FILE_NAME (in code)

Running the code:
node lambda-function-setup.js REGION ACCESS_KEY_ID
*/

// snippet-start:[lambda.JavaScript.v3.LambdaFunctionSetUp]
// Load the Lambda client
const { LambdaClient, CreateFunctionCommand } = require('@aws-sdk/client-lambda');
// Instantiate a Lambda client
const region = process.argv[2];
const lambda = new LambdaClient(region);

var params = {
  Code: { /* required */
    S3Bucket: 'BUCKET_NAME',
    S3Key: 'ZIP_FILE_NAME'
  },
  FunctionName: 'slotpull', /* required */
  Handler: 'index.handler', /* required */
  Role: 'arn:aws:iam::650138640062:role/v3-lambda-tutorial-lambda-role', /* required */
  Runtime: 'nodejs12.x', /* required */
  Description: 'Slot machine game results generator',
};
lambda.send(new CreateFunctionCommand(params)).then(
  data => { console.log(data) }, // successful response
  err => {console.log(err)} // an error occurred
);

// snippet-end:[lambda.JavaScript.v3.LambdaFunctionSetUp]
