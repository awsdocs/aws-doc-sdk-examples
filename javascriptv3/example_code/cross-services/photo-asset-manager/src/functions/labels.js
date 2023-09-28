/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, paginateScan } from "@aws-sdk/lib-dynamodb";

import { LABELS_TABLE_NAME } from "../lambda-env.js";
import { RESPONSE_HEADERS, withLogging } from "../common.js";

/**
 * @typedef {Record<string, number} Labels - e.g { "Person": 10, "Beverage": 9 }
 */

/**
 * Unmarshall the Amazon DynamoDB items into a response object.
 * @param {Labels} labels
 * @param {{ Label: string, Count: number }} item
 * @returns {Labels}
 */
export const labelsReducer = (labels, item) => {
  labels[item.Label] = { count: item.Count };
  return labels;
};

/**
 * Get labels from a database table.
 * @returns {Promise<Labels>}
 */
const getLabels = async () => {
  const client = new DynamoDBClient({});
  const ddbDocClient = DynamoDBDocumentClient.from(client);

  /**
   * A DynamoDB "Scan" returns all of the items in a table. If there are many items,
   * the results might be paginated. The paginateScan command is a convenience function
   * that wraps the ScanCommand in a generator function.
   */
  const paginator = paginateScan(
    {
      client: ddbDocClient,
    },
    {
      TableName: LABELS_TABLE_NAME,
    },
  );

  const items = [];
  for await (const page of paginator) {
    items.push(...page.Items);
  }

  return items.reduce(labelsReducer, {});
};

/**
 * Create a handler function.
 * @param {{ getLabels: (tableName) => Promise<Record<string, number>> }} options
 */
export const getHandler = ({ getLabels }) => {
  /**
   * Fetch labels from a database table and return them.
   * @type {import("@types/aws-lambda").APIGatewayProxyHandler}
   */
  return async () => {
    console.log("GETTING LABELS...");
    const labels = await getLabels();
    console.log("LABELS: ", labels);

    /**
     * @type {import("@types/aws-lambda").APIGatewayProxyResult}}
     */
    const output = {
      statusCode: 200,
      headers: RESPONSE_HEADERS,
      body: JSON.stringify({ labels }),
    };
    return output;
  };
};

export const handler = withLogging(getHandler({ getLabels }));
