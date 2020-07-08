const mockDeleteAccessKey = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/DeleteAccessKeyCommand", () => ({
  IAM: function IAM() {
    this.DeleteAccessKeyCommand = mockDeleteAccessKey;
  },
}));
const { params, run } = require("../../iam/iam_deleteaccesskey.js");

//test function
test("has to mock iam#deleteaccesskey", async (done) => {
  await run();
  expect(mockDeleteAccessKey).toHaveBeenCalled;
  done();
});
