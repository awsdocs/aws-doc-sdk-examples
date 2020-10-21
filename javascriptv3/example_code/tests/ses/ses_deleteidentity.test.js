const mockDeleteIdentity = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/DeleteIdentityCommand", () => ({
  SES: function SES() {
    this.DeleteIdentityCommand = mockDeleteIdentity;
  },
}));
const { run } = require("../../ses/src/ses_deleteidentity.js");

//test function
test("has to mock SES#createReceiptFilter", async (done) => {
  await run();
  expect(mockDeleteIdentity).toHaveBeenCalled;
  done();
});
