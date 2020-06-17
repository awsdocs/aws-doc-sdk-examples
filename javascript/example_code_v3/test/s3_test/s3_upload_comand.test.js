process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'BUCKET_NAME');
process.argv.push('--arg3', 'KEY');
process.argv.push('--arg4', 'BODY');
const mockUploadObject = jest.fn();
jest.mock('@aws-sdk/client-s3/commands/PutObjectCommand', () => ({
    S3: function S3() {
        this.PutObjectCommand = mockCreateBucket
    }
}));
const {bucketParams, run} = require("../../s3/s3_setbucketpolicy");

//test function
test("has to mock S3#uploadObject",  async (done) => {
    await run();
    expect(mockUploadObject).toHaveBeenCalled;
    done();
});
