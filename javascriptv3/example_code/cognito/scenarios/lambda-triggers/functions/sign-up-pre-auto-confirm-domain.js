/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.
*/

// snippet-start:[javascript.v3.cognito.scenarios.lambda-triggers.PreSignUpAutoConfirmDomain]
exports.handler = async (event) => {
  const validDomain = "example.com";
  const [_, userDomain] = event.request.userAttributes.email.split("@");

  event.response.autoConfirmUser = userDomain === validDomain;

  return event;
};
// snippet-end:[javascript.v3.cognito.scenarios.lambda-triggers.PreSignUpAutoConfirmDomain]
