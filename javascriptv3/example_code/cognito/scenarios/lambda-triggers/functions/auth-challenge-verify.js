/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-verify-auth-challenge-response.html.

Guidance on implementing this example is in the 'Amplify Docs' at https://docs.amplify.aws/lib/auth/switch-auth/q/platform/js/#customauth-flow.
*/

// snippet-start:[javascript.v3.cognito.scenarios.lambda-triggers.VerifyAuthChallenge]
exports.handler = async (event) => {
  if (
    event.request.privateChallengeParameters.answer ==
    event.request.challengeAnswer
  ) {
    event.response.answerCorrect = true;
  } else {
    event.response.answerCorrect = false;
  }

  return event;
};
// snippet-end:[javascript.v3.cognito.scenarios.lambda-triggers.VerifyAuthChallenge]
