import { mockClient } from "aws-sdk-client-mock";
import { DynamoDBDocumentClient } from "@aws-sdk/lib-dynamodb";

const ddbMock = mockClient(DynamoDBDocumentClient);

beforeEach(() => {
    ddbMock.reset();
});

import 'regenerator-runtime/runtime'
import { run } from "../src/ddbdoc_delete_item";
import { DeleteCommand } from "@aws-sdk/lib-dynamodb";

it("should get user names from the DynamoDB", async () => {
    ddbMock.on(DeleteCommand).resolves({
        TableName: "DataTable"
    });
    const data = await run("DataTable");
    expect(data.TableName).toStrictEqual("DataTable");
});