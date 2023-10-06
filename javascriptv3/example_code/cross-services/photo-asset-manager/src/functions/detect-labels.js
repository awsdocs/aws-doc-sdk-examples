/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  RekognitionClient,
  DetectLabelsCommand,
} from "@aws-sdk/client-rekognition";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, UpdateCommand } from "@aws-sdk/lib-dynamodb";

import { LABELS_TABLE_NAME } from "../lambda-env.js";
import { withLogging } from "../common.js";

/**
 * Detect labels on an image in an Amazon S3 bucket.
 * @param {string} bucket
 * @param {string} key
 * @returns {Promise<string[]>} An array of labels.
 */
const detect = async (bucket, key) => {
  const client = new RekognitionClient({});
  const command = new DetectLabelsCommand({
    // The "Image" property can be either a base64-encoded image string or a reference to an Amazon S3 object.
    // This AWS Lambda function is invoked by the Amazon S3 trigger on the bucket when an object is uploaded.
    // The bucket and key from the event are used here.
    Image: {
      S3Object: {
        Bucket: bucket,
        Name: key,
      },
    },
  });
  const response = await client.send(command);

  if (!response.Labels) {
    throw new Error("No labels found.");
  }

  return response.Labels.filter((label) => label.Confidence >= 95).map(
    (label) => label.Name,
  );
};

const store = async (key, labels) => {
  const client = new DynamoDBClient({});
  const ddbDocClient = DynamoDBDocumentClient.from(client);

  for (const label of labels) {
    // The UpdateCommand is used to add or update an item to Amazon DynamoDB.
    const command = new UpdateCommand({
      TableName: LABELS_TABLE_NAME,
      Key: {
        Label: label,
      },
      // Add or update an image label to DynamoDB. Increment the count of images
      // matching that label.
      UpdateExpression:
        "SET #Count = if_not_exists(#Count, :zero) + :one, Images = list_append(if_not_exists(Images, :empty), :images)",
      ExpressionAttributeValues: {
        ":zero": 0,
        ":one": 1,
        ":empty": [],
        ":images": [key],
      },
      ExpressionAttributeNames: {
        "#Count": "Count",
      },
    });

    await ddbDocClient.send(command);
  }
};

/**
 * Create a handler function.
 * @param {{
 *   detect: ( bucket: string, key: string ) => Promise<string[]>,
 *   store: (key: string, labels: string[]) => Promise<void>
 * }} options
 */
export const getHandler = ({ detect, store }) => {
  /**
   * Detect labels on new images uploaded to Amazon S3. Store
   * those labels in an Amazon DynamoDB table.
   * @type {import('aws-lambda').S3Handler}
   */
  return async (event) => {
    for (const record of event.Records) {
      const labels = await detect(record.s3.bucket.name, record.s3.object.key);
      console.log(`Labels for ${record.s3.object.key}: `, labels);
      await store(record.s3.object.key, labels);
    }
  };
};

export const handler = withLogging(getHandler({ detect, store }));
