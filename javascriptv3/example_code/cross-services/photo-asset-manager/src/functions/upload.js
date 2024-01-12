// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { randomUUID } from "node:crypto";

import {
  RESPONSE_HEADERS,
  createPresignedPutURL,
  withLogging,
} from "../common.js";
import { STORAGE_BUCKET_NAME } from "../lambda-env.js";

/**
 * Create a handler function.
 * @param {{ createPresignedPutURL: (bucket: string, key: string, contentType: string) => Promise<string>}} options
 */
export const getHandler = ({ createPresignedPutURL }) => {
  /**
   * Create a presigned URL that allows a user to upload an object
   * to Amazon Simple Storage Service (Amazon S3).
   * @type {import("@types/aws-lambda").APIGatewayProxyHandler}
   * @param {import("@types/aws-lambda").APIGatewayProxyEvent} event
   */
  return async (event) => {
    const { file_name } = JSON.parse(event.body);
    const prefix = randomUUID();
    const key = `${prefix}${file_name}`;
    console.log(`CREATED KEY: ${key}`);

    const presignedUrl = await createPresignedPutURL({
      bucket: STORAGE_BUCKET_NAME,
      key,
      contentType: "image/jpeg",
    });

    /**
     * @type {import("@types/aws-lambda").APIGatewayProxyResult}}
     */
    const output = {
      statusCode: 200,
      headers: RESPONSE_HEADERS,
      body: JSON.stringify({ url: presignedUrl }),
    };
    return output;
  };
};

export const handler = withLogging(getHandler({ createPresignedPutURL }));
