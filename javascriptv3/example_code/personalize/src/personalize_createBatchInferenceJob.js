// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createBatchInferenceJob.js demonstrates how to create a batch inference job with Amazon Personalize.
A batch inference job is a tool that imports your batch input data from an Amazon S3 bucket, uses your 
solution version to generate recommendations, and exports the recommendations to an Amazon S3 bucket.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateBatchInferenceJob.html.

Inputs (replace in code):

- JOB_NAME
- INPUT_PATH
- INPUT_KMS_KEY_ARN
- OUTPUT_PATH
- OUTPUT_KMS_KEY_ARN
- ROLE_ARN
- SOLUTION_VERSION_ARN

Running the code:
node createBatchInferenceJob.js
*/

// snippet-start:[personalize.JavaScript.createBatchInferenceJobV3]
// Get service clients module and commands using ES6 syntax.
import { CreateBatchInferenceJobCommand } from "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the batch inference job's parameters.

export const createBatchInferenceJobParam = {
  jobName: "JOB_NAME",
  jobInput: {
    s3DataSource: {
      path: "INPUT_PATH",
    },
  },
  jobOutput: {
    s3DataDestination: {
      path: "OUTPUT_PATH",
    },
  },
  roleArn: "ROLE_ARN",
  solutionVersionArn: "SOLUTION_VERSION_ARN",
  numResults: 20,
};

export const run = async () => {
  try {
    const response = await personalizeClient.send(
      new CreateBatchInferenceJobCommand(createBatchInferenceJobParam),
    );
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[personalize.JavaScript.createBatchInferenceJobV3]
// For unit tests only.
// module.exports ={run, createBatchInferenceJobParam};
