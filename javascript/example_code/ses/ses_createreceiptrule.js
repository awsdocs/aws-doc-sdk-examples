//snippet-sourceauthor: [daviddeyo]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[5/25/18]

// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-receipt-rules.html
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create createReceiptRule params 
var params = {
 Rule: {
  Actions: [
     {
    S3Action: {
     BucketName: "S3_BUCKET_NAME/*",
     ObjectKeyPrefix: "email"
    }
   }
  ],
    Recipients: [
      'DOMAIN | EMAIL_ADDRESS',
      /* more items */
    ],
  Enabled: true | false,
  Name: "RULE_NAME",
  ScanEnabled: true | false,
  TlsPolicy: "Optional"
 },
 RuleSetName: "RULE_SET_NAME"
};

// Create the promise and SES service object
var newRulePromise = new AWS.SES({apiVersion: '2010-12-01'}).createReceiptRule(params).promise();

// Handle promise's fulfilled/rejected states
newRulePromise.then(
  function(data) {
    console.log("Rule created");
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
