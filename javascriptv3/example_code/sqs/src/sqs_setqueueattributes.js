/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sqs-examples-send-receive-messages.html.

Purpose:
sqs_setqueueattributes.js demonstrates how to programmatically set a policy on an SQS queue

Inputs (replace in code):
- SQS_QUEUE_ARN (into command line below; e.g., 'arn:aws:sqs:us-east-2:123456789012:MyQueue')
- SNS_TOPIC_ARN (into command line below; e.g., 'arn:aws:sns:us-east-2:123456789012:MyTopic')

Running the code:
node sqs_setqueueattributes.js
 */
// snippet-start:[sqs.JavaScript.messages.setQueueAttributesV3]
// Import required AWS SDK clients and commands for Node.js
const {SetQueueAttributesCommand} = require("@aws-sdk/client-sqs");
const {sqsClient} = require("./libs/sqsClient.js");

const params = {
    Attributes: {
        policy: `{
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "sns.amazonaws.com"
                    },
                    "Action": "sqs:SendMessage",
                    "Resource": "SQS_QUEUE_ARN",
                    "Condition": {
                        "ArnEquals": {
                            "aws:SourceArn": "SNS_TOPIC_ARN"
                        }
                    }
                }
            ]
        }`
    }

}

const run = async () => {
    const command = new SetQueueAttributesCommand(params)

    let results;
    try {
        results = sqsClient.send(command)
        console.log("Success, attributes set!")
        return results;
    } catch (err) {
        console.error(err)
    }
};
run();

// snippet-end:[sqs.JavaScript.messages.setQueueAttributesV3]
// For unit tests only.
module.exports = {run, params};