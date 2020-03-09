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

// Configuring the AWS SDK
var AWS = require('aws-sdk');
AWS.config.update({region: 'us-west-2'});

exports.myHandler = (event, context, callback) => {

    // Define the object that will hold the data values returned
    var slotResults = {
        'isWinner' : false,
        'leftWheelImage' : {'file' : {S: ''}},
        'middleWheelImage' : {'file' : {S: ''}},
        'rightWheelImage' : {'file' : {S: ''}}
    };

    // Define parameters JSON for retrieving slot pull data from the database
    var thisPullParams = {
        Key: {
            "slotPosition": {
                N: ""
            }
        },
        TableName: "TABLE_NAME"
    };

    // Create DynamoDB service object
    var request = new AWS.DynamoDB({region: 'us-west-2', apiVersion: '2012-08-10'});

    // =============================LEFT===========================================
    // Set a random number 0-9 for the left slot position
    thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*10).toString();
    // Call DynamoDB to retrieve the image to use for the Left slot result
    var myLeftPromise = request.getItem(thisPullParams).promise().then(
        function(data) {
            return data.Item.imageFile.S
        },
        function() {
            console.log("Database read error on left wheel.")
        }
    );

    // =============================MIDDLE===========================================
    // Set a random number 0-9 for the middle slot position
    thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*10).toString();
    // Call DynamoDB to retrieve the image to use for the Left slot result
    var myMiddlePromise = request.getItem(thisPullParams).promise().then(
        function(data) {
            return data.Item.imageFile.S
        },
        function() {
            console.log("Database read error on middle wheel.")
        }
    );

    // =============================RIGHT===========================================
    // Set a random number 0-9 for the slot position
    thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*10).toString();
    // Call DynamoDB to retrieve the image to use for the Left slot result
    var myRightPromise = request.getItem(thisPullParams).promise().then(
        function(data) {
            return data.Item.imageFile.S
        },
        function() {
            console.log("Database read error on right wheel.")
        }
    );


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
