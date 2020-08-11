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
node lambda-trigger-post-authentication.js
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
    }
    catch(err) {
        // Return to Amazon Cognito
        return null;
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.post-authenticationV3]
