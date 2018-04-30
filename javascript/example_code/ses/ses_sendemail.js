// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ses-examples-sending-email.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create sendEmail params 
var params = {
  Destination: { /* required */
    CcAddresses: [
      'EMAIL_ADDRESS',
      /* more items */
    ],
    ToAddresses: [
      'EMAIL_ADDRESS',
      /* more items */
    ]
  },
  Message: { /* required */
    Body: { /* required */
      Html: {
       Charset: "UTF-8",
       Data: "HTML_FORMAT_BODY"
      },
      Text: {
       Charset: "UTF-8",
       Data: "TEXT_FORMAT_BODY"
      }
     },
     Subject: {
      Charset: 'UTF-8',
      Data: 'Test email'
     }
    },
  Source: 'SENDER_EMAIL_ADDRESS', /* required */
  ReplyToAddresses: [
     'EMAIL_ADDRESS',
    /* more items */
  ],
};
           
// Create the promise and SES service object
var sendPromise = new AWS.SES({apiVersion: '2010-12-01'}).sendEmail(params).promise();

// Handle promise's fulfilled/rejected states
sendPromise.then(
  function(data) {
    console.log(data.MessageId);
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
