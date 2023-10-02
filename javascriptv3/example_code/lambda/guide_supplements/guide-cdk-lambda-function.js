/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

//snippet-start:[javascript.v3.cross_service.supplement.list_widgets]
import { S3Client, ListObjectsCommand } from "@aws-sdk/client-s3";

// The following code uses the AWS SDK for JavaScript (v3).
// For more information, see https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html.
const s3Client = new S3Client({});

/**
 * @param {string} bucketName
 */
const listObjectNames = async (bucketName) => {
  const command = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await s3Client.send(command);

  if (!Contents.length) {
    const err = new Error(`No objects found in ${bucketName}`);
    err.name = "EmptyBucketError";
    throw err;
  }

  // Map the response to a list of strings representing the keys of the Amazon Simple Storage Service (Amazon S3) objects.
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

  const error = new Error(
    `Unimplemented HTTP method: ${lambdaEvent.httpMethod}`,
  );
  error.name = "UnimplementedHTTPMethodError";
  throw error;
};

const handleGetRequest = async () => {
  if (process.env.BUCKET === "undefined") {
    const err = new Error(`No bucket name provided.`);
    err.name = "MissingBucketName";
    throw err;
  }

  const objects = await listObjectNames(process.env.BUCKET);
  return buildResponseBody(200, objects);
};

/**
 * @typedef {{statusCode: number, body: string, headers: Record<string, string> }} LambdaResponse
 */

/**
 *
 * @param {number} status
 * @param {Record<string, string>} headers
 * @param {Record<string, unknown>} body
 *
 * @returns {LambdaResponse}
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

    if (err.name === "MissingBucketName") {
      return buildResponseBody(400, err.message);
    }

    if (err.name === "EmptyBucketError") {
      return buildResponseBody(204, []);
    }

    if (err.name === "UnimplementedHTTPMethodError") {
      return buildResponseBody(400, err.message);
    }

    return buildResponseBody(500, err.message || "Unknown server error");
  }
};
//snippet-end:[javascript.v3.cross_service.supplement.list_widgets]
