/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
putUsers.js demonstrates how to incrementally import a user into an Amazon Personalize dataset.
See https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_PutUsers.html

Inputs (replace in code):
- DATASET_ARN
- USER_ID
- PROPERTY1_NAME
- PROPERTY1_VALUE

Running the code:
node putUsers.js
*/

// snippet-start:[personalize.JavaScript.putUsersV3]
// Get service clients module and commands using ES6 syntax.
import { PutUsersCommand } from
  "@aws-sdk/client-personalize-events";
import { personalizeEventsClient } from "./libs/personalizeClients.js";
// or create the client here
// const personalizeEventsClient = new PersonalizeEventsClient({ region: "REGION"});

// set the put users parameters. For string properties and values, use the \ character to escape quotes.
var putUsersParam = {
    datasetArn: "DATASET_ARN",
    users: [ 
      {
        'userId': 'USER_ID',
        'properties': "{\"PROPERTY1_NAME\": \"PROPERTY1_VALUE\"}"   
      }
    ]
};
export const run = async () => {
  try {
    const response = await personalizeEventsClient.send(new PutUsersCommand(putUsersParam));
    console.log("Success!", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.putUsersV3]
// For unit tests only.
// module.exports ={run, putUsersParam};