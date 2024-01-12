// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.CodeExample.parsewiki]
var InstaView = require("instaview");
var AWS = require("aws-sdk");

exports.handler = function (event, context, callback) {
  // Uncomment for logging incoming event and records.
  //console.log('Received event:', JSON.stringify(event, null, 2));
  event.Records.forEach(function (record) {
    // Uncomment for logging event information.
    //console.log(record.eventID);
    //console.log(record.eventName);

    // Get the relevant fields from the record.
    var id = record.dynamodb.NewImage.id.S;
    var wiki = record.dynamodb.NewImage.wiki.S;

    AWS.config.update({
      region: "us-east-1",
    });

    var dynamodbDoc = new AWS.DynamoDB.DocumentClient();

    // Set the InstaView parameters to ensure links have proper path.

    InstaView.conf.paths = {
      base_href: "",
      articles: "wiki.htm?id=",
      math: "/math/",
      images: "",
      images_fallback: "http://upload.wikimedia.org/wikipedia/commons/",
      magnify_icon: "skins/common/images/magnify-clip.png",
    };

    // Use InstaView to render HTML content from wiki markup.
    var rendered = InstaView.convert(wiki);

    // Update the record with the HTML output.
    var params = {
      TableName: "Wiki",
      Key: {
        id: id,
      },
      AttributeUpdates: {
        content: {
          Action: "PUT",
          Value: rendered,
        },
      },
    };

    // Send update request to DynamoDB service.
    dynamodbDoc.update(params, function (err, data) {
      // Only log if there is an error.
      if (err) {
        console.error(
          "Unable to update item. Error JSON:",
          JSON.stringify(err, null, 2)
        );
      }
    });

    // Add entry with timestamp to history table for all previous versions.
    params = {
      TableName: "WikiHistory",
      Item: {
        id: id,
        timestamp: Math.floor(new Date() / 1000),
        wiki: wiki,
      },
    };

    // Send put item request to DynamoDB service.
    dynamodbDoc.put(params, function (err, data) {
      // Only log if there is an error.
      if (err) {
        console.error(
          "Unable to update item. Error JSON:",
          JSON.stringify(err, null, 2)
        );
      }
    });
  });

  // Exit when all calls return.
  callback();
};
// snippet-end:[dynamodb.JavaScript.CodeExample.parsewiki]
