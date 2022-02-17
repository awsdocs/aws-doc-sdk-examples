/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_listtemplates.js demonstrates how to list the available Amazon SES email templates.

Inputs (replace in code):
- ITEMS_COUNT

Running the code:
node ses_listreceiptfilters.js
*/
// snippet-start:[ses.JavaScript.templates.listTemplatesV3]
// Import required AWS SDK clients and commands for Node.js
import { SESClient, ListTemplatesCommand }  from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";

// Set the parameters
const params = { MaxItems: "ITEMS_COUNT" }; //ITEMS_COUNT

const run = async () => {
  try {
    const data = await sesClient.send(new ListTemplatesCommand({ params }));
    console.log("Success.", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.listTemplatesV3]
// For unit tests only.
// module.exports ={run, params};
