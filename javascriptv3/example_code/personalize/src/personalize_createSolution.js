/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createSolution.js demonstrates how to create a solution for a custom dataset group
with Amazon Personalize. See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateSolution.html.

Inputs (replace in code):
- DATASET_GROUP_ARN
- NAME
- RECIPE_ARN

Running the code:
node createSolution.js
*/

// snippet-start:[personalize.JavaScript.createSolutionV3]
// Get service clients module and commands using ES6 syntax.
import { CreateSolutionCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the solution parameters
export const createSolutionParam = {
  datasetGroupArn: 'DATASET_GROUP_ARN', /* required */
  recipeArn: 'RECIPE_ARN', /* required */
  name: 'NAME' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateSolutionCommand(createSolutionParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createSolutionV3]
// For unit tests only.
// module.exports ={run, createSolutionParam};