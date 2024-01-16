// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { AttributeType, Table } from "aws-cdk-lib/aws-dynamodb";
import { Construct } from "constructs";

export interface AppDatabaseProps {}

export class AppDatabase extends Construct {
  static readonly KEY = "comment_key";
  static readonly INDEX = "sentiment";
  table: Table;

  constructor(scope: Construct, {}: AppDatabaseProps = {}) {
    super(scope, "ddb");
    this.table = new Table(this, "Comments", {
      partitionKey: { name: AppDatabase.KEY, type: AttributeType.STRING },
      sortKey: {
        name: AppDatabase.INDEX,
        type: AttributeType.STRING,
      },
    });
  }
}
