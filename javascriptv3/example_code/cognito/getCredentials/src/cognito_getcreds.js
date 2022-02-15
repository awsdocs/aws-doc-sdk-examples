/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-define-auth-challenge.html.

Purpose:
cognito_getcreds.ts generates or retrieves an Amazon Cognito ID.

For more information, see https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/CognitoIdentity.html#getId-property.

Inputs (into command line):
- IDENTITY_POOL_ID: Your Cognito identity pool ID
- AWS_ACCOUNT_ID: Your AWS account ID
- REGION

Running the code:
1. Install webpack by entering 'yarn add webpack' at a command prompt.
2. Run the following command to generate 'main.js' which is used in cognito_getcreds.html.
   webpack cognito_getcreds.js --mode development --target web --devtool false -o main.ts
3. Open cognito_getcreds.html in your browser.
*/
// snippet-start:[cognito.javascript.cognito_getcredsV3]
// Load the required clients and packages
const {
  CognitoIdentityClient,
  GetIdCommand
} = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");

// Set the parameter
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID";
const ACCOUNT_ID = "ACCOUNT_ID";

// Initialize the Amazon Cognito credentials provider
const cognitoidentity = new CognitoIdentityClient({
  region: "REGION",
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: "REGION" }),
    identityPoolId: IDENTITY_POOL_ID
  }),
});

const getIdParams = {
  IdentityPoolId: IDENTITY_POOL_ID,
  AccountId: ACCOUNT_ID
};

const showId = async () => {
  try {
    const data = await cognitoidentity.send(new GetIdCommand(getIdParams));
    const results = "Cognito Identity ID is " + data.IdentityId;
    document.getElementById("results").innerHTML = results;
  } catch (err) {
    const errorResults = "Error" + err;
    document.getElementById("results").innerHTML = results;
  }
};

// Make function available to browser
window.showId = showId;

// snippet-end:[cognito.javascript.cognito_getcredsV3]
module.exports = {showId};  //for unit tests only
