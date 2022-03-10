/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
getRecommendationsWithFilter.js demonstrates how to get filtered recommendations from a campaign
from a custom dataset group.

Inputs (replace in code):
- CAMPAIGN_ARN (replace with a recommender ARN to filter recommendations from a recommender)
- USER_ID
- FILTER_ARN
- PROPERTY and VALUE filter values

Running the code:
node getRecommendationsWithFilter.js
*/

// snippet-start:[personalize.JavaScript.getRecommendationsWithFilterV3]
// Get service clients module and commands using ES6 syntax.
import { GetRecommendationsCommand } from
  "@aws-sdk/client-personalize-runtime";
import { personalizeRuntimeClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeRuntimeClient = new PersonalizeRuntimeClient({ region: "REGION"});

// set recommendation request param
export const getRecommendationsParam = {
  campaignArn: 'CAMPAIGN_ARN', /* required */
  userId: 'USER_ID',      /* required */
  numResults: 15,    /* optional */
  filterArn: 'FILTER_ARN',   /* required to filter recommendations */
  filterValues: {
    "PROPERTY": "\"VALUE\""  /* provide if your filter has a placeholder parameter */
  }
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
// snippet-end:[personalize.JavaScript.getRecommendationsWithFilterV3]
// For unit tests only.
// module.exports ={run, getRecommendationsParam};