/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-dead-letter-queues.html.

Purpose:
sqs_deadletterqueue.js demonstrates how to enable the dead-letter functionality of an Amazon SQS queue.

Inputs (replace in code):
- SQS_QUEUE_URL
- DEAD_LETTER_QUEUE_ARN

Running the code:
node sqs_deadletterqueue.js
*/
// snippet-start:[sqs.JavaScript.deadLetter.setQueueAttributesV3]
// Import required AWS SDK clients and commands for Node.js
import { SetQueueAttributesCommand } from  "@aws-sdk/client-sqs";
import { sqsClient } from  "./libs/sqsClient.js";

// Set the parameters
var params = {
  Attributes: {
    RedrivePolicy:
      '{"deadLetterTargetArn":"DEAD_LETTER_QUEUE_ARN",' +
      '"maxReceiveCount":"10"}', //DEAD_LETTER_QUEUE_ARN
  },
  QueueUrl: "SQS_QUEUE_URL", //SQS_QUEUE_URL
};

const run = async () => {
  try {
    const data = await sqsClient.send(new SetQueueAttributesCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[sqs.JavaScript.deadLetter.setQueueAttributesV3]
// For unit tests only.
// module.exports ={run, params};
