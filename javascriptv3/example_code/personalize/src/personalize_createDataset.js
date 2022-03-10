/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDataset.js demonstrates how to create a dataset with Amazon Personalize. 
A dataset is a container for data that you upload to Amazon Personalize.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDataset.html.

Inputs (replace in code):
- DATASET_GROUP_ARN
- DATASET_TYPE
- NAME
- SCHEMA_ARN

Running the code:
node createDataset.js
*/

// snippet-start:[personalize.JavaScript.createDatasetV3]
// Get service clients module and commands using ES6 syntax.
import { CreateDatasetCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the dataset parameters
export const createDatasetParam = {
  datasetGroupArn: 'DATASET_GROUP_ARN', /* required */
  datasetType: 'DATASET_TYPE', /* required */
  name: 'NAME', /* required */
  schemaArn: 'SCHEMA_ARN' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateDatasetCommand(createDatasetParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDatasetV3]
// For unit tests only.
// module.exports ={run, createDatasetParam};