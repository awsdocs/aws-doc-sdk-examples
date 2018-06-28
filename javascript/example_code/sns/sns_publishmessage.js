// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.

// Load the AWS SDK for Node.js
const AWS = require('aws-sdk');

// Configure the region
AWS.config.update({
    region: 'SNS_REGION'
});

// Create SNS service object
const sns = new AWS.SNS();

// Create the params object with the Example message and the SQS Topic ARN
let params = {
    Message: 'Test Message',
    TopicArn: 'TOPIC_ARN'
};

sns.publish(params, (err, data) => {
    if (err) {
        console.log(err.stack);
        return;
    }
    console.log(`Message ${params.message} send sent to the topic ${params.topicArn}`);
    console.log(data);
});

