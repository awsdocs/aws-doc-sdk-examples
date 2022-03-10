
/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createBatchInferenceJob.js demonstrates how to create a batch inference job with Amazon Personalize.
A batch inference job is a tool that imports your batch input data from an Amazon S3 bucket, uses your 
solution version to generate recommendations, and exports the recommendations to an Amazon S3 bucket.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateBatchInferenceJob.html.

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
import { CreateBatchInferenceJobCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the batchInferenceJob parameters

export const createBatchInferenceJobParam = {
  jobName: 'JOB_NAME',
  jobInput: {         /* required */
    s3DataSource: {   /* required */
      path: 'INPUT_PATH', /* required */
      // kmsKeyArn: 'INPUT_KMS_KEY_ARN' /* optional */'
    }
  },
  jobOutput: {         /* required */
    s3DataDestination: {   /* required */
      path: 'OUTPUT_PATH', /* required */
      // kmsKeyArn: 'OUTPUT_KMS_KEY_ARN' /* optional */'
    }
  },
  roleArn: 'ROLE_ARN', /* required */
  solutionVersionArn: 'SOLUTION_VERSION_ARN', /* required */
  numResults: 20 /* optional integer*/
};

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateBatchInferenceJobCommand(createBatchInferenceJobParam));
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
