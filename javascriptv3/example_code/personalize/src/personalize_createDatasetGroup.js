/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createDatasetGroup.js demonstrates how to create a custom dataset group with Amazon Personalize.
A custom dataset group contains custom resources such as solutions,
solution versions, filters, campaigns, and batch inference jobs.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateDatasetGroup.html.

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

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the dataset group parameters.
export const createDatasetGroupParam = { 
  name: 'NAME' /* required */
}

export const run = async (createDatasetGroupParam) => {
  try {
    const response = await personalizeClient.send(new CreateDatasetGroupCommand(createDatasetGroupParam));
    console.log("Success", response);
    return "Run successfully"; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run(createDatasetGroupParam);
// snippet-end:[personalize.JavaScript.createDatasetGroupV3]
// For unit tests only.
// module.exports ={run, createDatasetGroupParam};