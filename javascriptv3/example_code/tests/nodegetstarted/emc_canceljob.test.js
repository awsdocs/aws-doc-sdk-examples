const mockGetStarted = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/PutObjectCommand", () => ({
  S3: function S3() {
    this.PutObjectCommand = mockGetStarted;
  },
}));
const {  run } = require("../../nodegetstarted/sample");

//test function
test("has to mock getstarted#putobject", async (done) => {
  await run();
  expect(mockGetStarted).toHaveBeenCalled;
  done();
});
