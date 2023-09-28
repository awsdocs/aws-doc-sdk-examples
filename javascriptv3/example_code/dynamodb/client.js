/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[dynamodb.JavaScript.tables.createclientv3]
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";

export const client = new DynamoDBClient({ region: "us-east-1" });
// snippet-end:[dynamodb.JavaScript.tables.createclientv3]
