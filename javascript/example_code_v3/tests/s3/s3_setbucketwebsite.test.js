



const mockSetBucketWebSite = jest.fn();
jest.mock('@aws-sdk/client-s3/commands/PutBucketWebsiteCommand', () => ({
    S3: function S3() {
        this.PutBucketWebsiteCommand = mockSetBucketWebSite
    }
}));
const {staticHostParams, run} = require("../../s3/s3_setbucketwebsite");

//test function
test("has to mock S3#setBucketWebSite",  async (done) => {
    await run();
    expect(mockSetBucketWebSite).toHaveBeenCalled;
    done();
});
