/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-define-auth-challenge.html.

Purpose:
lambda-trigger-auth-challenge-define.js defines a series of challenges for authentication, and
issues tokens only if all of the challenges are successfully completed.

Running the code:
1. On the AWS Lambda service dashboard, choose Create function.
2. On the Create function page, name the function, and choose Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the Amazon Cognito service.
5. Choose Manage user pools.
6. Choose the user pool you want to add the trigger to. (If you don't have a user pool, create one.)
7. In General Settings, choose Triggers.
8. In the Define Auth Challenge pane, choose the Lambda function.
*/
// snippet-start:[cognito.javascript.lambda-trigger.define-auth-challengeV3]
exports.handler = async (event) => {
  try {
    if (
      event.request.session.length == 1 &&
      event.request.session[0].challengeName == "SRP_A"
    ) {
      event.response.issueTokens = false;
      event.response.failAuthentication = false;
      event.response.challengeName = "PASSWORD_VERIFIER";
    } else if (
      event.request.session.length == 2 &&
      event.request.session[1].challengeName == "PASSWORD_VERIFIER" &&
      event.request.session[1].challengeResult == true
    ) {
      event.response.issueTokens = false;
      event.response.failAuthentication = false;
      event.response.challengeName = "CUSTOM_CHALLENGE";
    } else if (
      event.request.session.length == 3 &&
      event.request.session[2].challengeName == "CUSTOM_CHALLENGE" &&
      event.request.session[2].challengeResult == true
    ) {
      event.response.issueTokens = true;
      event.response.failAuthentication = false;
    } else {
      event.response.issueTokens = false;
      event.response.failAuthentication = true;
    }
  } catch (err) {
    // Return to Amazon Cognito
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.define-auth-challengeV3]
