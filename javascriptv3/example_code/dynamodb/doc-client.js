// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.JavaScript.tables.createdocclientv3]
import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

export const docClient = DynamoDBDocumentClient.from(new DynamoDBClient({}));
// snippet-end:[dynamodb.JavaScript.tables.createdocclientv3]
