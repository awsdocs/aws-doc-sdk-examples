/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-authentication.html.

Purpose:
lambda-trigger-pre-authentication.js is a sample function that prevents users from a specific user pool app client
to sign-in to the user pool.

Running the code:
node lambda-trigger-pre-authentication.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.pre-authenticationV3]
exports.handler = async (event, context) => {
    try {
        if (event.callerContext.clientId === "user-pool-app-client-id-to-be-blocked") {
            const error = new Error("Cannot authenticate users from this user pool app client");
        }
    }
    catch(err) {
        // Return to Amazon Cognito
        return err;
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-authenticationV3]
