/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
getRecommendations.js demonstrates how to get recommendations from a campaign created in a 
custom dataset group with Amazon Personalize. See https://docs.aws.amazon.com/personalize/latest/dg/API_RS_GetRecommendations.html.

Inputs (replace in code):
- CAMPAIGN_ARN
- USER_ID

Running the code:
node getRecommendations.js
*/

// snippet-start:[personalize.JavaScript.getRecommendationsV3]
// Get service clients module and commands using ES6 syntax.
import { GetRecommendationsCommand } from
  "@aws-sdk/client-personalize-runtime";

import { personalizeRuntimeClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeRuntimeClient = new PersonalizeRuntimeClient({ region: "REGION"});

// set the recommendation request parameters
export const getRecommendationsParam = {
  campaignArn: 'CAMPAIGN_ARN', /* required */
  userId: 'USER_ID',      /* required */
  numResults: 15    /* optional */
}

export const run = async () => {
  try {
    const response = await personalizeRuntimeClient.send(new GetRecommendationsCommand(getRecommendationsParam));
    console.log("Success!", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[personalize.JavaScript.getRecommendationsV3]
// For unit tests only.
// module.exports ={run, getRecommendationsParam};