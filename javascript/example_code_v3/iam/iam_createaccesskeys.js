/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/iam-examples-managing-access-keys.html

Purpose:
iam_createaccesskeys.test.js demonstrates how to create a new AWS access key and AWS access key ID for an IAM user.

Inputs (in command line below):
- REGION
 - IAM_USER_NAME

Running the code:
node iam_createaccesskeys.js REGION IAM_USER_NAME >newuserkeys.txt
(This create newuserkeys.txt and adds the access id key and secret key to it.)
 */

// snippet-start:[iam.JavaScript.keys.createAccessKey]
async function run() {
  // Load the AWS SDK for Node.js
  const {IAMClient, CreateAccessKeyCommand} = require("@aws-sdk/client-iam");
  // Create IAM service object
  const region = process.argv[2];
  const iam = new IAMClient(region);
  const userName = process.argv[3];
  try{
    const data = await iam.send(new CreateAccessKeyCommand(userName));
    console.log("Success", data.AccessKey);
    }
  catch (err) {
  console.log('Error', err);
    }
};
run();
// snippet-end:[iam.JavaScript.keys.createAccessKey]
exports.run = run;
