/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ses-examples-ip-filters.html..

Purpose:
ses_createreceiptfilter.js demonstrates how to create an Amazon SES IP address filter.]

Inputs:
- REGION (in command line input below)
- IP_ADDRESS_OR_RANGE (replace in code): Either a single IP address (10.0.0.1) or an IP
  address range in CIDR notation (10.0.0.1/24)
- Policy (replace in code): 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions.
- NAME (replace in code): The filter name.

Running the code:
node ses_createreceiptfilter.js REGION IP_ADDRESS_OR_RANGE ALLOW|BLOCK NAME
 */

// snippet-start:[ses.JavaScript.v3.filters.createReceiptFilter]
// Import required AWS-SDK clients and commands for Node.js
const {SES, CreateReceiptFilterCommand} = require("@aws-sdk/client-sns");
// Set the AWS region
const region = process.argv[2];
// Create SES service object
const ses = new SES(region);
// Set the parameters
const params = {
    Filter: {
        IpFilter: {
            Cidr: process.argv[3],
            Policy: process.argv[4]
        },
        Name: process.argv[5]
    }
};
async function run() {
    try {
        const data = await ses.send(new CreateReceiptFilterCommand(params));
        console.log(data)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run()
// snippet-end:[ses.JavaScript.v3.filters.createReceiptFilter]
exports.run = run; //for unit tests only
