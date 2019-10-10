/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[writeLambdaFunction.js demonstrates how to write a Lambda function code]
// snippet-service:[lambda]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS_JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/nodejs-write-lambda-function-example-full.html

// NOTE:
// Supporting topics for the above-mentioned topic also contain important information about the role policy when creating the IAM role.

// snippet-start:[lambda.JavaScript.writeLambdaFunction.complete]
// Configuring the AWS SDK
var AWS = require('aws-sdk');
AWS.config.update({region: 'REGION'});

exports.handler = (event, context, callback) => {

const URL_BASE = "S3_BUCKET_URL";

// Define the object that will hold the data values returned
var slotResults = {
	'isWinner' : false,
	'leftWheelImage' : {'file' : {S: ''}},
	'middleWheelImage' : {'file' : {S: ''}},
	'rightWheelImage' : {'file' : {S: ''}}
};

// define parameters JSON for retrieving slot pull data from the database
var thisPullParams = {
    Key : {'slotPosition' : {N: ''}},
    TableName: 'slotWheels',
    ProjectionExpression: 'imageFile'
};

// create DynamoDB service object
var request = new AWS.DynamoDB({region: 'REGION', apiVersion: '2012-08-10'});

// set a random number 0-9 for the slot position
thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*15).toString();
// call DynamoDB to retrieve the image to use for the Left slot result
var myLeftPromise = request.getItem(thisPullParams).promise().then(function(data) {return URL_BASE + data.Item.imageFile.S});

// set a random number 0-9 for the slot position
thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*15).toString();
// call DynamoDB to retrieve the image to use for the Left slot result
var myMiddlePromise = request.getItem(thisPullParams).promise().then(function(data) {return URL_BASE + data.Item.imageFile.S});

// set a random number 0-9 for the slot position
thisPullParams.Key.slotPosition.N = Math.floor(Math.random()*15).toString();
// call DynamoDB to retrieve the image to use for the Left slot result
var myRightPromise = request.getItem(thisPullParams).promise().then(function(data) {return URL_BASE + data.Item.imageFile.S});


Promise.all([myLeftPromise, myMiddlePromise, myRightPromise]).then(function(values) {
    slotResults.leftWheelImage.file.S = values[0];
    slotResults.middleWheelImage.file.S = values[1];
    slotResults.rightWheelImage.file.S = values[2];
    // if all three values are identical, the spin is a winner
    if ((values[0] === values[1]) && (values[0] === values[2])) {
        slotResults.isWinner = true;
    }
    // return the JSON result to the caller of the Lambda function
    callback(null, slotResults);
});

};
// snippet-end:[lambda.JavaScript.writeLambdaFunction.complete]
