/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
createFilter.js demonstrates how to create a recommendation filter with Amazon Personalize.
See https://docs.aws.amazon.com/personalize/latest/dg/API_CreateFilter.html.

Inputs (replace in code):
- NAME
- DATASET_GROUP_ARN
- FILTER_EXPRESSION (example: INCLUDE ItemID WHERE Interactions.EVENT_TYPE IN ("click"))

Running the code:
node createFilter.js
*/

// snippet-start:[personalize.JavaScript.createFilterV3]
// Get service clients module and commands using ES6 syntax.
import { CreateFilterCommand } from
  "@aws-sdk/client-personalize";
import { personalizeClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeClient = new PersonalizeClient({ region: "REGION"});

// set the filter param
export const createFilterParam = {
  datasetGroupArn: 'DATASET_GROUP_ARN', /* required */
  name: 'NAME', /* required */
  filterExpression: 'FILTER_EXPRESSION' /*required */
}

export const run = async () => {
  try {
    const response = await personalizeClient.send(new CreateFilterCommand(createFilterParam));
    console.log("Success", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.createFilterV3]
// For unit tests only.
// module.exports ={run, createFilterParam};