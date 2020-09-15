describe("S3#deleteBucketWebsite", () => {

  const mockDeleteBucketWebsite = jest.fn();
  jest.mock("@aws-sdk/client-s3/commands/DeleteBucketWebsiteCommand", () => ({
    S3: function S3() {
      this.DeleteBucketWebsiteCommand = mockDeleteBucketWebsite;
    },
  }));

  const { run } = require("../src/s3_deletebucketwebsite");

  //test function
  test("has to mock S3#deleteBucketWebsite", async (done) => {
    await run();
    expect(mockDeleteBucketWebsite).toHaveBeenCalled;
    done();
  });
});