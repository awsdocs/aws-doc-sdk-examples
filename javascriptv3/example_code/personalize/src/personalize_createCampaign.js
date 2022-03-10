/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createCampaign.js demonstrates how to create a campaign with Amazon Personalize. A campaign
is a deployed solution version (trained model) with provisioned dedicated 
transaction capacity for creating real-time recommendations for your application users.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateCampaign.html.

Inputs (replace in code):
- SOLUTION_VERSION_ARN
- NAME

Running the code:
node createCampaign.js
*/

// snippet-start:[personalize.JavaScript.createCampaignV3]
// Get service clients module and commands using ES6 syntax.

import { CreateCampaignCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the campaign parameters
export const createCampaignParam = {
  solutionVersionArn: 'SOLUTION_VERSION_ARN', /* required */
  name: 'NAME',  /* required */
  minProvisionedTPS: 1    /* optional integer */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateCampaignCommand(createCampaignParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createCampaignV3]
// For unit tests only.
// module.exports ={run, createCampaignParam};
