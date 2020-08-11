/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-post-authentication.html.

Purpose:
lambda-trigger-post-authentication.js is a post authentication sample Lambda function sends data from a successful
sign-in to CloudWatch Logs.

Running the code:
1. On the AWS Lambda service dashboard, click Create function.
2. On the Create function page, name the function, and click Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the AWS Cognito service.
5. Click Manage User pools.
6. Click the User Pool you want to add the trigger to. (If you don't have a User Pool, create one.)
7. In General Settings, click Triggers.
8. In the Post authentication pane, select the lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.post-authenticationV3]
exports.handler = async (event, context) => {
  try {
    // Send post authentication data to Cloudwatch logs
    console.log("Authentication successful");
    console.log("Trigger function =", event.triggerSource);
    console.log("User pool = ", event.userPoolId);
    console.log("App client ID = ", event.callerContext.clientId);
    console.log("User ID = ", event.userName);
  } catch (err) {
    // Return to Amazon Cognito
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.post-authenticationV3]
