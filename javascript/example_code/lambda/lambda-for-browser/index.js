/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (lambda-for-browser).

// Purpose:
index.js is an AWS Lambda function in an example demonstrating how to trigger a Lambda function from the browser.

 */
// snippet-start:[cross-service.lambda-from-browser.javascript.lambda]

'use strict'

console.log('Loading function');

var AWS = require('aws-sdk');

// Initialize the Amazon Cognito credentials provider.
AWS.config.region = "REGION";
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: "IDENTITY_POOL_ID",
});


// Create client.
const docClient = new AWS.DynamoDB.DocumentClient();


exports.handler = async(event, context, callback) => {
    const params = {
        Item: {
            Id: event.Item.Id,
            Color: event.Item.Color,
            Pattern: event.Item.Pattern
        },
        TableName: event.TableName
    };
    await docClient.put(params, async function (err, data) {
        if (err) {
            console.error(
                "Unable to add item. Error JSON:",
                JSON.stringify(err, null, 2)
            );
        } else {
            console.log("Adding data to dynamodb...");
            console.log("Added item:", JSON.stringify(data, null, 2));
        }
    });
    callback(null, event);
};

// snippet-end:[cross-service.lambda-from-browser.javascript.lambda]
