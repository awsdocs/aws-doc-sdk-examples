/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-migrate-user.html.

Purpose:
lambda-trigger-migrate-user.js migrates the user with an existing password and suppresses the
welcome message from Amazon Cognito.

Running the code:
node lambda-trigger-migrate-user.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.migrate-userV3]
exports.handler = async (event, context) => {

    let user;

    if ( event.triggerSource == "UserMigration_Authentication" ) {
        // authenticate the user with your existing user directory service
        try {
            user = authenticateUser(event.userName, event.request.password);
            if (user) {
                event.response.userAttributes = {
                    "email": user.emailAddress,
                    "email_verified": "true"
                };
                event.response.finalUserStatus = "CONFIRMED";
                event.response.messageAction = "SUPPRESS";
                context.succeed(event);
            }
        }
        catch(err) {
            // Return error to Amazon Cognito
            console.log("Bad password", err);
        }
    }
    else if ( event.triggerSource == "UserMigration_ForgotPassword" ) {
        try {
            // Lookup the user in your existing user directory service
            user = lookupUser(event.userName);
            if (user) {
                event.response.userAttributes = {
                    "email": user.emailAddress,
                    // required to enable password-reset code to be sent to user
                    "email_verified": "true"
                };
                event.response.messageAction = "SUPPRESS";
                context.succeed(event);
            }
        }
        catch(err){
                // Return error to Amazon Cognito
                console.log("Bad password", err);
            }
        }
    else {
        // Return error to Amazon Cognito
        callback("Bad triggerSource " + event.triggerSource);
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.migrate-userV3]
