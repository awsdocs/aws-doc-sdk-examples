/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sns-examples-sending-sms.html.

Purpose:
sns_confirmsubscription.js demonstrates how to verify an endpoint owner's intent to receive messages by validating the token sent to the endpoint by an earlier Subscribe action.
If the token is valid, the action creates a new subscription and returns its Amazon Resource Name (ARN). This call requires an AWS signature only when the AuthenticateOnUnsubscribe flag is set to "true".
For more information on subscription confirmations, see https://docs.aws.amazon.com/sns/latest/api/API_ConfirmSubscription.html.

Inputs (replace in code):
- TOKEN: token sent to an endpoint during subscribe action. For example, for an email endpoint, the token is in the URL of the Confirm Subscription page sent by email. For example, 'abc123' is the token in the URL
https://sns.us-east-1.amazonaws.com/confirmation.html?TopicArn=arn:aws:sns:us-east-1:xxxxx:my-aws-topic&Token=abc123&Endpoint=address@email.com/
- TOPIC_ARN
- AuthenticateOnUnsubscribe: either 'true' or 'false'

Running the code:
node sns_confirmsubscription.js
*/
// snippet-start:[sns.JavaScript.subscriptions.confirmSubscriptionV3]
// Import required AWS SDK clients and commands for Node.js
import {ConfirmSubscriptionCommand } from "@aws-sdk/client-sns";
import {snsClient } from "./libs/snsClient.js";

// Set the parameters
const params = {
  Token: "TOKEN", // Required. Token sent to the endpoint by an earlier Subscribe action. */
  TopicArn: "TOPIC_ARN", // Required
  AuthenticateOnUnsubscribe: "true", // 'true' or 'false'
};

const run = async () => {
  try {
    const data = await snsClient.send(new ConfirmSubscriptionCommand(params));
    console.log("Success.",  data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[sns.JavaScript.subscriptions.confirmSubscriptionV3]
// For unit tests only.
// module.exports ={run, params};
