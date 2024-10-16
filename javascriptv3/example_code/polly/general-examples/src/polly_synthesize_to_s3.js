// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/polly-examples.html.

Purpose:
polly.ts demonstrates how to convert text to speech using Amazon Polly,
and automatically upload an audio file of the speech to an
Amazon Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- BUCKET_NAME
- IDENTITY_POOL_ID

Running the code:
node polly_synthesize_to_s3.js
*/
// snippet-start:[Polly.JavaScript.general-examples.synthesizetos3_V3]
import { StartSpeechSynthesisTaskCommand } from "@aws-sdk/client-polly";
import { pollyClient } from "./libs/pollyClient.js";

// Create the parameters
const params = {
  OutputFormat: "mp3",
  OutputS3BucketName: "videoanalyzerbucket",
  Text: "Hello David, How are you?",
  TextType: "text",
  VoiceId: "Joanna",
  SampleRate: "22050",
};

const run = async () => {
  try {
    await pollyClient.send(new StartSpeechSynthesisTaskCommand(params));
    console.log(`Success, audio file added to ${params.OutputS3BucketName}`);
  } catch (err) {
    console.log("Error putting object", err);
  }
};
run();

// snippet-end:[Polly.JavaScript.general-examples.synthesizetos3_V3]
