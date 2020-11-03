const mockListObjects = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
  S3: function S3() {
    this.listObjects = mockListObjects;
  },
}));
const { bucketParams, run } = require("../../s3/s3_listObjects");

test("has to mock S3#listObjects", async (done) => {
  await run();
  expect(mockListObjects).toHaveBeenCalledWith(bucketParams);
  done();
});
