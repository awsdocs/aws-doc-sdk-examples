
/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 2 (v2).

Purpose:
ses_listreceiptrulesets.js demonstrates how to list Amazon Simple Email Service (SES) receipt rule sets.

*/

// snippet-start:[ses.JavaScript.filters.listReceiptRuleSets]
// Load the AWS SDK for Node.js.
var AWS = require('aws-sdk');
// Set the AWS Region.
AWS.config.update({ region: 'REGION' });

// Create listReceiptRuleSets parameters.
var params = {
    NextToken: ""
};


// Create the promise and SES service object.
var sendPromise = new AWS.SES({ apiVersion: '2010-12-01' }).listReceiptRuleSets(params).promise();

// Handle promise's fulfilled/rejected states.
sendPromise.then(
    function (data) {
        console.log(data);
    }).catch(
    function (err) {
        console.error(err, err.stack);
    });
// snippet-end:[ses.JavaScript.filters.listReceiptRuleSets]
