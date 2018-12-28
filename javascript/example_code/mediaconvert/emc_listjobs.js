 
//snippet-sourcedescription:[emc_listjobs.js demonstrates how to retrieve a list of the most-recently created transcoding jobs.]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaConvert]
//snippet-service:[mediaconvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[daviddeyo]


// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/emc-examples-jobs.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-west-2'});
// Set the customer endpoint
AWS.config.mediaconvert = {endpoint : 'ACCOUNT_ENDPOINT'};


var params = {
  MaxResults: 10,
  Order: 'ASCENDING',
  Queue: 'QUEUE_ARN',
  Status: 'SUBMITTED'
};

// Create a promise on a MediaConvert object
var endpointPromise = new AWS.MediaConvert({apiVersion: '2017-08-29'}).listJobs(params).promise();

// Handle promise's fulfilled/rejected status
endpointPromise.then(
  function(data) {
    console.log("Jobs: ", data);
  },
  function(err) {
    console.log("Error", err);
  }
);