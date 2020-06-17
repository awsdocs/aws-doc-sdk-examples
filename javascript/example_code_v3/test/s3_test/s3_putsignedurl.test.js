process.argv.push('--arg1', 'BUCKET_NAME');
process.argv.push('--arg2', 'REGION');
process.argv.push('--arg3', 'KEY');
process.argv.push('--arg4', 'BODY');
const mockGetSignedURL = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.putObject = mockGetSignedURL
    }
}));
const {bucketParams, run} = require("../../s3/s3_putsignedurl");

//test function
test("has to mock S3#presignedURL",  async (done) => {
    await run();
    expect(mockGetSignedURL).toHaveBeenCalled;
    done();
});
