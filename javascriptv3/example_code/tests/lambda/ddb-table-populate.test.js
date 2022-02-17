const mockPopulateTable = jest.fn();
jest.mock("@aws-sdk/client-ddb/commands/PutItemCommand", () => ({
  DynamoDB: function DynamoDB() {
    this.PutItemCommand = mockPopulateTable;
  },
}));
const { run } = require("../../lambda/tutorial/slotassets/ddb-table-populate");

test("has to mock ddb#populatetable", async (done) => {
  await run();
  expect(mockPopulateTable).toHaveBeenCalled;
  done();
});
