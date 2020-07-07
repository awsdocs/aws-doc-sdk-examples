

const mockBatchGetItem = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/BatchGetItemCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.BatchGetItemCommand = mockBatchGetItem
    }
}));
const {params, run} = require("../../dynamodb/QueryExample/ddb_batchgetitem");

//test function
test("has to mock db#batchGetItem",  async (done) => {
    await run();
    expect(mockBatchGetItem).toHaveBeenCalled;
    done();
});
