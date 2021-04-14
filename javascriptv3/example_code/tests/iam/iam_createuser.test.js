const mockGetUser = jest.fn();
jest.mock("@aws-sdk/client-iam/commands/GetUserCommand", () => ({
  IAM: function IAM() {
    this.GetUserCommand = mockGetUser;
  },
}));
const { params, run } = require("../../iam/src/iam_createuser.js");

test("has to mock iam#getUser", async (done) => {
  await run();
  expect(mockGetUser).toHaveBeenCalled;
  done();
});
