const mockCreateTable = jest.fn();
jest.mock("@aws-sdk/client-ddb/commands/CreateTableCommand", () => ({
  DynamoDB: function DynamoDB() {
    this.CreateTableCommand = mockCreateTable;
  },
}));
const { run } = require("../../lambda/tutorial/slotassets/ddb-table-create");

test("has to mock ddb#createtable", async (done) => {
  await run();
  expect(mockCreateTable).toHaveBeenCalled;
  done();
});
