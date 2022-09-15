/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-ddb-setup.html.

Purpose:
index.ts is call from index.html in this example. It contains functions for running the App.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID

Running the code:
ts-node ddb-table-create.ts
*/
// snippet-start:[s3.JavaScript.buckets.indexv3]

// Load the required clients and packages
const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const { LambdaClient, InvokeCommand } = require("@aws-sdk/client-lambda");
// Initialize the Amazon Cognito credentials provider
const REGION = "REGION"; // e.g., 'us-east-2'
const lambda = new LambdaClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID e.g., eu-west-1:xxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx
  }),
});

/* CLIENT UI CODE */
// Application global variables
let isSpinning = false;
const pullHandle = () => {
  if (isSpinning == false) {
    // Show the handle pulled down
    slot_handle.src = "resources/lever-dn.png";
  }
};

const slot_L = document.querySelector("#slot_L");
const slot_M = document.querySelector("#slot_M");
const slot_R = document.querySelector("#slot_R");
const winner_light = document.querySelector("#winner_light");

const initiatePull = () => {
  // Show the handle flipping back up
  slot_handle.src = "resources/lever-up.png";
  // Set all three wheels "spinning"
  slot_L.src = "resources/slotpullanimation.gif";
  slot_M.src = "resources/slotpullanimation.gif";
  slot_R.src = "resources/slotpullanimation.gif";
  winner_light.style.visibility = "hidden";

  // Set app status to spinning
  isSpinning = true;
  // Call the Lambda function to collect the spin results
  lambda.send(
    new InvokeCommand({
      FunctionName: "slotpull",
      InvocationType: "RequestResponse",
      LogType: "None",
    }),
    function (err, data) {
      if (err) {
        prompt(err);
      } else {
        pullResults = JSON.parse(
          //parse Uint8Array payload to string
          new TextDecoder("utf-8").decode(data.Payload)
        );
        displayPull();
      }
    }
  );
};

const displayPull = () => {
  isSpinning = false;
  if (pullResults.isWinner) {
    winner_light.style.visibility = "visible";
  }
  slot_L.src = `resources/${pullResults.leftWheelImage.file.S}`;
  slot_M.src = `resources/${pullResults.middleWheelImage.file.S}`;
  slot_R.src = `resources/${pullResults.rightWheelImage.file.S}`;
};

const slotHandle = document.querySelector("#slot_handle");
slotHandle.onmousedown = pullHandle;
slotHandle.onmouseup = initiatePull;

// snippet-end:[s3.JavaScript.buckets.indexv3]
