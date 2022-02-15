/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3), which is available
at https://github.com/aws/aws-sdk-js-v3.

// Purpose:
index.html is the front-end for an example demonstrating how to trigger an AWS Lambda function from the browser.

 */
// snippet-start:[cross-service.lambda-from-browser.javascriptv3.lambda]

'use strict'

console.log('Loading function');

// Import required AWS SDK clients and commands for Node.js.
import { PutCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "../libs/ddbDocClient";

exports.handler = async(event, context) => {

    const params = {
        Item: {
            Id: event.Item.Id,
            Color: event.Item.Color,
            Pattern: event.Item.Pattern
        },
        TableName: event.TableName
    };

    try {
        const data = await ddbDocClient.send(new PutCommand(params));
        console.log("Adding data to dynamodb...");
        console.log("Added item:", JSON.stringify(data, null, 2));
    } catch (err) {
        console.error(
            "Unable to add item. Error JSON:",
            JSON.stringify(err, null, 2)
        );
    }
};

// snippet-end:[cross-service.lambda-from-browser.javascriptv3.lambda]
