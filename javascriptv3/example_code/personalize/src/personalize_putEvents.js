/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
putEvents.js demonstrates how to import real-time interaction event data into Amazon Personalize.
For more information, see https://docs.aws.amazon.com/personalize/latest/dg/API_UBS_PutEvents.html.

Inputs (replace in code):
- USER_ID
- ITEM_ID
- EVENT_TYPE
- SESSION_ID
- EVENT_ID
- TRACKING_ID

Running the code:
node putEvents.js
*/

// snippet-start:[personalize.JavaScript.putEventsV3]
// Get service clients module and commands using ES6 syntax.
import { PutEventsCommand } from
  "@aws-sdk/client-personalize-events";
import { personalizeEventsClient } from "./libs/personalizeClients.js";
// Or, create the client here.
// const personalizeEventsClient = new PersonalizeEventsClient({ region: "REGION"});

// Convert your UNIX timestamp to a Date.
const sentAtDate = new Date(1613443801 * 1000)  // 1613443801 is a testing value. Replace it with your sentAt timestamp in UNIX format.

// Set put events parameters.
var putEventsParam = {
  eventList: [          /* required */
    {
      eventType: 'EVENT_TYPE',     /* required */
      sentAt: sentAtDate,           /* required, must be a Date with js */
      eventId: 'EVENT_ID',    /* optional */
      itemId: 'ITEM_ID'         /* optional */
    }
  ],
  sessionId: 'SESSION_ID',      /* required */
  trackingId: 'TRACKING_ID', /* required */
  userId: 'USER_ID'  /* required */
};
export const run = async () => {
  try {
    const response = await personalizeEventsClient.send(new PutEventsCommand(putEventsParam));
    console.log("Success!", response);
    return response; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[personalize.JavaScript.putEventsV3]
// For unit tests only.
// module.exports ={run, putEventsParam};
