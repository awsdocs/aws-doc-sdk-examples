/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-creating-template.html.

Purpose:
ses_listtemplates.ts demonstrates how to list the available Amazon SES email templates.

Inputs (replace in code):
- REGION
- ITEMS_COUNT

Running the code:
ts-node ses_listreceiptfilters.ts
*/
// snippet-start:[ses.JavaScript.templates.listTemplatesV3]
// Import required AWS SDK clients and commands for Node.js
const { SESClient, ListTemplatesCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const params = { MaxItems: "ITEMS_COUNT" }; //ITEMS_COUNT

// Create SES service object
const ses = new SESClient({ region: REGION });

const run = async () => {
  try {
    const data = await ses.send(new ListTemplatesCommand({ params }));
    console.log(data);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.templates.listTemplatesV3]

