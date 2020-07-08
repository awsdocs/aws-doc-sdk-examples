const mockSendTemplateEmail = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/VerifyDomainIdentityCommand", () => ({
  SES: function SES() {
    this.VerifyDomainIdentityCommand = mockSendTemplateEmail;
  },
}));
const { run } = require("../../ses/ses_sendtemplatedemail.js");

//test function
test("has to mock SES#sendtemplatedemail", async (done) => {
  await run();
  expect(mockSendTemplateEmail).toHaveBeenCalled;
  done();
});
