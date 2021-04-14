const mockListTables = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/ListTablesCommand", () => ({
  DynamoDB: function DynamoDB() {
    this.ListTablesCommand = mockListTables;
  },
}));
const { params, run } = require("../../dynamodb/src/ddb_listtables");

test("has to mock db#listTables", async (done) => {
  await run();
  expect(mockListTables).toHaveBeenCalled;
  done();
});
