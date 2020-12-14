/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_settopicattributes.ts demonstrates how to set the attributes of an Amazon SNS topic.

Inputs (replace in code):
- REGION
- ATTRIBUTE_NAME
- TOPIC_ARN
- NEW_ATTRIBUTE_VALUE

Running the code:
ts-node sns_settopicattributes.ts
 */
// snippet-start:[sns.JavaScript.topics.setTopicAttributesV3]

// Import required AWS SDK clients and commands for Node.js
const { SNSClient, SetTopicAttributesCommand } = require("@aws-sdk/client-sns");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = {
  AttributeName: "ATTRIBUTE_NAME", // ATTRIBUTE_NAME
  TopicArn: "TOPIC_ARN", // TOPIC_ARN
  AttributeValue: "NEW_ATTRIBUTE_VALUE", //NEW_ATTRIBUTE_VALUE
};

// Create SNS service object
const sns = new SNSClient(REGION);

const run = async () => {
  try {
    const data = await sns.send(new SetTopicAttributesCommand(params));
    console.log("Success, attributed updated", data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.topics.setTopicAttributesV3]

