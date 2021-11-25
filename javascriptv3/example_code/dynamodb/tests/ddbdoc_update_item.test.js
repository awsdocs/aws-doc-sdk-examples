import { mockClient } from "aws-sdk-client-mock";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

const ddbMock = mockClient(DynamoDBDocumentClient);

beforeEach(() => {
    ddbMock.reset();
});

import 'regenerator-runtime/runtime'
import { run } from "../src/ddbdoc_update_item";
import { UpdateCommand } from "@aws-sdk/lib-dynamodb";

it("should get user names from the DynamoDB", async () => {
    ddbMock.on(UpdateCommand).resolves({
        Item: { id: "user1", name: "John" },
    });
    const names = await run("DataTable");
    expect(names.Item.name).toStrictEqual("John");
});

