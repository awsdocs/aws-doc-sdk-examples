// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.create-auth-challenge]
exports.handler = (event, context, callback) => {
  if (event.request.challengeName == "CUSTOM_CHALLENGE") {
    event.response.publicChallengeParameters = {};
    event.response.publicChallengeParameters.captchaUrl = "url/123.jpg";
    event.response.privateChallengeParameters = {};
    event.response.privateChallengeParameters.answer = "5";
    event.response.challengeMetadata = "CAPTCHA_CHALLENGE";
  }

  //Return to Amazon Cognito.
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.create-auth-challenge]
