process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'BUCKET_NAME');
const mockDeleteBucketPolicy = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.deleteBucketPolicy = mockDeleteBucketPolicy
    }
}));
const {bucketParams, run} = require("../../s3/s3_deleteBucketPolicy");

//test function
test("has to mock S3#deleteBucketPolicy",  async (done) => {
    await run();
    expect(mockDeleteBucketPolicy).toHaveBeenCalled;
    done();
});
