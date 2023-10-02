/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { S3Client, GetObjectCommand } from "@aws-sdk/client-s3";
import { Upload } from "@aws-sdk/lib-storage";
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { BatchGetCommand, DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";
import { SNSClient, PublishCommand } from "@aws-sdk/client-sns";
import { randomUUID } from "node:crypto";
import { Readable } from "node:stream";
import archiver from "archiver";

import { withLogging, createPresignedGetURL } from "../common.js";
import {
  LABELS_TABLE_NAME,
  NOTIFICATION_TOPIC,
  STORAGE_BUCKET_NAME,
  WORKING_BUCKET_NAME,
} from "../lambda-env.js";

/**
 * Get a list of unique image keys for the given labels.
 * @param {string[]} labels
 */
export const getImageKeysForLabels = async (labels) => {
  const client = new DynamoDBClient({});
  const docClient = DynamoDBDocumentClient.from(client);

  const uniqueImageKeys = new Set();

  /**
   * Using BatchGetCommand instead of multiple GetCommand calls reduces
   * the number of calls made to Amazon DynamoDB.
   */
  const command = new BatchGetCommand({
    RequestItems: {
      [LABELS_TABLE_NAME]: {
        Keys: labels.map((label) => ({ Label: label })),
        /**
         * Only the Images field is needed.
         */
        ProjectionExpression: "Images",
      },
    },
  });

  const { Responses } = await docClient.send(command);
  /**
   * @type {{ Images: string[]}[]}
   */
  const labelsTableResponses = Responses[LABELS_TABLE_NAME];
  labelsTableResponses.forEach((response) => {
    response.Images.forEach((image) => uniqueImageKeys.add(image));
  });

  return Array.from(uniqueImageKeys);
};

/**
 * Fetch an object from Amazon Simple Storage Service (Amazon S3) and return a Readable stream.
 * @param {string} imageKey
 */
const s3Readable = async (imageKey) => {
  const client = new S3Client({});
  const command = new GetObjectCommand({
    Bucket: STORAGE_BUCKET_NAME,
    Key: imageKey,
  });
  const { Body } = await client.send(command);

  return Body;
};

// Upload the archive to Amazon S3. Images will be compressed and streamed
// to the destination bucket. The Upload accepts the stream and handles
// the rest.
const zipAndUpload = async (imageKeys) => {
  const archive = archiver("zip");
  archive.on("error", (err) => {
    console.error("ARCHIVE ERROR: ", err);
    throw err;
  });
  archive.on("warning", (warn) => {
    console.warn("ARCHIVE WARNING: ", warn);
  });

  const fileName = `${randomUUID()}.zip`;
  const client = new S3Client({});
  console.log("UPLOADING ARCHIVE TO S3: ", fileName);
  const upload = new Upload({
    client,
    params: {
      Bucket: WORKING_BUCKET_NAME,
      Key: fileName,
      Body: Readable.from(archive),
    },
  });
  upload.on("httpUploadProgress", (progress) => {
    console.log("PROGRESS: ", progress);
  });

  for (const imageKey of imageKeys) {
    console.log("FETCHING IMAGE: ", imageKey);
    const s3Stream = await s3Readable(imageKey);
    console.log("APPENDING IMAGE TO ARCHIVE: ", imageKey);
    archive.append(s3Stream, { name: imageKey });
  }
  archive.finalize();
  console.log("DONE APPENDING IMAGES");

  await upload.done();
  console.log("UPLOAD COMPLETE");
  return fileName;
};

/**
 * Publish a message to Amazon Simple Notification Service (Amazon SNS) with a URL to download the zip.
 * @param {string} url
 */
const publishMessage = async (url) => {
  const client = new SNSClient({});
  const message =
    "Your images are ready for download at the following URL.\n" +
    "Amazon SNS breaks up the long URL. Strip out the whitespace characters to get the correct link.\n" +
    url;
  const command = new PublishCommand({
    Message: message,
    TopicArn: NOTIFICATION_TOPIC,
  });
  await client.send(command);
};

/**
 * @param {{
 *   zipAndUpload: (imageKeys: string[]) => Promise<string>,
 *   getImageKeysForLabels: (labels: string[]) => Promise<string[]>,
 *   publishMessage: (url: string) => Promise<void>,
 *   createPresignedGetURL: (params: { bucket: string, key: string }) => Promise<string>
 * }}
 */
const getHandler =
  ({
    getImageKeysForLabels,
    zipAndUpload,
    publishMessage,
    createPresignedGetURL,
  }) =>
  /**
   * Collect images that match the provided labels, zip them, and publish
   * a message with a URL to download the zip.
   *
   * The Event type here is APIGatewayEvent instead of APIGatewayProxyEvent.
   * This is because the download handler is invoked asynchronously and
   * configured to return a canned success response before the Lambda has
   * finished executing.
   *
   * @type {import("@types/aws-lambda").Handler}
   * @param {import("@types/aws-lambda").APIGatewayEventDefaultAuthorizerContext} event
   */
  async (event) => {
    const { labels } = event;
    const imageKeys = await getImageKeysForLabels(labels);
    console.log("IMAGES: ", imageKeys);
    console.log("DESTINATION BUCKET: ", WORKING_BUCKET_NAME);
    const fileName = await zipAndUpload(imageKeys);
    console.log("FILE NAME", fileName);
    const url = await createPresignedGetURL({
      bucket: WORKING_BUCKET_NAME,
      key: fileName,
    });
    await publishMessage(url);
    console.log("URL SENT TO SUBSCRIBERS: ", url);
  };

export const handler = withLogging(
  getHandler({
    zipAndUpload,
    getImageKeysForLabels,
    createPresignedGetURL,
    publishMessage,
  }),
);
