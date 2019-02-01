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
 
//snippet-sourcedescription:[ses_sendbulktemplatedemail.js demonstrates how to compose an Amazon SES templated email for multiple destinations and queue it for sending.]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-service:[ses]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ses-examples-sending-email.html

// snippet-start:[ses.JavaScript.email.sendBulkTemplatedEmail]
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create sendBulkTemplatedEmail params 
var params = {
  Destinations: [ /* required */
    {
      Destination: { /* required */
        CcAddresses: [
          'EMAIL_ADDRESS',
          /* more items */
        ],
        ToAddresses: [
          'EMAIL_ADDRESS',
          'EMAIL_ADDRESS'
          /* more items */
        ]
      },
      ReplacementTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }'
  },
  ],
  Source: 'EMAIL_ADDRESS', /* required */
  Template: 'TEMPLATE_NAME', /* required */
  DefaultTemplateData: '{ \"REPLACEMENT_TAG_NAME\":\"REPLACEMENT_VALUE\" }',
  ReplyToAddresses: [
    'EMAIL_ADDRESS'
  ]
};


// Create the promise and SES service object
var sendPromise = new AWS.SES({apiVersion: '2010-12-01'}).sendBulkTemplatedEmail(params).promise();

// Handle promise's fulfilled/rejected states
sendPromise.then(
  function(data) {
    console.log(data);
  }).catch(
    function(err) {
    console.log(err, err.stack);
  });
// snippet-end:[ses.JavaScript.email.sendBulkTemplatedEmail]
