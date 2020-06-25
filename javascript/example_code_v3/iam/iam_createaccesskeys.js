/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/iam-examples-managing-access-keys.html.

Purpose:
iam_createaccesskeys.js demonstrates how to create a new AWS Access Key and AWS Access Key ID for an IAM user.

Inputs (into command line below):
- REGION
 - IAM_USER_NAME

Running the code:
node iam_createaccesskeys.js REGION IAM_USER_NAME >newuserkeys.txt
(This create newuserkeys.txt and adds the access id key and secret key to it.)
 */
// snippet-start:[iam.JavaScript.v3.keys.createAccessKey]
// Import required AWS SDK clients and commands for Node.js
const {IAMClient, CreateAccessKeyCommand} = require("@aws-sdk/client-iam");
// Set the AWS Region
const region = process.argv[2];
// Create IAM service object
const iam = new IAMClient(region);
// Set the parameters
const userName = process.argv[3];

async function run() {
  try{
    const data = await iam.send(new CreateAccessKeyCommand(userName));
    console.log("Success", data.AccessKey);
    }
  catch (err) {
  console.log('Error', err);
    }
};
run();
// snippet-end:[iam.JavaScript.v3.keys.createAccessKey]
exports.run = run; //for unit tests only
