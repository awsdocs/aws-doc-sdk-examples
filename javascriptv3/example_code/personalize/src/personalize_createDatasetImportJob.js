/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDatasetImportJob.js demonstrates how to create a dataset import job to import data into an
Amazon Personalize dataset.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDatasetImportJob.html.

Inputs (replace in code):
- S3_PATH
- NAME
- ROLE_ARN
- DATASET_ARN

Running the code:
node createDatasetImportJob.js
*/

// snippet-start:[personalize.JavaScript.createDatasetImportJobV3]
// Get service clients module and commands using ES6 syntax.
import {CreateDatasetImportJobCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the dataset import job parameters.
export const datasetImportJobParam = {
  datasetArn: 'DATASET_ARN', /* required */
  dataSource: {  /* required */
    dataLocation: 'S3_PATH'
  },
  jobName: 'NAME',/* required */
  roleArn: 'ROLE_ARN' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateDatasetImportJobCommand(datasetImportJobParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDatasetImportJobV3]
// For unit tests only.
// module.exports ={run, createDatasetImportJobParam};