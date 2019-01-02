 
//snippet-sourcedescription:[ses_createreceiptfilter.js demonstrates how to create an Amazon SES IP address filter.]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-service:[ses]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[daviddeyo]


// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-ip-filters.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create createReceiptFilter params
var params = {
 Filter: {
  IpFilter: {
   Cidr: "IP_ADDRESS_OR_RANGE", 
   Policy: "Allow" | "Block"
  },
  Name: "NAME"
 }
};


// Create the promise and SES service object
var sendPromise = new AWS.SES({apiVersion: '2010-12-01'}).createReceiptFilter(params).promise();

// Handle promise's fulfilled/rejected states
sendPromise.then(
  function(data) {
    console.log(data);
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
