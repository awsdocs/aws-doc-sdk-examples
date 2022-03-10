/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDatasetGroup.js demonstrates how to create a custom dataset group with Amazon Personalize.
A custom dataset group is a dataset group containing custom resources, including solutions, 
solution versions, filters, campaigns, and batch inference jobs. 
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDatasetGroup.html.

Inputs (replace in code):
- NAME

Running the code:
node createDatasetGroup.js
*/

// snippet-start:[personalize.JavaScript.createDatasetGroupV3]
// Get service clients module and commands using ES6 syntax.

import { CreateDatasetGroupCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the dataset group parameters
export const createDatasetGroupParam = { 
  name: 'NAME' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateDatasetGroupCommand(createDatasetGroupParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createDatasetGroupV3]
// For unit tests only.
// module.exports ={run, createDatasetGroupParam};