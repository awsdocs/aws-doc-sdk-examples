process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'BUCKET_NAME');
const mockDeleteBucketWebsite = jest.fn();
jest.mock('@aws-sdk/client-s3/commands/DeleteBucketWebsiteCommand', () => ({
    S3: function S3() {
        this.DeleteBucketWebsiteCommand = mockDeleteBucketWebsite
    }
}));
const {bucketParams, run} = require("../../s3/s3_deletebucketwebsite");

//test function
test("has to mock S3#deleteBucketWebsite",  async (done) => {
    await run();
    expect(mockDeleteBucketWebsite).toHaveBeenCalled;
    done();
});
