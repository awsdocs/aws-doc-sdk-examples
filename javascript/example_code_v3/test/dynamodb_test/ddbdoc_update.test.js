const mockCreateBucket = jest.fn();
jest.mock('@aws-sdk/client-dynamodb/commands/UpdateTableCommand', () => ({
    DynamoDB: function DynamoDB() {
        this.UpdateTableCommand = mockCreateBucket
    }
}));
const {bucketParams, run} = require("../../dynamodb/ddbdoc_update");

//test function
test("has to mock db#dbdoc_scan",  async (done) => {
    await run();
    expect(mockCreateBucket).toHaveBeenCalled;
    done();
});
