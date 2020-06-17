/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-ip-filters.html

Purpose:
ses_deletereceiptfilter.test.js demonstrates how to delete an Amazon SES IP address filter.

Inputs:
- REGION (in commmand line input below): An IP address or range of addresses to filter
- IP_ADDRESS_OR_RANGE (replace in code): Either a single IP address (10.0.0.1) or an IP
  address range in CIDR notation (10.0.0.1/24)
- Policy (replace in code): 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions.
- NAME (replace in code): The filter name.

Running the code:
ses_createreceiptfilter.js REGION FILTER_NAME
 */
// snippet-start:[ses.JavaScript.filters.deleteReceiptFilter]
async function run() {
    try {
        const {SES, DeleteReceiptFilterCommand} = require("@aws-sdk/client-ses");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {Identity: process.argv[3]};
        const data = await ses.send(new DeleteReceiptFilterCommand(params));
        console.log("IP Filter deleted")
    } catch (err) {
        console.error(err, err.stack);
    }
};
run();
// snippet-end:[ses.JavaScript.filters.deleteReceiptFilter]
exports.run = run;
