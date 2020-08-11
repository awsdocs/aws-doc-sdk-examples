/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-custom-message.html..

Purpose:
lambda-trigger-custom-message-admin-create-user.js is a custom message Lambda function with the
CustomMessage_AdminCreateUser trigger returns a user name and verification code and so include
both request.usernameParameter and request.codeParameter in the message body.

Running the code:
node lambda-trigger-custom-message-admin-create-user.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.custom-message-admin-create-userV3]
exports.handler = async (event, context) => {
    try{
        if(event.userPoolId === "theSpecialUserPool") {
            // Identify why was this function invoked
            if(event.triggerSource === "CustomMessage_AdminCreateUser") {
                // Ensure that your message contains event.request.codeParameter event.request.usernameParameter. This is the placeholder for the code and username that will be sent to your user.
                event.response.smsMessage = "Welcome to the service. Your user name is " + event.request.usernameParameter + " Your temporary password is " + event.request.codeParameter;
                event.response.emailSubject = "Welcome to the service";
                event.response.emailMessage = "Welcome to the service. Your user name is " + event.request.usernameParameter + " Your temporary password is " + event.request.codeParameter;
            }
            // Create custom message for other events

            // Customize messages for other user pools
        }
    }

        // Return to Amazon Cognito
    catch(err){
        return null;
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.custom-message-admin-create-userV3]
