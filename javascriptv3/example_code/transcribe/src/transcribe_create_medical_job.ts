/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_create_medical_job.ts demonstrates how to create an Amazon Transcribe medical transcription job.

Inputs (replace in code):
- REGION
- JOB_NAME
- LANGUAGE_CODE
- SOURCE_FILE_FORMAT
- SOURCE_LOCATION

Running the code:
ts-node transcribe_create_medical_job.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.createJobsV3]
// Import the required AWS SDK clients and commands for Node.js
const {
  TranscribeClient,
  StartMedicalTranscriptionJobCommand,
} = require("@aws-sdk/client-transcribe");

// Set the AWS Region
const REGION = "REGION"; // For example, "us-east-1"

// Set the parameters
const params = {
  TranscriptionJobName: "JOB_NAME",
  LanguageCode: "LANGUAGE_CODE", // For example, 'en-US'
  MediaFormat: "SOURCE_FILE_FORMAT", // For example, 'wav'
  Media: {
    MediaFileUri: "SOURCE_LOCATION",
    // For example, "https://transcribe-demo.s3-REGION.amazonaws.com/hello_world.wav"
  },
};

// Create an Amazon Transcribe service client object
const client = new TranscribeClient({ region: REGION });

const run = async () => {
  try {
    const data = await client.send(
      new StartMedicalTranscriptionJobCommand(params)
    );
    console.log("Success - put", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[transcribe.JavaScript.jobs.createJobsV3]
