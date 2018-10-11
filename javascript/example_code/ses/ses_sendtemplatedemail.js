 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ses-examples-sending-email.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create sendTemplatedEmail params 
var params = {
  Destination: { /* required */
    CcAddresses: [
      'EMAIL_ADDRESS',
      /* more CC email addresses */
    ],
    ToAddresses: [
      'EMAIL_ADDRESS',
      /* more To email addresses */
    ]
  },
  Source: 'EMAIL_ADDRESS', /* required */
  Template: 'TEMPLATE_NAME', /* required */
  TemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }', /* required */
  ReplyToAddresses: [
    'EMAIL_ADDRESS'
  ],
};


// Create the promise and SES service object
var sendPromise = new AWS.SES({apiVersion: '2010-12-01'}).sendTemplatedEmail(params).promise();

// Handle promise's fulfilled/rejected states
sendPromise.then(
  function(data) {
    console.log(data);
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
