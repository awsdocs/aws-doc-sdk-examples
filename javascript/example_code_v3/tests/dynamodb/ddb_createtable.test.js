process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'TABLE_NAME');
const mockCreateTable = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/CreateTableCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.CreateTableCommand = mockCreateTable
    }
}));
const {params, run} = require("../../dynamodb/ddb_createtable");

//test function
test("has to mock db#createTable",  async (done) => {
    await run();
    expect(mockCreateTable).toHaveBeenCalled;
    done();
});
