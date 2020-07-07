

const mockgetCors = jest.fn();
jest.mock('@aws-sdk/client-s3/commands/GetBucketCorsCommand', () => ({
    S3: function S3() {
        this.GetBucketCorsCommand = mockgetCors
    }
}));
const {bucketParams, run} = require("../../s3/s3_getcors");

//test function
test("has to mock S3#getCors",  async (done) => {
    await run();
    expect(mockgetCors).toHaveBeenCalled;
    done();
});
