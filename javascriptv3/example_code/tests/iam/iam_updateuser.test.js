const mockUpdateUser = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/UpdateUserCommand", () => ({
  IAM: function IAM() {
    this.UpdateUserCommand = mockUpdateUser;
  },
}));
const { params, run } = require("../../iam/src/iam_updateuser.js");

test("has to mock iam#updateuser", async (done) => {
  await run();
  expect(mockUpdateUser).toHaveBeenCalled;
  done();
});
