/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.

Purpose:
lambda-trigger-pre-sign-up-auto-confirm-front-end.js demonstrates how to sign up a new
user. It will invoke a pre-signup Lambda trigger as part of the authentication.

Inputs (replace in code):
- EMAIL: Your email.
- PHONE_NUMBER: Your phone number here with +country code and no delimiters in front.

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

// snippet-start:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-front-endV3]
const attributeList = [];
const dataEmail = {
  Name: "email",
  Value: "EMAIL", // Your email here
};
const dataPhoneNumber = {
  Name: "phone_number",
  Value: "PHONE_NUMBER", // Your phone number here with +country code and no delimiters in front
};

const dataEmailDomain = {
  Name: "custom:domain",
  Value: "example.com",
};
const attributeEmail = new AmazonCognitoIdentity.CognitoUserAttribute(dataEmail);
const attributePhoneNumber = new AmazonCognitoIdentity.CognitoUserAttribute(
  dataPhoneNumber
);
const attributeEmailDomain = new AmazonCognitoIdentity.CognitoUserAttribute(
  dataEmailDomain
);

attributeList.push(attributeEmail);
attributeList.push(attributePhoneNumber);
attributeList.push(attributeEmailDomain);

const cognitoUser;
userPool.signUp("username", "password", attributeList, null, function (
  err,
  result
) {
  if (err) {
    alert(err);
    return;
  }
  cognitoUser = result.user;
  console.log("user name is " + cognitoUser.getUsername());
});
// snippet-end:[cognito.javascript.lambda-trigger.pre-sign-up-auto-confirm-front-endV3]
