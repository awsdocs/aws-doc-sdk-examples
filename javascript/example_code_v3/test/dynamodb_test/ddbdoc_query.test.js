const mockCreateBucket = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.createBucket = mockCreateBucket
    }
}));
const {bucketParams, run} = require("../../dynamodb/ddbdoc_query");

//test function
test("has to mock db#dbdoc_query",  async (done) => {
    await run();
    expect(mockCreateBucket).toHaveBeenCalled;
    done();
});
