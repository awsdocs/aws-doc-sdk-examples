/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

// Load the Lambda client
const { LambdaClient, CreateFunctionCommand } = require('@aws-sdk/client-lambda');
// Instantiate a Lambda client
const lambda = new LambdaClient({region: 'us-west-2'});

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
