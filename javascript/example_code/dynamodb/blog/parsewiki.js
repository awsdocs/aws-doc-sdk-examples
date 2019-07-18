// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.JavaScript.CodeExample.parsewiki]

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
 */
const InstaView = require('instaview');
const AWS = require('aws-sdk');
AWS.config.update({
  region: process.env.AWS_REGION,
});
const dynamodbDoc = new AWS.DynamoDB.DocumentClient();

exports.handler = async event => {
  // Uncomment for logging incoming event and records.
  //console.log('Received event:', JSON.stringify(event, null, 2));
  for (const record of event.Records) {
    // Uncomment for logging event information.
    //console.log(record.eventID);
    //console.log(record.eventName);

    // Get the relevant fields from the record.
    const id = record.dynamodb.NewImage.id.S;
    const wiki = record.dynamodb.NewImage.wiki.S;

    // Set the InstaView parameters to ensure links have proper path.

    InstaView.conf.paths = {
      base_href: '',
      articles: 'wiki.htm?id=',
      math: '/math/',
      images: '',
      images_fallback: 'http://upload.wikimedia.org/wikipedia/commons/',
      magnify_icon: 'skins/common/images/magnify-clip.png'
    };

    // Use InstaView to render HTML content from wiki markup.
    const rendered = InstaView.convert(wiki);

    // Update the record with the HTML output.
    let params = {
      TableName: 'Wiki',
      Key: {
        id: id
      },
      AttributeUpdates: {
        content: {
          Action: 'PUT',
          Value: rendered,
        }
      }
    };

    // Send update request to DynamoDB service.
    try {
      await dynamodbDoc.update(params).promise();
    } catch (err) {
      console.error('Unable to update item. Error JSON:', JSON.stringify(err, null, 2));
      return {
        statusCode: 500,
        body: JSON.stringify({ err })
      };
    }

    // Add entry with timestamp to history table for all previous versions.
    params = {
      TableName: 'WikiHistory',
      Item: {
        id: id,
        timestamp: Math.floor(new Date() / 1000),
        wiki: wiki
      }
    };

    // Send put item request to DynamoDB service.
    try {
      await dynamodbDoc.put(params).promise();
    } catch (err) {
      console.error('Unable to update item. Error JSON:', JSON.stringify(err, null, 2));
      return {
        statusCode: 500,
        body: JSON.stringify({ err })
      };
    }
  }
  return {
    statusCode: 200,
    body: JSON.stringify({ message: 'Success' })
  };
};
// snippet-end:[dynamodb.JavaScript.CodeExample.parsewiki]
