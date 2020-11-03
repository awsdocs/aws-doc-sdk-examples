const mockDeleteIdentity = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/DeleteIdentityCommand", () => ({
  SES: function SES() {
    this.DeleteIdentityCommand = mockDeleteIdentity;
  },
}));
const { run } = require("../../ses/src/ses_deleteidentity.js");

test("has to mock SES#createRecepiptFilter", async (done) => {
  await run();
  expect(mockDeleteIdentity).toHaveBeenCalled;
  done();
});
