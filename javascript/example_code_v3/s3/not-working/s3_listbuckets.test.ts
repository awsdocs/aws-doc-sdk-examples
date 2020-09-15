describe("S3#listBuckets", () => {

  const mockListBuckets = jest.fn();
  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.listBuckets = mockListBuckets;
    },
  }));

  const { run } = require("../src/s3_listbuckets");
  
  test("has to mock S3#listBuckets", async (done) => {
    await run();
    expect(mockListBuckets).toHaveBeenCalled;
    done();
  });
});