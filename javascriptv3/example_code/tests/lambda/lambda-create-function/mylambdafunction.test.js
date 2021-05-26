const mockCreateTable = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/CreateTableCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.CreateTableCommand = mockCreateTable;
    },
}));
import { run } from "../../../lambda/lambda_create_function/src/mylamdbafunction";

test("has to mock dynamodb#createtable", async (done) => {
    await run();
    expect(mockCreateTable).toHaveBeenCalled;
    done();
});
