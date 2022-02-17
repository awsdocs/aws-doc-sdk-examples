/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.

Purpose:
lambda-trigger-pre-sign-up-auto-confirm-and-verify.js confirms all users and sets the user's email
and phone_number attributes to verified if the attribute is present. Also, if aliasing is enabled,
aliases will be created for phone_number and email when auto-verify is set.

Running the code:
1. On the AWS Lambda service dashboard, choose Create function.
2. On the Create function page, name the function, and choose Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the Amazon Cognito service.
5. Choose Manage user pools.
6. Choose the user pool you want to add the trigger to. (If you don't have a user pool, create one.)
7. In General Settings, choose Triggers.
8. In the Pre sign-up pane, select the Lambda function.
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
  } catch (err) {
    // Return to Amazon Cognito
    return err;
  }
};

// snippet-end:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-and-verifyV3]
