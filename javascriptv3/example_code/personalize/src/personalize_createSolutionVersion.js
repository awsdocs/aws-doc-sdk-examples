/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createSolutionVersion.js demonstrates how to create a solution version (trained model) for 
a custom dataset group with Amazon Personalize. See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateSolutionVersion.html

Inputs (replace in code):
- SOLUTION_ARN

Running the code:
node createSolutionVersion.js
*/

// snippet-start:[personalize.JavaScript.createSolutionVersionV3]
// Get service clients module and commands using ES6 syntax.
import { CreateSolutionVersionCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the solution version parameters
export const solutionVersionParam = {
  solutionArn: 'SOLUTION_ARN' /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateSolutionVersionCommand(solutionVersionParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createSolutionVersionV3]
// For unit tests only.
// module.exports ={run, solutionVersionParam};