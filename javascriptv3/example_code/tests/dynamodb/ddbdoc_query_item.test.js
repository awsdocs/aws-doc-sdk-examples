const mockQuery = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/QueryCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.QueryCommand = mockQuery;
    },
}));
const { params, run } = require("../../dynamodb/src/ddb_query");

test("has to mock db#Query", async (done) => {
    await run();
    expect(mockQuery).toHaveBeenCalled;
    done();
});
