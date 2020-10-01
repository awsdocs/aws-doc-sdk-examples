/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_listtopics.ts demonstrates how to retrieve a list of Amazon SNS topics.

Inputs (replace in code):
- REGION

Running the code:
ts-node sns_listtopics.ts
 */
// snippet-start:[sns.JavaScript.topics.listTopicsV3]
// Import required AWS SDK clients and commands for Node.js
const { SNSClient, ListTopicsCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create SNS service object
const sns = new SNSClient(REGION);

const run = async () => {
  try {
    const data = await sns.send(new ListTopicsCommand({}));
    console.log(data.Topics);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.topics.listTopicsV3]
export = { run }; //for unit tests only
