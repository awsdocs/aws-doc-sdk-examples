/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-sign-up.html.

Purpose:
lambda-trigger-pre-sign-up-auto-confirm-front-end.js demonstrates how to sign up a new
user. It will invoke a pre-signup Lambda trigger as part of the authentication.

Inputs (replace in code):
- EMAIL: Your email.
- PHONE_NUMBER: Your phone number here with +country code and no delimiters in front.

Running the code:
1. On the AWS Lambda service dashboard, click Create function.
2. On the Create function page, name the function, and click Create function.
3. Copy and paste the code into the index.js file in the editor, and save the function.
4. Open the AWS Cognito service.
5. Click Manage User pools.
6. Click the User Pool you want to add the trigger to. (If you don't have a User Pool, create one.)
7. In General Settings, click Triggers.
8. In the Pre sign-up pane, select the lambda function.
*/

// snippet-start:[cognito.javascript.lambda-trigger.presign-up-auto-confirm-front-endV3]
var attributeList = [];
var dataEmail = {
  Name: "email",
  Value: "EMAIL", // Your email here
};
var dataPhoneNumber = {
  Name: "phone_number",
  Value: "PHONE_NUMBER", // Your phone number here with +country code and no delimiters in front
};

var dataEmailDomain = {
  Name: "custom:domain",
  Value: "example.com",
};
var attributeEmail = new AmazonCognitoIdentity.CognitoUserAttribute(dataEmail);
var attributePhoneNumber = new AmazonCognitoIdentity.CognitoUserAttribute(
  dataPhoneNumber
);
var attributeEmailDomain = new AmazonCognitoIdentity.CognitoUserAttribute(
  dataEmailDomain
);

attributeList.push(attributeEmail);
attributeList.push(attributePhoneNumber);
attributeList.push(attributeEmailDomain);

var cognitoUser;
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
