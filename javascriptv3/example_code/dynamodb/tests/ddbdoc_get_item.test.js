import { mockClient } from "aws-sdk-client-mock";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

const ddbMock = mockClient(DynamoDBDocumentClient);

beforeEach(() => {
    ddbMock.reset();
});

import 'regenerator-runtime/runtime'
import { run } from "../src/ddbdoc_get_item";
import { GetCommand } from "@aws-sdk/lib-dynamodb";

it("should get an item from a DynamoDB table", async () => {
    ddbMock.on(GetCommand).resolves({
        Item: { id: "user1", name: "John" },
    });
    const names = await run("user1");
    expect(names.Item.name).toStrictEqual("John");
});