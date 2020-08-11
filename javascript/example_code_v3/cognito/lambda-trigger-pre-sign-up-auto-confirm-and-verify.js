/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.

Purpose:
lambda-trigger-pre-sign-up-auto-confirm-and-verify.js confirms all users and sets the user's email
and phone_number attributes to verified if the attribute is present. Also, if aliasing is enabled,
aliases will be created for phone_number and email when auto-verify is set.

Running the code:
node lambda-trigger-pre-sign-up-auto-confirm-and-verify.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-and-verifyV3]
exports.handler = async (event, context) => {
    // Confirm the user
        event.response.autoConfirmUser = true;
    try {
        // Set the email as verified if it is in the request
        if (event.request.userAttributes.hasOwnProperty("email")) {
            event.response.autoVerifyEmail = true;
        }

        // Set the phone number as verified if it is in the request
        if (event.request.userAttributes.hasOwnProperty("phone_number")) {
            event.response.autoVerifyPhone = true;
        }
    }
    catch(err){
        // Return to Amazon Cognito
        return err;
    }
};

// snippet-end:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-and-verifyV3]
