const mockVerifyDomainIdentity = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/VerifyDomainIdentityCommand", () => ({
  SES: function SES() {
    this.VerifyDomainIdentityCommand = mockVerifyDomainIdentity;
  },
}));
const { run } = require("../../ses/src/ses_verifydomainidentity.js");

test("has to mock SES#verifydomainidentity", async (done) => {
  await run();
  expect(mockVerifyDomainIdentity).toHaveBeenCalled;
  done();
});
