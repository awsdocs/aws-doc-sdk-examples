const mockCreateAccessKey = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/CreateAccessKeyCommand", () => ({
  IAM: function IAM() {
    this.CreateAccessKeyCommand = mockCreateAccessKey;
  },
}));
const { params, run } = require("../../iam/src/iam_createaccesskeys.js");

test("has to mock iam#createAccessKeys", async (done) => {
  await run();
  expect(mockCreateAccessKey).toHaveBeenCalled;
  done();
});
