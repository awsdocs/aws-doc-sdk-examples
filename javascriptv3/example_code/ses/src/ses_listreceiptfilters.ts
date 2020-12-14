/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-ip-filters.html.

Purpose:
ses_listreceiptfilters.ts demonstrates how to list the Amazon SES IP filters for an AWS account.

Inputs (replace in code):
- REGION

Running the code:
ts-node ses_listreceiptfilters.ts
*/
// snippet-start:[ses.JavaScript.filters.listReceiptFiltersV3]
// Import required AWS SDK clients and commands for Node.js
const { SESClient, ListReceiptFiltersCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create SES service object
const ses = new SESClient(REGION);

const run = async () => {
  try {
    const data = await ses.send(new ListReceiptFiltersCommand({}));
    console.log(data.Filters);
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.filters.listReceiptFiltersV3]

