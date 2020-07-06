process.argv.push('--arg1', 'us-east-1');
process.argv.push('arg2', 'TABLE_NAME')
const mockPopulateTable = jest.fn();
jest.mock('@aws-sdk/client-ddb/commands/PutItemCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.PutItemCommand = mockPopulateTable
    }
}));
const {run} = require("../../lambda/tutorial/slotassets/ddb-table-populate");

//test function
test("has to mock ddb#populatetable",  async (done) => {
    await run();
    expect(mockPopulateTable).toHaveBeenCalled;
    done();
});
