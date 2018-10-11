 
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
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/sns-examples-managing-topics.html 
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set region
AWS.config.update({region: 'us-west-2'});


// Create promise and SNS service object
var deleteTopicPromise = new AWS.SNS({apiVersion: '2010-03-31'}).deleteTopic({TopicArn: 'TOPIC_ARN'}).promise();

// handle promise's fulfilled/rejected states
deleteTopicPromise.then(
  function(data) {
    console.log("Topic Deleted");
  }).catch(
    function(err) {
    console.error(err, err.stack);
  });
