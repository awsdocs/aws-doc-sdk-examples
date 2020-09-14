describe("S3#uploadtoBucket", () => {

  const mockPutObject = jest.fn();
  jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
      this.putObject = mockPutObject;
    },
  }));
  
  const { run } = require("../src/s3_upload");

  //test function
  test("has to mock S3#uploadtoBucket", async (done) => {
    await run();
    expect(mockPutObject).toHaveBeenCalled;
    done();
  });
});