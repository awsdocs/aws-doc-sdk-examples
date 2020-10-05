const mockDeleteBucket = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
  S3: function S3() {
    this.deleteBucket = mockDeleteBucket;
  },
}));
const { bucketParams, run } = require("../../s3/s3_deleteBucket");

//test function
test("has to mock S3#deleteBucket", async (done) => {
  await run();
  expect(mockDeleteBucket).toHaveBeenCalledWith(bucketParams);
  done();
});
