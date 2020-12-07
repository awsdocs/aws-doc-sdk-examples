/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_create_medical_job.ts demonstrates how to create an Amazon Transcribe medical transcription job.

Inputs (replace in code):
- REGION
- MEDICAL_JOB_NAME
- OUTPUT_BUCKET_NAME
- JOB_TYPE
- LANGUAGE_CODE
- SOURCE_FILE_FORMAT
- SOURCE_FILE_LOCATION

Running the code:
ts-node transcribe_create_medical_job.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.createMedicalJobV3]
// Import the required AWS SDK clients and commands for Node.js
const {
  TranscribeClient,
  StartMedicalTranscriptionJobCommand,
} = require("@aws-sdk/client-transcribe");

// Set the AWS Region
const REGION = "REGION"; // For example, "us-east-1"

// Set the parameters
const params = {
  MedicalTranscriptionJobName: 'MEDICAL_JOB_NAME', // Required
  OutputBucketName: 'OUTPUT_BUCKET_NAME', // Required
  Specialty: "PRIMARYCARE", // Required. Possible values are 'PRIMARYCARE'
  Type: "JOB_TYPE", // Required. Possible values are 'CONVERSATION' and 'DICTATION'
  LanguageCode: "LANGUAGE_CODE", // For example, 'en-US'
  MediaFormat: "SOURCE_FILE_FORMAT", // For example, 'wav'
  Media: {
    MediaFileUri: "SOURCE_FILE_LOCATION",
    // The S3 object location of the input media file. The URI must be in the same region
    // as the API endpoint that you are calling.For example,
    // "https://transcribe-demo.s3-REGION.amazonaws.com/hello_world.wav"
  }
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
// snippet-end:[transcribe.JavaScript.jobs.createMedicalJobV3]
