// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[websocket_send_msg.js implements a WebSocket customRoute AWS Lambda function.]
// snippet-service:[lambda]
// snippet-keyword:[AWS Lambda]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-07-11]
// snippet-sourceauthor:[AWS]

// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License"). You
// may not use this file except in compliance with the License. A copy of
// the License is located at
//
// http://aws.amazon.com/apache2.0/
//
// or in the "license" file accompanying this file. This file is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
// ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.


const AWS = require('aws-sdk');

const ddb = new AWS.DynamoDB.DocumentClient({ apiVersion: '2012-08-10' });

const { TableName } = process.env;

exports.lambda_handler = async (event, context) => {
  // console.log(`Environment Variables:\n` + JSON.stringify(process.env, null, 2));
  // console.log(`WebSocket sendmsg: DynamoDB Table: ${TableName}`);
  console.log(`WebSocket sendmsg: event argument:\n` + JSON.stringify(event,null,2));

  // Retrieve all current connections
  let connectionData;
  try {
    connectionData = await ddb.scan({ TableName: TableName,
                                      ProjectionExpression: 'connectionId' }).promise();
  } catch (e) {
    console.log(e.stack);
    return { statusCode: 500, body: e.stack };
  }

  // Create management object for posting the message to each connection
  const apigwManagementApi = new AWS.ApiGatewayManagementApi({
    apiVersion: '2018-11-29',
    endpoint: event.requestContext.domainName + '/' + event.requestContext.stage
  });

  // Retrieve the message
  const message = JSON.parse(event.body).msg;
  console.log(`WebSocket sendmsg: Received message "${message}"`);

  // Send the message to each connection
  const postCalls = connectionData.Items.map(async ({ connectionId }) => {
    try {
      await apigwManagementApi.postToConnection({ ConnectionId: connectionId, Data: message }).promise();
    } catch (e) {
      if (e.statusCode === 410) {
        console.log(`Found stale connection, deleting ${connectionId}`);
        await ddb.delete({ TableName: TableName, Key: { connectionId } }).promise();
      } else {
        throw e;
      }
    }
  });

  try {
    await Promise.all(postCalls);
  } catch (e) {
    return { statusCode: 500, body: e.stack };
  }

  return { statusCode: 200, body: 'Data sent.' };
};