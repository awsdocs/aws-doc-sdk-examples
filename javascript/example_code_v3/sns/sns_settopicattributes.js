/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-managing-topics.html.

Purpose:
sns_settopicattributes.js demonstrates how to set the attributes of an Amazon SNS topic.

Inputs:
- REGION (into command line below)
- ATTRIBUTE_NAME (into command line below)
- TOPIC_ARN (into command line below)
- NEW_ATTRIBUTE_VALUE (into command line below)

Running the code:
node sns_settopicattributes.js REGION ATTRIBUTE_NAME TOPIC_ARN NEW_ATTRIBUTE_VALUE
 */
// snippet-start:[sns.JavaScript.v3.topics.setTopicAttributes]
// Import required AWS SDK clients and commands for Node.js
const {SNS, SetTopicAttributesCommand} = require("@aws-sdk/client-sns");
// Set the AWS Region
const region = process.argv[2];
// Create SNS service object
const sns = new SNS(region);
// Set the parameters
const params = {
  AttributeName: process.argv[3], /* required */
  TopicArn: process.argv[4], /* required */
  AttributeValue: process.argv[5]
};

async function run() {
  try {
    const data = await sns.send(new SetTopicAttributesCommand(params));
    console.log('Success, attributed updated', data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.v3.topics.setTopicAttributes]
exports.run = run; //for unit tests only
