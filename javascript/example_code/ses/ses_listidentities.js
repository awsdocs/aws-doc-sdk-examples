/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
 
//snippet-sourcedescription:[ses_listidentities.js demonstrates how to list all the identities for an AWS account.]
//snippet-keyword:[JavaScript]
//snippet-sourcesyntax:[javascript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-service:[ses]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ses-examples-managing-identities.html

// snippet-start:[ses.JavaScript.identities.listIdentities]
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set region 
AWS.config.update({region: 'REGION'});

// Create listIdentities params 
var params = {
 IdentityType: "Domain",
 MaxItems: 10
};

// Create the promise and SES service object
var listIDsPromise = new AWS.SES({apiVersion: '2010-12-01'}).listIdentities(params).promise();

// Handle promise's fulfilled/rejected states
listIDsPromise.then(
  function(data) {
    console.log(data.Identities);
  }).catch(
  function(err) {
    console.error(err, err.stack);
  });
// snippet-end:[ses.JavaScript.identities.listIdentities]
