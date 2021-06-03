/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
allClients.js is a helper function that creates the Amazon Transcribe,
Amazon Translate, Amazon Comprehend, and Amazon Simple Email Service (SES) service clients.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID - an Amazon Cognito Identity Pool ID.
*/
// snippet-start:[allcients.JavaScript.streaming.createclientv3]
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import {
    fromCognitoIdentityPool,
} from "@aws-sdk/credential-provider-cognito-identity";
import {
    ComprehendClient
} from "@aws-sdk/client-comprehend";
import {SESClient} from "@aws-sdk/client-ses";
import {TranscribeStreamingClient} from "@aws-sdk/client-transcribe-streaming";
import {TranslateClient} from "@aws-sdk/client-translate";

const REGION = "REGION";
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; // An Amazon Cognito Identity Pool ID.

// Create an Amazon Transcribe service client object.
const transcribeClient = new TranscribeStreamingClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID
    }),
});

// Create SES service object.
const sesClient = new SESClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID
    }),
});

// Create an Amazon Comprehend service client object.
const comprehendClient = new ComprehendClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID
    }),
});

// Create an Amazon Transcribe service client object.
const translateClient = new TranslateClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID
    }),
});

export { transcribeClient, sesClient, comprehendClient, translateClient };
// snippet-end:[allcients.JavaScript.streaming.createclientv3]
