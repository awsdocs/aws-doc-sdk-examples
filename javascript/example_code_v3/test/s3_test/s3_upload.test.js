process.argv.push('--arg1', 'BUCKET_NAME');
process.argv.push('--arg2', 'KEY');
process.argv.push('--arg3', 'BODY');
process.argv.push('--arg4', 'FILE_NAME');
process.argv.push('--arg5', 'eu-west-1');
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
