// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  S3Client,
  PutObjectCommand,
  GetObjectCommand,
} from "@aws-sdk/client-s3";
import { getSignedUrl } from "@aws-sdk/s3-request-presigner";
/**
 * Lodash is CJS, and tree-shaking doesn't work. Importing the flow module directly
 * reduces the bundle size.
 */
import flow from "lodash/flow.js";

/**
 * Creates a presigned URL for uploading an object to Amazon Simple Storage Service (Amazon S3).
 * @param {{ bucket: string, key: string, contentType }} object - The object to create a presigned URL for.
 */
export const createPresignedPutURL = ({ bucket, key, contentType }) => {
  const client = new S3Client({});
  // Expires in 15 minutes.
  const expiresIn = 15 * 60;
  const command = new PutObjectCommand({
    Bucket: bucket,
    Key: key,
    ContentType: contentType,
  });
  return getSignedUrl(client, command, { expiresIn });
};

/**
 * Create a presigned URL for downloading an object from Amazon S3.
 * @param {{ bucket: string, key: string }} object - The object to create a presigned URL for.
 */
export const createPresignedGetURL = ({ bucket, key }) => {
  const client = new S3Client({});
  // Expires in 24 hours.
  const expiresIn = 24 * 60 * 60;
  const command = new GetObjectCommand({
    Bucket: bucket,
    Key: key,
  });
  return getSignedUrl(client, command, { expiresIn });
};

export const RESPONSE_HEADERS = {
  "Access-Control-Allow-Origin": "*",
};

/**
 * Wraps a handler in a try/catch block and logs any errors.
 * @param {import("@types/aws-lambda").Handler} handler
 * @returns {import("@types/aws-lambda").Handler}
 */
export const withErrorLogging =
  (handler) =>
  async (...args) => {
    try {
      return await handler(...args);
    } catch (err) {
      console.error(err);
      throw err;
    }
  };

/**
 * Wraps a handler function that logs the event arguments.
 * @param {import("@types/aws-lambda").Handler} handler
 * @returns {import("@types/aws-lambda").Handler}
 */
export const withEventLogging =
  (handler) =>
  async (...args) => {
    console.log("INPUT: ", JSON.stringify(args));
    const result = await handler(...args);
    console.log("OUTPUT: ", JSON.stringify(result));
    return result;
  };

/**
 * Wraps a handler with logging and error handling.
 * @param {import("@types/aws-lambda").Handler} handler
 * @returns {import("@types/aws-lambda").Handler}
 */
export const withLogging = flow(withEventLogging, withErrorLogging);
