/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-verify-auth-challenge-response.html.

Purpose:
lambda-trigger-auth-challenge-verify.js is an example AWS Lambda function that checks whether the user's response
to a challenge matches the expected response. The answerCorrect parameter is set to true if the
user's response matches the expected response.

Running the code:
1. On the AWS Lambda service dashboard, choose Create function.
2. On the Create function page, name the function, and choose Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the Amazon Cognito service.
5. Choose Manage user pools.
6. Choose the user pool you want to add the trigger to. (If you don't have a user pool, create one.)
7. In General Settings, choose Triggers.
8. In the Verify Auth Challenge Response pane, select the Lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.auth-challenge-verifyV3]
exports.handler = async (event) => {
  try {
    if (
      event.request.privateChallengeParameters.answer ==
      event.request.challengeAnswer
    ) {
      event.response.answerCorrect = true;
    } else {
      event.response.answerCorrect = false;
    }
  } catch (err) {
    // Return to Amazon Cognito
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.auth-challenge-verifyV3]
