---
debug:
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
  finish: end_turn
  engine: bedrock
isolated: false
combined: true
prompt: |
  Persona: You are a guru-level solutions engineer with expertise in javascript and AWS kinesis-firehose.
  Task: Write code for a workflow using the AWS SDK for javascript to interface with the kinesis-firehose.
  Output:
     - Give me a perfectly-functional file based on the list of files you created in the previous step (03_FILES.md)
     - Each file should be wrapped in <file></file> parent tags that will allow an automated process to interpret them
     - Additionally, within these <file> tags, include a <name> tag identifying the file name and a <contents> tag with the raw file contents.
     - Do not include ANY formatting. For example: ticks or any other markdown language formatting.
     - Do not explain anything. Just provide the file contents in the format requested.
---
<file>
<name>index.js</name>
<contents>
const config = require('./config');
const firehoseClient = require('./firehoseClient');
const dataIngestion = require('./dataIngestion');
const logger = require('./logger');
const errorHandler = require('./errorHandler');

const main = async () => {
  try {
    const records = [
      Buffer.from('Record 1'),
      Buffer.from('Record 2'),
      Buffer.from('Record 3'),
    ];

    await dataIngestion.putRecords(firehoseClient, config.deliveryStreamName, records);
    await dataIngestion.putRecordBatch(firehoseClient, config.deliveryStreamName, records);
  } catch (err) {
    errorHandler.handleError(err);
  }
};

main();
</contents>
</file>

<file>
<name>config.js</name>
<contents>
require('dotenv').config();

const config = {
  deliveryStreamName: process.env.DELIVERY_STREAM_NAME,
  logGroupName: process.env.LOG_GROUP_NAME,
};

module.exports = config;
</contents>
</file>

<file>
<name>firehoseClient.js</name>
<contents>
const AWS = require('aws-sdk');

const firehoseClient = new AWS.Firehose({
  region: process.env.AWS_REGION,
});

module.exports = firehoseClient;
</contents>
</file>

<file>
<name>dataIngestion.js</name>
<contents>
const logger = require('./logger');
const errorHandler = require('./errorHandler');

const putRecord = async (firehoseClient, deliveryStreamName, record) => {
  try {
    const params = {
      DeliveryStreamName: deliveryStreamName,
      Record: record,
    };

    const response = await firehoseClient.putRecord(params).promise();
    logger.info(`Successfully ingested record: ${record.toString()}`);
    return response;
  } catch (err) {
    errorHandler.handleError(err);
  }
};

const putRecordBatch = async (firehoseClient, deliveryStreamName, records) => {
  try {
    const params = {
      DeliveryStreamName: deliveryStreamName,
      Records: records,
    };

    const response = await firehoseClient.putRecordBatch(params).promise();
    logger.info(`Successfully ingested ${records.length} records`);
    return response;
  } catch (err) {
    errorHandler.handleError(err);
  }
};

module.exports = {
  putRecord,
  putRecordBatch,
};
</contents>
</file>

<file>
<name>logger.js</name>
<contents>
const winston = require('winston');

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.json(),
  defaultMeta: { service: 'kinesis-firehose-example' },
  transports: [
    new winston.transports.Console(),
  ],
});

module.exports = logger;
</contents>
</file>

<file>
<name>errorHandler.js</name>
<contents>
const logger = require('./logger');

const handleError = (err) => {
  logger.error(err);
  // Additional error handling logic, e.g., retries, fallbacks
};

module.exports = {
  handleError,
};
</contents>
</file>

<file>
<name>cdk.ts</name>
<contents>
import * as cdk from 'aws-cdk-lib';
import * as kinesis from 'aws-cdk-lib/aws-kinesisfirehose';
import * as logs from 'aws-cdk-lib/aws-logs';

const app = new cdk.App();
const stack = new cdk.Stack(app, 'KinesisFirehoseExampleStack');

const logGroup = new logs.LogGroup(stack, 'LogGroup', {
  logGroupName: process.env.LOG_GROUP_NAME,
  removalPolicy: cdk.RemovalPolicy.DESTROY,
});

const deliveryStream = new kinesis.CfnDeliveryStream(stack, 'DeliveryStream', {
  deliveryStreamName: process.env.DELIVERY_STREAM_NAME,
  deliveryStreamType: 'DirectPut',
});

new cdk.CfnOutput(stack, 'DeliveryStreamName', {
  value: deliveryStream.deliveryStreamName,
  description: 'The name of the Kinesis Firehose Delivery Stream',
});

new cdk.CfnOutput(stack, 'LogGroupName', {
  value: logGroup.logGroupName,
  description: 'The name of the CloudWatch Log Group',
});
</contents>
</file>

<file>
<name>setup.sh</name>
<contents>
#!/bin/bash

# Install dependencies
npm install

# Build CDK app
npm run build
</contents>
</file>

<file>
<name>index.test.js</name>
<contents>
const index = require('./index');

// Test cases for the main entry point
</contents>
</file>

<file>
<name>config.test.js</name>
<contents>
const config = require('./config');

// Test cases for the configuration module
</contents>
</file>

<file>
<name>firehoseClient.test.js</name>
<contents>
const firehoseClient = require('./firehoseClient');

// Test cases for the Kinesis Firehose client module
</contents>
</file>

<file>
<name>dataIngestion.test.js</name>
<contents>
const dataIngestion = require('./dataIngestion');

// Test cases for the data ingestion module
</contents>
</file>

<file>
<name>logger.test.js</name>
<contents>
const logger = require('./logger');

// Test cases for the logging module
</contents>
</file>

<file>
<name>errorHandler.test.js</name>
<contents>
const errorHandler = require('./errorHandler');

// Test cases for the error handling module
</contents>
</file>