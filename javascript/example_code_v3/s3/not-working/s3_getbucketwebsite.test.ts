describe("S3#getBucketWebsite", () => {
  const mockGetBucketWebsite = jest.fn();
  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.GetBucketWebsiteCommand = mockGetBucketWebsite;
    },
    GetBucketWebsiteCommand: () => {}
  }));

  //test functions
  test("has to mock S3#getBucketWebsite", async (done) => {
    const { run } = require("../src/s3_getbucketwebsite");
    await run();
    expect(mockGetBucketWebsite).toHaveBeenCalled;
    done();
  });
});