describe("S3#uploadObject", () => {

  const mockUploadObject = jest.fn();
  jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
    S3: function S3() {
      this.PutObjectCommand = mockUploadObject;
    },
  }));
  const { run } = require("../src/s3_upload_putcommand");

  //test function
  test("has to mock S3#uploadObject", async (done) => {
    await run();
    expect(mockUploadObject).toHaveBeenCalled;
    done();
  });
});