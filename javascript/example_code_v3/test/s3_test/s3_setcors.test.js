process.argv.push('--arg1', 'BUCKET_NAME');
process.argv.push('--arg2', 'eu-west-1');
const mockSetCors = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.putBucketCors = mockSetCors
    }
}));
const {corsParams, run} = require("../../s3/s3_setcors");

//test function
test("has to mock S3#setCors",  async (done) => {
    await run();
    expect(mockSetCors).toHaveBeenCalled;
    done();
});
