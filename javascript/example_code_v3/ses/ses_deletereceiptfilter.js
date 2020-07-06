/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-ip-filters.html.

Purpose:
ses_deletereceiptfilter.js demonstrates how to delete an Amazon SES IP address filter.

Inputs:
- REGION (in command line below; an IP address or range of addresses to filter)
- IP_ADDRESS_OR_RANGE (in code; either a single IP address (10.0.0.1) or an IP
  address range in CIDR notation (10.0.0.1/24))
- Policy (in code; 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions)
- NAME (in code; the filter name)

Running the code:
node ses_deletereceiptfilter.js REGION FILTER_NAME
 */
// snippet-start:[ses.JavaScript.v3.filters.deleteReceiptFilter]
// Import required AWS SDK clients and commands for Node.js
const {SES, DeleteReceiptFilterCommand} = require("@aws-sdk/client-ses");
// Set the AWS Region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {FilterName: process.argv[3]};

async function run() {
    try {
        const data = await ses.send(new DeleteReceiptFilterCommand(params));
        console.log("IP Filter deleted")
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.v3.filters.deleteReceiptFilter]
exports.run = run; //for unit tests only
