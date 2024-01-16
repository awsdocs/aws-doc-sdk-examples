// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cross-service.lambda-from-browser.javascriptv3.lambda]
import { PutCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "./ddbDocClient.js";

/**
 * @param {{ Item: { Id: string, Color: string, Pattern: string }, TableName: string}} event
 */
export const handler = async (event) => {
  const params = {
    Item: {
      Id: event.Item.Id,
      Color: event.Item.Color,
      Pattern: event.Item.Pattern,
    },
    TableName: event.TableName,
  };

  try {
    console.log("Adding data to dynamodb...");
    const data = await ddbDocClient.send(new PutCommand(params));
    console.log("Added item:", JSON.stringify(data, null, 2));
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[cross-service.lambda-from-browser.javascriptv3.lambda]
