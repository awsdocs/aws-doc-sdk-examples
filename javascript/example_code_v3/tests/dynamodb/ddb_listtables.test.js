process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'TABLE_NAME');
const mockListTables = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/ListTablesCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.ListTablesCommand = mockListTables
    }
}));
const {params, run} = require("../../dynamodb/ddb_listtables");

//test function
test("has to mock db#listTables",  async (done) => {
    await run();
    expect(mockListTables).toHaveBeenCalled;
    done();
});
