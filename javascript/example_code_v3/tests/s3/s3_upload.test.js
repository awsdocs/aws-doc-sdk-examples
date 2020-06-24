process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'BUCKET_NAME');
process.argv.push('--arg3', 'index.html');
process.argv.push('--arg4', 'test content');
process.argv.push('--arg5', 'index.html');

const mockPutObject = jest.fn();
jest.mock('@aws-sdk/client-s3', () => ({
    S3: function S3() {
        this.putObject = mockPutObject
    }
}));
const { uploadParams, file, path, run} = require("../../s3/s3_upload");

//test function
test("has to mock S3#uploadtoBucket",  async (done) => {
    await run();
    expect(mockPutObject).toHaveBeenCalled;
    done();
});
