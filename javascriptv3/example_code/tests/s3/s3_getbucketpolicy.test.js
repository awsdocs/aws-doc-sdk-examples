const mockCreateBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
  S3: function S3() {
    this.createBucket = mockCreateBucket;
  },
}));
const { bucketParams, run } = require("../../s3/s3_createBucket");

test("has to mock S3#getBucketPolicy", async (done) => {
  await run();
  expect(mockCreateBucket).toHaveBeenCalledWith(bucketParams);
  done();
});
