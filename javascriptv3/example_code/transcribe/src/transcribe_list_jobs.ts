/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_list_jobs.ts demonstrates how to retrieve a list of Amazon Transcribe transcription jobs.

Inputs (replace in code):
- REGION
- JOB_NAME

Running the code:
ts-node transcribe_list_jobs.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.listJobsV3]
const {
  TranscribeClient,
  ListTranscriptionJobsCommand,
} = require("@aws-sdk/client-transcribe");

const client = new TranscribeClient({ region: "REGION" });
const params = {
  JobNameContains: "KEY_WORD" // Returns only transcription job names containing this string
};

const run = async () => {
  try {
    const data = await client.send(new ListTranscriptionJobsCommand(params));
    console.log("Success", data.TranscriptionJobSummaries);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[transcribe.JavaScript.jobs.listJobsV3]
