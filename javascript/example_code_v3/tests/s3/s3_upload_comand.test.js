




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
