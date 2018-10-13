//snippet-sourceauthor: [daviddeyo]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[5/25/18]

// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/emc-examples-templates.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-west-2'});
// Set the customer endpoint
AWS.config.mediaconvert = {endpoint : 'ACCOUNT_ENDPOINT'};


var params = {
  ListBy: 'NAME',
  MaxResults: 10,
  Order: 'ASCENDING',
};

// Create a promise on a MediaConvert object
var listTemplatesPromise = new AWS.MediaConvert({apiVersion: '2017-08-29'}).listJobTemplates(params).promise();

// Handle promise's fulfilled/rejected status
listTemplatesPromise.then(
  function(data) {
    console.log("Success ", data);
  },
  function(err) {
    console.log("Error", err);
  }
);