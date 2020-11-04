const mockUpdateItem = jest.fn();
jest.mock("@aws-sdk/client-dynamodb/commands/GetUpdateCommand", () => ({
    DynamoDB: function DynamoDB() {
        this.UpdateItemCommand = mockUpdateItem;
    },
}));
const { run } = require("../../dynamodb/src/ddbdoc_update_item");

test("has to mock db#updateItem", async (done) => {
    await run();
    expect(mockUpdateItem).toHaveBeenCalled;
    done();
});
