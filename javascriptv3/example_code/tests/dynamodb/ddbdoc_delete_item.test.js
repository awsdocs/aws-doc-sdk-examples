const mockDeleteItem = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/DeleteItemCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.DeleteItemCommand = mockDeleteItem;
    },
}));
const { params, run } = require("../../dynamodb/src/ddb_deleteitem");

//test function
test("has to mock db#deleteItem", async (done) => {
    await run();
    expect(mockDeleteItem).toHaveBeenCalled;
    done();
});
