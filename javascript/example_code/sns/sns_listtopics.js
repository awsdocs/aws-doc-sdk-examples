 
//snippet-sourcedescription:[sns_listtopics.js demonstrates how to retrieve a list of Amazon SNS topics.]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Notification Service]
//snippet-service:[sns]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[daviddeyo]


// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sns-examples-managing-topics.html 
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set region
AWS.config.update({region: 'us-west-2'});


// Create promise and SNS service object
var listTopicsPromise = new AWS.SNS({apiVersion: '2010-03-31'}).listTopics({}).promise();

// handle promise's fulfilled/rejected states
listTopicsPromise.then(
  function(data) {
    console.log(data.Topics);
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
