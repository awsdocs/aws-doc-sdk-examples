// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//cloudwatch-examples-getting-metrics.html 
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create CloudWatch service object
var cw = new AWS.CloudWatch({apiVersion: '2010-08-01'});

// Create parameters JSON for putMetricData
var params = {
  MetricData: [
    {
      MetricName: 'PAGES_VISITED',
      Dimensions: [
        {
          Name: 'UNIQUE_PAGES',
          Value: 'URLS'
        },
      ],
      Unit: 'None',
      Value: 1.0
    },
  ],
  Namespace: 'SITE/TRAFFIC'
};

cw.putMetricData(params, function(err, data) {
  if (err) {
    console.log("Error", err);
  } else {
    console.log("Success", JSON.stringify(data));
  }
});
