/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_delete_job.ts demonstrates how to delete an Amazon Transcribe transcription job.

Inputs (replace in code):
- JOB_NAME

Running the code:
node transcribe_delete_job.js
 */
// snippet-start:[transcribe.JavaScript.jobs.deleteJobV3]
// Import the required AWS SDK clients and commands for Node.js
import { DeleteTranscriptionJobCommand } from "@aws-sdk/client-transcribe";
import { transcribeClient } from "./libs/transcribeClient.js";

// Set the parameters
export const params = {
  TranscriptionJobName: "JOB_NAME", // Required. For example, 'transciption_demo'
};

export const run = async () => {
  try {
    const data = await transcribeClient.send(
      new DeleteTranscriptionJobCommand(params)
    );
    console.log("Success - deleted");
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[transcribe.JavaScript.jobs.deleteJobV3]
// For unit tests.
// module.exports = {run, params}
