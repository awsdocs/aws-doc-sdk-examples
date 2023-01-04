/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_delete_medical_job.test.js demonstrates how to delete an Amazon Transcribe medical transcription job.

Inputs (replace in code):

- MEDICAL_JOB_NAME


Running the code:
node transcribe_delete_medical_job.js
 */
// snippet-start:[transcribe.JavaScript.jobs.deleteMedicalJobV3]
// Import the required AWS SDK clients and commands for Node.js
import { DeleteMedicalTranscriptionJobCommand } from "@aws-sdk/client-transcribe";
import { transcribeClient } from "./libs/transcribeClient.js";

// Set the parameters
export const params = {
  MedicalTranscriptionJobName: "MEDICAL_JOB_NAME", // For example, 'medical_transciption_demo'
};

export const run = async () => {
  try {
    const data = await transcribeClient.send(
      new DeleteMedicalTranscriptionJobCommand(params)
    );
    console.log("Success - deleted");
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[transcribe.JavaScript.jobs.deleteMedicalJobV3]
// For unit tests.
// module.exports = {run, params}
