/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createBatchSegmentJob.js demonstrates how to create a batch segment job with Amazon Personalize.
A batch segment job is a tool that imports your batch input data from an Amazon S3 bucket, uses your solution version 
to create user segments, and exports the user segments to an Amazon S3 bucket.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateBatchSegmentJob.html.

Inputs (replace in code):
- NAME
- INPUT_PATH
- INPUT_KMS_KEY_ARN
- OUTPUT_PATH
- OUTPUT_KMS_KEY_ARN
- ROLE_ARN
- SOLUTION_VERSION_ARN

Running the code:
node createBatchSegmentJob.js
*/

// snippet-start:[personalize.JavaScript.createBatchSegmentJobV3]
// Get service clients module and commands using ES6 syntax.
import { CreateBatchSegmentJobCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the batch segment job's parameters.

export const createBatchSegmentJobParam = {
  jobName: 'NAME',
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
  numResults: 20 /* optional */
};

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateBatchSegmentJobCommand(createBatchSegmentJobParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createBatchSegmentJobV3]
// For unit tests only.
// module.exports ={run, createBatchSegmentJobParam};