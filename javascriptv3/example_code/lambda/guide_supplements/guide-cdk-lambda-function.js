/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

//snippet-start:[javascript.v3.cross_service.supplement.list_widgets]
import { S3Client, ListObjectsCommand } from "@aws-sdk/client-s3";

// In the following code we are using AWS JS SDK v3
// See https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html
const s3Client = new S3Client({});
const bucketName = process.env.BUCKET;

const listObjectNames = async (bucketName) => {
  const command = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await s3Client.send(command);

  if (!Contents.length) {
    const err = new Error(`No objects found in ${bucketName}`);
    err.name = "EmptyBucketError";
    throw err;
  }

  // Map the response to a list of strings representing the keys of the S3 objects.
  // Filter out any objects that don't have keys.
  return Contents.map(({ Key }) => Key).filter((k) => !!k);
};

/**
 * @typedef {{ httpMethod: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH', path: string }} LambdaEvent
 */

/**
 *
 * @param {LambdaEvent} lambdaEvent
 */
const routeRequest = (lambdaEvent) => {
  if (lambdaEvent.httpMethod === "GET" && lambdaEvent.path === "/") {
    return handleGetRequest();
  }

  return buildResponseBody(400, "Unsupported HTTP method.");
};

const handleGetRequest = async () => {
  const objects = await listObjectNames(bucketName);
  return buildResponseBody(200, objects);
};

/**
 * @typedef { statusCode: number, body: string, headers: Record<string, string> } Response
 */

/**
 *
 * @param {number} status
 * @param {Record<string, string>} headers
 * @param {Record<string, unknown>} body
 *
 * @returns {Response}
 */
const buildResponseBody = (status, body, headers = {}) => {
  return {
    statusCode: status,
    headers,
    body,
  };
};

/**
 *
 * @param {LambdaEvent} event
 */
export const handler = async (event) => {
  try {
    return await routeRequest(event);
  } catch (err) {
    console.error(err);

    if (err && err.name === "EmptyBucketError") {
      return buildResponseBody(204, {});
    }

    return buildResponseBody(500, err.message || "Unknown server error");
  }
};
//snippet-end:[javascript.v3.cross_service.supplement.list_widgets]
