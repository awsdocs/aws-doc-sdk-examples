/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDatasetExportJob.js demonstrates how to create a dataset export job to export data from
Amazon Personalize to an Amazon S3 bucket.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDatasetExportJob.html.

Inputs (replace in code):
- DATASET_ARN
- S3_DESTINATION_PATH
- NAME
- ROLE_ARN
- INGESTION_MODE

Running the code:
node createDatasetExportJob.js
*/

// snippet-start:[personalize.JavaScript.createDatasetExportJobV3]
// Get service clients module and commands using ES6 syntax.
import { CreateDatasetExportJobCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the export job parameters.
export const datasetExportJobParam = {
  datasetArn: 'DATASET_ARN', /* required */
  jobOutput: {
    s3DataDestination: {
        path: 'S3_DESTINATION_PATH' /* required */
        //kmsKeyArn: 'ARN'  /* include if your bucket uses AWS KMS for encryption
    } 
  },
  jobName: 'NAME',/* required */
  roleArn: 'ROLE_ARN' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateDatasetExportJobCommand(datasetExportJobParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDatasetExportJobV3]
// For unit tests only.
// module.exports ={run, createDatasetExportJobParam};