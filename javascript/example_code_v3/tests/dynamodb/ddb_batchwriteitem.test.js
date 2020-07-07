
const mockBatchGetItem = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/BatchWriteItemCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.BatchWriteItemCommand = mockBatchGetItem
    }
}));
const {params, run} = require("../../dynamodb/QueryExample/ddb_batchwriteitem");

//test function
test("has to mock db#batchWriteItem",  async (done) => {
    await run();
    expect(mockBatchGetItem).toHaveBeenCalled;
    done();
});
