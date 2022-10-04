/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

Use this script to verify the custom authentication flow Lambda triggers.

Several Lambda triggers must be turned on and set up in Amazon Cognito
for this example to run successfully. Refer to the triggers listed below:
Trigger                              | Handler
Create auth challenge Lambda trigger | auth-challenge-create
Define auth challenge Lambda trigger | auth-challenge-define
Verify auth challenge Lambda trigger | auth-challenge-verify
Pre sign-up Lambda trigger           | sign-up-pre-auto-confirm-verify
*/
import { Auth } from "aws-amplify";

Auth.configure({
  authenticationFlowType: "CUSTOM_AUTH",
  userPoolId: "<USER_POOL_ID>",
  userPoolWebClientId: "<USER_POOL_CLIENT_ID>",
});

const run = async () => {
  const userName = "<USER_NAME>";
  const password = "<PASSWORD>";
  const email = "<EMAIL>";

  const signUpResult = await Auth.signUp({
    userName,
    password,
    attributes: { email },
  });

  console.log(signUpResult);

  let cognitoUser = await Auth.signIn(userName, password);
  console.log(cognitoUser);

  cognitoUser = await Auth.sendCustomChallengeAnswer(cognitoUser, "5");
  console.log(cognitoUser);

  cognitoUser = await Auth.sendCustomChallengeAnswer(cognitoUser, "Peccy");
  console.log(cognitoUser);
};

run();
