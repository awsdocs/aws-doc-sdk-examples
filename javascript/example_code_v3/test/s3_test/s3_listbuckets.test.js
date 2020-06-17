process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'BUCKET_NAME');
const mockListBuckets = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.listBuckets = mockListBuckets
    }
}));
const {bucketParams, run} = require("../../s3/s3_listbuckets");

//test function
test("has to mock S3#listBuckets",  async (done) => {
    await run();
    expect(mockListBuckets).toHaveBeenCalledWith();
    done();
});
