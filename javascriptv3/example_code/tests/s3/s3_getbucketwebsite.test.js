const mockGetBucketWebsite = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/GetBucketWebsiteCommand", () => ({
  S3: function S3() {
    this.GetBucketWebsiteCommand = mockGetBucketWebsite;
  },
}));
const { bucketParams, run } = require("../../s3/s3_getbucketwebsite");
test("has to mock S3#getBucketWebsite", async (done) => {
  await run();
  expect(mockGetBucketWebsite).toHaveBeenCalled;
  done();
});
