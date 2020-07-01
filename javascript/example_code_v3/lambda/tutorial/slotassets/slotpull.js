/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/using-lambda-function-prep.html.
Purpose:
    lambda-function-setup.test.js demostrates how to create a lambda function.

Running the code:
    node lambda-function-setup.test.js REGION ACCESS_KEY_ID
*/
'use strict';

// Load the DynamoDB client
const { DynamoDBClient, GetItemCommand } = require('@aws-sdk/client-dynamodb');

exports.handler = (event, context, callback) => {

    // Define the object that will hold the data values returned
    let slotResults = {
        'isWinner' : false,
        'leftWheelImage' : {'file' : {S: ''}},
        'middleWheelImage' : {'file' : {S: ''}},
        'rightWheelImage' : {'file' : {S: ''}}
    };

    const tableName = 'TABLE_NAME';

    // Instantiate a DynamoDB client
    const ddb = new DynamoDBClient({region: 'us-west-2'});

    // =============================LEFT===========================================
    // Set a random number 0-9 for the left slot position
    const leftParams = {
        TableName: tableName,
        Key: { slotPosition: { N: Math.floor(Math.random()*10).toString() } }
    }
    // Call DynamoDB to retrieve the image to use for the Left slot result
    const myLeftPromise = ddb.send(new GetItemCommand(leftParams)).then(
        data => data.Item.imageFile.S,
        err => {
            console.log("Database read error on left wheel.");
        }
    );

    // =============================MIDDLE===========================================
    // Set a random number 0-9 for the middle slot position
    const middleParams = {
        TableName: tableName,
        Key: { slotPosition: { N: Math.floor(Math.random()*10).toString() } }
    }
    // Call DynamoDB to retrieve the image to use for the Left slot result
    const myMiddlePromise = ddb.send(new GetItemCommand(middleParams)).then(
        data => data.Item.imageFile.S,
        err => {
            console.log("Database read error on middle wheel.");
        }
    );

    // =============================RIGHT===========================================
    // Set a random number 0-9 for the slot position
    const rightParams = {
        TableName: tableName,
        Key: { slotPosition: { N: Math.floor(Math.random()*10).toString() } }
    }
    // Call DynamoDB to retrieve the image to use for the Left slot result
    const myRightPromise = ddb.send(new GetItemCommand(rightParams)).then(
        data => data.Item.imageFile.S,
        err => {
            console.log("Database read error on right wheel.");
        }
    )


    Promise.all([myLeftPromise, myMiddlePromise, myRightPromise]).then(
        function(values) {
            slotResults.leftWheelImage.file.S = values[0];
            slotResults.middleWheelImage.file.S = values[1];
            slotResults.rightWheelImage.file.S = values[2];
            // If all three values are identical, the spin is a winner
            if ((values[0] === values[1]) && (values[0] === values[2])) {
                slotResults.isWinner = true;
            }
            // Return the JSON result to the caller of the Lambda function
            callback(null, slotResults);
        }
    );
};
