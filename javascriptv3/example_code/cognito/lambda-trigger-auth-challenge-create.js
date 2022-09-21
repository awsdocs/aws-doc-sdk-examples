/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-create-auth-challenge.html.

Purpose:
In lambda-trigger-auth-challenge-create.js a CAPTCHA is created as a challenge to the user.
The URL for the CAPTCHA image is added to the public challenge parameters as "captchaUrl",
and the expected answer is added to the private challenge parameters.

Running the code:
1. On the AWS Lambda service dashboard, choose Create function.
2. On the Create function page, name the function, and choose Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the Amazon Cognito service.
5. Choose Manage User pools.
6. Choose the User Pool you want to add the trigger to. (If you don't have a user pool, create one.)
7. In General Settings, click Triggers.
8. In the Create Auth Challenge pane, select the Lambda function.

*/

// snippet-start:[cognito.javascript.lambda-trigger.create-auth-challengeV3]
exports.handler = async (event) => {
  try {
    if (event.request.challengeName == "CUSTOM_CHALLENGE") {
      event.response.publicChallengeParameters = {};
      event.response.publicChallengeParameters.captchaUrl = "url/123.jpg";
      event.response.privateChallengeParameters = {};
      event.response.privateChallengeParameters.answer = "5";
      event.response.challengeMetadata = "CAPTCHA_CHALLENGE";
    }
  } catch (err) {
    return null;
  }
};
// snippet-end:[cognito.javascript.lambda-trigger.create-auth-challengeV3]
