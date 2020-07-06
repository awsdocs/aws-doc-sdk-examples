process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'TABLE_NAME');
const mockDeleteTable = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/DeleteTableCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.DeleteTableCommand = mockDeleteTable
    }
}));
const {params, run} = require("../../dynamodb/ddb_deletetable");

//test function
test("has to mock db#deleteTable",  async (done) => {
    await run();
    expect(mockDeleteTable).toHaveBeenCalled;
    done();
});
