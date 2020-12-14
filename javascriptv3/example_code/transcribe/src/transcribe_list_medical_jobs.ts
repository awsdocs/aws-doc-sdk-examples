/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_list_medical_jobs.ts demonstrates how to retrieve a list of Amazon Transcribe medical transcription jobs.

Inputs (replace in code):
- REGION
- KEYWORD

Running the code:
ts-node transcribe_list_medical_jobs.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.listMedicalJobsV3]
// Import the required AWS SDK clients and commands for Node.js

const {
  TranscribeClient,
  ListMedicalTranscriptionJobsCommand,
} = require("@aws-sdk/client-transcribe");

// Set the AWS Region
const REGION = "REGION"; // For example, "us-east-1"

// Set the parameters
const params = {
  JobNameContains: "KEYWORD", // Returns only transcription job names containing this string
};

// Create an Amazon Transcribe service client object
const client = new TranscribeClient({ region: REGION });

const run = async () => {
  try {
    const data = await client.send(
      new ListMedicalTranscriptionJobsCommand(params)
    );
    console.log("Success", data.MedicalTranscriptionJobName);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[transcribe.JavaScript.jobs.listMedicalJobsV3]
