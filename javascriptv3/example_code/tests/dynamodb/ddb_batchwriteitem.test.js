const mockBatchWriteItem = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/BatchWriteItemCommand", () => ({
  DynamoDB: function DynamoDB() {
    this.BatchWriteItemCommand = mockBatchWriteItem;
  },
}));
const { params, run } = require("../../dynamodb/src/ddb_batchwriteitem");

test("has to mock db#batchWriteItem", async (done) => {
  await run();
  expect(mockBatchWriteItem).toHaveBeenCalled;
  done();
});
