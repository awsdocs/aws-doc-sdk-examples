import { mockClient } from "aws-sdk-client-mock";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

const ddbMock = mockClient(DynamoDBDocumentClient);

beforeEach(() => {
    ddbMock.reset();
});

import 'regenerator-runtime/runtime'
import { run } from "../src/ddbdoc_query_item";
import { QueryCommand } from "@aws-sdk/lib-dynamodb";

it("should query the DynamoDB table", async () => {
    ddbMock.on(QueryCommand).resolves({
        Item: { id: "user1", name: "John" },
    });
    const names = await run("DataTable");
    expect(names.Item.name).toStrictEqual("John");
});

