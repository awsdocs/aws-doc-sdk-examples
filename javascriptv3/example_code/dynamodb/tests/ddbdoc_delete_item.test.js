import { mockClient } from "aws-sdk-client-mock";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

const ddbMock = mockClient(DynamoDBDocumentClient);

beforeEach(() => {
    ddbMock.reset();
});

import 'regenerator-runtime/runtime'
import { run } from "../src/ddbdoc_delete_item";
import { DeleteCommand } from "@aws-sdk/lib-dynamodb";

it("should delete an item from the DynamoDB table", async () => {
    ddbMock.on(DeleteCommand).resolves({
        Item: { id: "user1", name: "John" },
    });
    const data = await run("user1");
    expect(data.Item.name).toStrictEqual("John");
});