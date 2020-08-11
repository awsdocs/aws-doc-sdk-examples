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
1. On the AWS Lambda service dashboard, click Create function.
2. On the Create function page, name the function, and click Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the AWS Cognito service.
5. Click Manage User pools.
6. Click the User Pool you want to add the trigger to. (If you don't have a User Pool, create one.)
7. In General Settings, click Triggers.
8. In the User migration pane, select the lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.migrate-userV3]
exports.handler = async (event, context) => {
  let user;

  if (event.triggerSource == "UserMigration_Authentication") {
    // authenticate the user with your existing user directory service
    try {
      user = authenticateUser(event.userName, event.request.password);
      if (user) {
        event.response.userAttributes = {
          email: user.emailAddress,
          email_verified: "true",
        };
        event.response.finalUserStatus = "CONFIRMED";
        event.response.messageAction = "SUPPRESS";
        context.succeed(event);
      }
    } catch (err) {
      // Return error to Amazon Cognito
      console.log("Bad password", err);
    }
  } else if (event.triggerSource == "UserMigration_ForgotPassword") {
    try {
      // Lookup the user in your existing user directory service
      user = lookupUser(event.userName);
      if (user) {
        event.response.userAttributes = {
          email: user.emailAddress,
          // Required to enable password-reset code to be sent to user
          email_verified: "true",
        };
        event.response.messageAction = "SUPPRESS";
        context.succeed(event);
      }
    } catch (err) {
      // Return error to Amazon Cognito
      console.log("Bad password", err);
    }
  } else {
    // Return error to Amazon Cognito
    callback("Bad triggerSource " + event.triggerSource);
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.migrate-userV3]
