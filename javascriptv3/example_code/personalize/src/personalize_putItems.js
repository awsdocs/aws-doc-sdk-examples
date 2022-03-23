/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
putItems.js demonstrates how to incrementally import an item into an Amazon Personalize dataset.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_PutItems.html.

Inputs (replace in code):
- DATASET_ARN
- ITEM_ID
- PROPERTY1_NAME
- PROPERTY1_VALUE
- PROPERTY2_NAME
- PROPERTY2_VALUE
- PROPERTY3_NAME
- PROPERTY3_VALUE

Running the code:
node putItems.js
*/

// snippet-start:[personalize.JavaScript.putItemsV3]
// Get service clients module and commands using ES6 syntax.
import { PutItemsCommand } from
  "@aws-sdk/client-personalize-events";
import { personalizeEventsClient } from "./libs/personalizeClients.js";
// Or, create the client here.
// const personalizeEventsClient = new PersonalizeEventsClient({ region: "REGION"});

// Set the put items parameters. For string properties and values, use the \ character to escape quotes.
var putItemsParam = {
    datasetArn: 'DATASET_ARN', /* required */
    items: [    /* required */
      {
        'itemId': 'ITEM_ID',  /*  required */
        'properties': "{\"PROPERTY1_NAME\": \"PROPERTY1_VALUE\", \"PROPERTY2_NAME\": \"PROPERTY2_VALUE\", \"PROPERTY3_NAME\": \"PROPERTY3_VALUE\"}"   /* optional */
      }
    ]
};
export const run = async () => {
  try {
    const response = await personalizeEventsClient.send(new PutItemsCommand(putItemsParam));
    console.log("Success!", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.putItemsV3]
// For unit tests only.
// module.exports ={run, putItemsParam};