/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-define-auth-challenge.html.

Inputs (into command line):
- IDENTITY_POOL_ID: Your Cognito identity pool ID
- AWS_ACCOUNT_ID: Your AWS account ID
- REGION

Purpose:
cognito_getcreds.js generates or retrieves a Cognito ID.

For more information, see https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/CognitoIdentity.html#getId-property

Running the code:
1. Install Webpack by entering 'yarn add webpack' in the command line.
2. Run the following command to generate 'main.js' which is used in cognito_getcreds.html:
   webpack cognito_getcreds.js --mode development --target web --devtool false -o main.js
3. Open cognito_getcreds.html in your browser.
*/
// snippet-start:[cognito.javascript.cognito_getcredsV3]
// Load the required clients and packages
const {
    CognitoIdentityClient,
    GetIdCommand,
} = require("@aws-sdk/client-cognito-identity");
const {
    fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");

// Set your needed values
var IDENTITY_POOL_ID = "IDENTITY_POOL_ID";
var ACCOUNT_ID = "ACCOUNT_ID";

// Initialize the Amazon Cognito credentials provider
const cognitoidentity = new CognitoIdentityClient({
    region: "REGION",
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: "REGION" }),
        identityPoolId: IDENTITY_POOL_ID,
    }),
});

var getIdParams = {
    IdentityPoolId: IDENTITY_POOL_ID,
    AccountId: ACCOUNT_ID,
};

const showId = async () => {
    try {
        const data = await cognitoidentity.send(new GetIdCommand(getIdParams));
        var results = "Cognito Identity ID is " + data.IdentityId;
        document.getElementById("results").innerHTML = results;
    } catch (err) {
        var results = "Error" + err;
        document.getElementById("results").innerHTML = results;
    }
};

//Make function available to browser
window.showId = showId;

// snippet-start:[cognito.javascript.cognito_getcredsV3]

//for unit test
exports.showId = showId;
