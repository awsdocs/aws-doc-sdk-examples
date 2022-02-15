/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-browser.html.

Purpose:
polly.ts demonstrates how to convert text to speech using Amazon Polly.

Inputs (replace in code):
- OUTPUT_FORMAT
- SAMPLE_RATE
- TEXT_TYPE
- POLLY_VOICE
- REGION
- IDENTITY_POOL_ID

Running the code:
Follow the steps in https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-browser.html.
*/
// snippet-start:[Polly.JavaScript.BrowserExample.completeV3]
// snippet-start:[Polly.JavaScript.BrowserExample.configV3]
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import {
    fromCognitoIdentityPool,
} from "@aws-sdk/credential-provider-cognito-identity";
import { Polly } from "@aws-sdk/client-polly";
import { getSynthesizeSpeechUrl } from "@aws-sdk/polly-request-presigner";

// Create the Polly service client, assigning your credentials
const client = new Polly({
    region: "REGION",
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: "REGION" }),
        identityPoolId: "IDENTITY_POOL_ID" // IDENTITY_POOL_ID
    }),
});

// Set the parameters
const speechParams = {
    OutputFormat: "OUTPUT_FORMAT", // For example, 'mp3'
    SampleRate: "SAMPLE_RATE", // For example, '16000
    Text: "", // The 'speakText' function supplies this value
    TextType: "TEXT_TYPE", // For example, "text"
    VoiceId: "POLLY_VOICE" // For example, "Matthew"
};
// snippet-end:[Polly.JavaScript.BrowserExample.configV3]
// snippet-start:[Polly.JavaScript.BrowserExample.synthesizeV3]
const speakText = async () => {
    // Update the Text parameter with the text entered by the user
    speechParams.Text = document.getElementById("textEntry").value;
    try{
        let url = await getSynthesizeSpeechUrl({
            client, params: speechParams
        });
        console.log(url);
        // Load the URL of the voice recording into the browser
        document.getElementById('audioSource').src = url;
        document.getElementById('audioPlayback').load();
        document.getElementById('result').innerHTML = "Speech ready to play.";
    } catch (err) {
        console.log("Error", err);
        document.getElementById('result').innerHTML = err;
    }
};
// Expose the function to the browser
window.speakText = speakText;
// snippet-end:[Polly.JavaScript.BrowserExample.synthesizeV3]
// snippet-end:[Polly.JavaScript.BrowserExample.completeV3]
