/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-ip-filters.html.

Purpose:
ses_createreceiptfilter.js demonstrates how to create an Amazon SES IP address filter.

Inputs (replace in code):
- IP_ADDRESS_OR_RANGE
- POLICY
- NAME

Running the code:
node ses_createreceiptfilter.js
 */

// snippet-start:[ses.JavaScript.filters.createReceiptFilterV3]
// Import required AWS SDK clients and commands for Node.js
import { CreateReceiptFilterCommand } from "@aws-sdk/client-ses";
import { sesClient } from "./libs/sesClient.js";
// Set the parameters
const params = {
  Filter: {
    IpFilter: {
      Cidr: "IP_ADDRESS_OR_RANGE", // (in code; either a single IP address (10.0.0.1) or an IP address range in CIDR notation (10.0.0.1/24)),
      Policy: "POLICY", // 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions.
    },
    Name: "NAME" // NAME (the filter name)
  },
};

const run = async () => {
  try {
    const data = await sesClient.send(new CreateReceiptFilterCommand(params));
    console.log("Success", data);
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err.stack);
  }
};
run();
// snippet-end:[ses.JavaScript.filters.createReceiptFilterV3]
// For unit tests only.
// module.exports = { run, params };
