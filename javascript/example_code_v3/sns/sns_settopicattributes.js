/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sns-examples-managing-topics.html

Purpose:
sns_settopicattributes.js demonstrates how to set the attributes of an Amazon SNS topic.

Inputs:
- REGION (in commmand line input below)
- ATTRIBUTE_NAME (in commmand line input below)
- TOPIC_ARN (in commmand line input below)
- NEW_ATTRIBUTE_VALUE (in commmand line input below)

Running the code:
node sns_settopicattributes.js REGION ATTRIBUTE_NAME TOPIC_ARN NEW_ATTRIBUTE_VALUE
 */
// snippet-start:[sns.JavaScript.topics.setTopicAttributes]
async function run() {
  try {
    const {SNS, SetTopicAttributesCommand} = require("@aws-sdk/client-sns");
    const region = process.argv[2];
    const sns = new SNS(region);
    const params = {
      AttributeName: process.argv[3], /* required */
      TopicArn: process.argv[4], /* required */
      AttributeValue: process.argv[5]
    };
    const data = await sns.send(new SetTopicAttributesCommand(params));
    console.log(data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.topics.setTopicAttributes]
exports.run = run;
