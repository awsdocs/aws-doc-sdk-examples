/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createEventTracker.js demonstrates how to create an event tracker with Amazon Personalize. You 
use an event tracker when you record events with the PutEvents API operation.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_CreateEventTracker.html.

Inputs (replace in code):
- NAME
- DATASET_GROUP_ARN

Running the code:
node createEventTracker.js
*/

// snippet-start:[personalize.JavaScript.createEventTrackerV3]
// Get service clients module and commands using ES6 syntax.
import { CreateEventTrackerCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";

// Or, create the client here.
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// Set the event tracker's parameters.
export const createEventTrackerParam = {
  datasetGroupArn: 'DATASET_GROUP_ARN', /* required */
  name: 'NAME', /* required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateEventTrackerCommand(createEventTrackerParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createEventTrackerV3]
// For unit tests only.
// module.exports ={run, createEventTrackerParam};