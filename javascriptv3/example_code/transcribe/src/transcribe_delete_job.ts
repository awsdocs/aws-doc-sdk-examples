/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3.

Purpose:
transcribe_delete_job.ts demonstrates how to delete an Amazon Transcribe transcription job.

Inputs (replace in code):
- REGION
- JOB_NAME
- LANGUAGE_CODE
- SOURCE_FILE_FORMAT
- SOURCE_LOCATION

Running the code:
ts-node transcribe_create_job.ts
 */
// snippet-start:[transcribe.JavaScript.jobs.deleteJobsV3]

const {
  TranscribeClient,
  DeleteTranscriptionJobCommand,
} = require("@aws-sdk/client-transcribe");

const client = new TranscribeClient({ region: "eu-west-1" });
const params = {
  TranscriptionJobName: "JOB_NAME" // For example, 'transciption_demo'
};

const run = async () => {
  try {
    const data = await client.send(new DeleteTranscriptionJobCommand(params));
    console.log("Success - deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[transcribe.JavaScript.jobs.deleteJobsV3]
