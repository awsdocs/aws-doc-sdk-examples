/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_list_medical_jobs.js demonstrates how to retrieve a list of Amazon Transcribe medical transcription jobs.

Inputs (replace in code):
- KEYWORD

Running the code:
node transcribe_list_medical_jobs.js
 */
// snippet-start:[transcribe.JavaScript.jobs.listMedicalJobsV3]
// Import the required AWS SDK clients and commands for Node.js

import { ListMedicalTranscriptionJobsCommand } from "@aws-sdk/client-transcribe";
import { transcribeClient } from "./libs/transcribeClient.js";

// Set the parameters
export const params = {
  JobNameContains: "KEYWORD", // Returns only transcription job names containing this string
};

export const run = async () => {
  try {
    const data = await transcribeClient.send(
      new ListMedicalTranscriptionJobsCommand(params)
    );
    console.log("Success", data.MedicalTranscriptionJobName);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[transcribe.JavaScript.jobs.listMedicalJobsV3]
// For unit tests.
// module.exports = {run, params}
