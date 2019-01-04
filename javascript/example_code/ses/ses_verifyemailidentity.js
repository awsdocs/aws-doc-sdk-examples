 
//snippet-sourcedescription:[ses_verifyemailidentity.js demonstrates how to send an Amazon SES verification email.]
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
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-managing-identities.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set region 
AWS.config.update({region: 'REGION'});

// Create promise and SES service object
var verifyEmailPromise = new AWS.SES({apiVersion: '2010-12-01'}).verifyEmailIdentity({EmailAddress: "ADDRESS@DOMAIN.EXT"}).promise();

// Handle promise's fulfilled/rejected states
verifyEmailPromise.then(
  function(data) {
    console.log("Email verification initiated");
   }).catch(
    function(err) {
    console.error(err, err.stack);
  });
