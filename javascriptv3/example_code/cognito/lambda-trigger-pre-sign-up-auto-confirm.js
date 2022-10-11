/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.

Purpose:
lambda-trigger-pre-sign-up-auto-confirm.js is a sample Lambda trigger called just before sign up
with the user pool pre-signup Lambda trigger. It uses a custom attribute custom:domain to automatically
confirm new users from a particular email domain. Any new users not in the custom domain will be added
to the user pool, but not automatically confirmed.

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

// snippet-start:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirmV3]
exports.handler = async (event) => {
  // Set the user pool autoConfirmUser flag after validating the email domain
  event.response.autoConfirmUser = false;

  // Split the email address so we can compare domains
  const address = event.request.userAttributes.email.split("@");
  try {
    // This example uses a custom attribute "custom:domain"
    if (event.request.userAttributes.hasOwnProperty("custom:domain")) {
      if (event.request.userAttributes["custom:domain"] === address[1]) {
        event.response.autoConfirmUser = true;
      }
    }
  } catch (err) {
    // Return to Amazon Cognito
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirmV3]
