/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createRecommender.js demonstrates how to create a recommender
for a domain dataset group with Amazon Personalize.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateRecommender.html.

Inputs (replace in code):
- NAME
- RECIPE_ARN (the ARN for your use case)
- DATASET_GROUP_ARN

Running the code:
node createRecommender.js
*/

// snippet-start:[personalize.JavaScript.createRecommenderV3]
// Get service clients module and commands using ES6 syntax.
import { CreateRecommenderCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the recommender's parameters.
export const createRecommenderParam = {
  name: 'NAME', /* required */
  recipeArn: 'RECIPE_ARN', /* required */
  datasetGroupArn: 'DATASET_GROUP_ARN'  /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateRecommenderCommand(createRecommenderParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createRecommenderV3]
// For unit tests only.
// module.exports ={run, createRecommenderParam};