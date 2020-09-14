describe("S3#deleteBucketWebsite", () => {

  const mockGetBucketPolicy = jest.fn();
  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.createBucket = mockGetBucketPolicy;
    },
  }));

  const { bucketParams, run } = require("../src/s3_createBucket");

  //test function
  test("has to mock S3#getBucketPolicy", async (done) => {
    await run();
    expect(mockGetBucketPolicy).toHaveBeenCalledWith(bucketParams);
    done();
  });
});
