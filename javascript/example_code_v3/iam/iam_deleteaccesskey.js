/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_deleteaccesskey.js demonstrates how to delete the AWS access key pair for an IAM user.

Inputs (into command line below):
- REGION
- ACCESS_KEY_ID
- USER_NAME

Running the code:
  node iam_deleteaccesskey.js REGION ACCESS_KEY_ID USER_NAME
 */
// snippet-start:[iam.JavaScript.v3.keys.deleteAccessKey]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, DeleteAccessKeyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const params = {
  AccessKeyId: process.argv[3],
  UserName: process.argv[4]
};

async function run() {
  try {
    const data = await iam.send(new DeleteAccessKeyCommand(params));
    console.log("Success", data);
  }
  catch (err) {
    console.log('Error', err);
  }
}
run();
// snippet-end:[iam.JavaScript.v3.keys.deleteAccessKey]
exports.run = run; //for unit tests only
