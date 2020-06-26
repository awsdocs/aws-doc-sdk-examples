process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'RECEIVER_ADDRESS');
process.argv.push('--arg3', 'SENDER_ADDRESS');
process.argv.push('--arg4', 'TEMPLATE_NAME');

const mockSendTemplateEmail = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/VerifyDomainIdentityCommand', () => ({
  SES: function SES() {
    this.VerifyDomainIdentityCommand = mockSendTemplateEmail
  }
}));
const {run} = require("../../ses/ses_sendtemplatedemail.js");

//test function
test("has to mock SES#sendtemplatedemail",  async (done) => {
  await run();
  expect(mockSendTemplateEmail).toHaveBeenCalled;
  done();
});
