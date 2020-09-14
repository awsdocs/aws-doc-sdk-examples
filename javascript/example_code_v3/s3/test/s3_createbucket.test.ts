
describe("S3#createBucket", () => {
  const mockCreateBucket = jest.fn();
  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.createBucket = mockCreateBucket;
    },
  }));

  const { bucketParams, run } = require("../src/s3_createBucket");

  //test function
  test("has to mock S3#createBucket", async (done) => {  
    await run();
    expect(mockCreateBucket).toHaveBeenCalledWith(bucketParams);
    done();
  });
});