/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_delete_medical_job.ts demonstrates how to delete an Amazon Transcribe medical transcription job.

Inputs (replace in code):
- REGION
- MEDICAL_JOB_NAME


Running the code:
ts-node transcribe_delete_medical_job.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.deleteMedicalJobV3]
// Import the required AWS SDK clients and commands for Node.js
const {
  TranscribeClient,
  DeleteMedicalTranscriptionJobCommand,
} = require("@aws-sdk/client-transcribe");

// Set the AWS Region
const REGION = "REGION"; // For example, "us-east-1"

// Set the parameters
const params = {
  MedicalTranscriptionJobName: "MEDICAL_JOB_NAME" // For example, 'medical_transciption_demo'
};

// Create an Amazon Transcribe service client object
const client = new TranscribeClient({ region: REGION });

const run = async () => {
  try {
    const data = await client.send(
      new DeleteMedicalTranscriptionJobCommand(params)
    );
    console.log("Success - deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[transcribe.JavaScript.jobs.deleteMedicalJobV3]
