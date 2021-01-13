/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-browser.html.

Purpose:
polly.ts demonstrates how to convert text to speech using Amazon Polly.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- IDENTITY_POOL_ID

Running the code:
Follow the steps in https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-browser.html.
*/
// snippet-start:[Polly.JavaScript.general-examples.synthesizetos3_V3]
const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const {
  Polly,
  StartSpeechSynthesisTaskCommand,
} = require("@aws-sdk/client-polly");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create the parameters
var s3Params = {
  OutputFormat: "mp3",
  OutputS3BucketName: "BUCKET_NAME",
  Text: "Hello David, How are you?",
  TextType: "text",
  VoiceId: "Joanna",
  SampleRate: "22050",
};
// Create the Polly service client, assigning your credentials
const polly = new Polly({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: REGION }),
    identityPoolId: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID
  }),
});

const run = async () => {
  try {
    const data = await polly.send(
      new StartSpeechSynthesisTaskCommand(s3Params)
    );
    console.log("Audio file added to " + s3Params.OutputS3BucketName);
  } catch (err) {
    console.log("Error putting object", err);
  }
};
run();

// snippet-end:[Polly.JavaScript.general-examples.synthesizetos3_V3]
