process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'TABLE_NAME');
const mockDeleteItem = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/DeleteItemCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.DeleteItemCommand = mockDeleteItem
    }
}));
const {params, run} = require("../../dynamodb/ddb_deleteitem");

//test function
test("has to mock db#deleteItem",  async (done) => {
    await run();
    expect(mockDeleteItem).toHaveBeenCalled;
    done();
});
