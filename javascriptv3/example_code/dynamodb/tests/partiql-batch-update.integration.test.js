import { describe, it, expect } from "vitest";
import { ScanCommand, DynamoDBClient } from "@aws-sdk/client-dynamodb";

import { main } from "../actions/partiql/partiql-batch-update.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-batch-update", () => {
  const tableName = "Eggs";
  const client = new DynamoDBClient({});
  const starterValues = [
    { Variety: { S: "chicken" }, Style: { S: "poached" } },
    { Variety: { S: "duck" }, Style: { S: "scrambled" } },
  ];

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Variety",
        AttributeType: "S",
      },
    ],
    starterValues,
  );

  it("should change the egg style", async () => {
    const command = new ScanCommand({
      TableName: tableName,
      ConsistentRead: true,
    });

    const before = await client.send(command);
    expect(before.Items).toEqual(expect.arrayContaining(starterValues));

    await main();

    const after = await client.send(command);
    expect(after.Items).toEqual(
      expect.arrayContaining([
        { Variety: { S: "chicken" }, Style: { S: "omelette" } },
        { Variety: { S: "duck" }, Style: { S: "fried" } },
      ]),
    );
  });
});
