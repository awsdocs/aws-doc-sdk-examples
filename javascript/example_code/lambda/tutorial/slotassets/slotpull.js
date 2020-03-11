/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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
