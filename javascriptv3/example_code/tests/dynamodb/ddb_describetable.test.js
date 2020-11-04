const mockDesribeTable = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/DescribeTableCommand", () => ({
  DynamoDB: function DynamoDB() {
    this.DescribeTableCommand = mockDesribeTable;
  },
}));
const { params, run } = require("../../dynamodb/src/ddb_describetable");

test("has to mock db#describeTable", async (done) => {
  await run();
  expect(mockDesribeTable).toHaveBeenCalled;
  done();
});
