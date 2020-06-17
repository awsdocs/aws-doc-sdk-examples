process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'BUCKET_NAME');
const mockUploadObject = jest.fn();
jest.mock('@aws-sdk/client-s3/commands/PutBucketPolicyCommand', () => ({
    S3: function S3() {
        this.PutBucketPolicyCommand = mockUploadObject
    }
}));
const {uploadParams, run} = require("../../s3/s3_setbucketpolicy");

//test function
test("has to mock S3#setBucketPolicy",  async (done) => {
    await run();
    expect(mockUploadObject).toHaveBeenCalled;
    done();
});
