// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, GetCommand } from "@aws-sdk/lib-dynamodb";

export interface UserRepository {
  getUserInfoByEmail(
    userEmail: string,
  ): Promise<Record<string, unknown> | undefined>;
}

export class DynamoDBUserRepository implements UserRepository {
  private ddbDocClient: DynamoDBDocumentClient;
  private tableName: string;

  constructor(tableName: string) {
    const dynamoDBClient = new DynamoDBClient({});
    const ddbDocClient = DynamoDBDocumentClient.from(dynamoDBClient);
    this.ddbDocClient = ddbDocClient;
    this.tableName = tableName;
  }

  async getUserInfoByEmail(
    userEmail: string,
  ): Promise<Record<string, unknown> | undefined> {
    const getItemCommand = new GetCommand({
      TableName: this.tableName,
      Key: { UserEmail: userEmail },
    });

    const { Item } = await this.ddbDocClient.send(getItemCommand);
    return Item;
  }
}
