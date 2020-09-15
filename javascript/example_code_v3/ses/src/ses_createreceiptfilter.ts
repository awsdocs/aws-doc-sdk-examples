/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-ip-filters.html.

Purpose:
ses_createreceiptfilter.ts demonstrates how to create an Amazon SES IP address filter.

Inputs (replace in code):
- REGION
- IP_ADDRESS_OR_RANGE
- POLICY
- NAME

Running the code:
ts-node ses_createreceiptfilter.ts
 */

// snippet-start:[ses.JavaScript.filters.createReceiptFilterV3]

// Import required AWS SDK clients and commands for Node.js
const { SES, CreateReceiptFilterCommand } = require("@aws-sdk/client-ses");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {
  Filter: {
    IpFilter: {
      Cidr: "IP_ADDRESS_OR_RANGE", // (in code; either a single IP address (10.0.0.1) or an IP address range in CIDR notation (10.0.0.1/24)),
      Policy: "POLICY", // 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions.
    },
    Name: "NAME", // NAME (the filter name)
  },
};

// Create SES service object
const ses = new SES(REGION);

const run = async () => {
  try {
    const data = await ses.send(new CreateReceiptFilterCommand(params));
    console.log(
      "Success, IP Address Filter created; requestId:",
      data.$metadata.requestId
    );
  } catch (err) {
    console.error(err, err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.filters.createReceiptFilterV3]
export = {run}; //for unit tests only
