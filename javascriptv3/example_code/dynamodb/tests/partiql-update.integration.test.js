import { describe, it, expect } from "vitest";
import { DynamoDBClient, GetItemCommand } from "@aws-sdk/client-dynamodb";

import { main } from "../actions/partiql/partiql-update.js";
import { tableSetupTeardown } from "../libs/dynamodb-test.utils.js";

describe("partiql-update", () => {
  const tableName = "EyeColors";
  const client = new DynamoDBClient({});
  const starterValues = [
    { Color: { S: "blue" }, IsRecessive: { BOOL: "false" } },
  ];

  tableSetupTeardown(
    tableName,
    [
      {
        AttributeName: "Color",
        AttributeType: "S",
      },
    ],
    starterValues,
  );

  it("should mark blue eyes as recessive", async () => {
    const command = new GetItemCommand({
      TableName: tableName,
      Key: { Color: { S: "blue" } },
      ConsistentRead: true,
    });

    const before = await client.send(command);
    expect(before.Item).toEqual({
      Color: { S: "blue" },
      IsRecessive: { BOOL: false },
    });

    await main();

    const after = await client.send(command);
    expect(after.Item).toEqual({
      Color: { S: "blue" },
      IsRecessive: { BOOL: true },
    });
  });
});
