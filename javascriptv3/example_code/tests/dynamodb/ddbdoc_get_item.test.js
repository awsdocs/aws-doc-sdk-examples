const mockGetItem = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/GetItemCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.GetItemCommand = mockGetItem;
    },
}));
const { params, run } = require("../../dynamodb/src/ddb_getitem");

//test function
test("has to mock db#batchGetItem", async (done) => {
    await run();
    expect(mockGetItem).toHaveBeenCalled;
    done();
});
