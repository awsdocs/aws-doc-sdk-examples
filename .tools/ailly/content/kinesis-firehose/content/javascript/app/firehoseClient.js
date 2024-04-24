const AWS = require('aws-sdk');

const firehoseClient = new AWS.Firehose({
  region: process.env.AWS_REGION,
});

module.exports = firehoseClient;