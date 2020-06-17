/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-ip-filters.html

Purpose:
ses_createreceiptfilter.js demonstrates how to create an Amazon SES IP address filter.]

Inputs:
- REGION (in commmand line input below)
- IP_ADDRESS_OR_RANGE (replace in code): Either a single IP address (10.0.0.1) or an IP
  address range in CIDR notation (10.0.0.1/24)
- Policy (replace in code): 'ALLOW' or 'BLOCK' email traffic from the filtered addressesOptions.
- NAME (replace in code): The filter name.

Running the code:
node ses_createreceiptfilter.js REGION IP_ADDRESS_OR_RANGE ALLOW|BLOCK NAME
 */

// snippet-start:[ses.JavaScript.filters.createReceiptFilter]
async function run() {
    try {
        const {SES, CreateReceiptFilterCommand} = require("@aws-sdk/client-sns");
        const region = process.argv[2];
        const ses = new SES(region);
        const params = {
            Filter: {
                IpFilter: {
                    Cidr: process.argv[3],
                    Policy: process.argv[4]
                },
                Name: process.argv[5]
            }
        }
        const data = await ses.send(new CreateReceiptFilterCommand(params));
        console.log(data)
    } catch (err) {
        console.error(err, err.stack);
    }
};
run()
// snippet-end:[ses.JavaScript.filters.createReceiptFilter]
exports.run = run;
