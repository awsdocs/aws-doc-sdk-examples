process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'RECEIVER_ADDRESS');
process.argv.push('--arg3', 'SENDER_ADDRESS');

const mockSendEmail = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/SendEmailCommand', () => ({
    SES: function SES() {
        this.SendEmailCommand = mockSendEmail
    }
}));
const {run} = require("../../ses/ses_sendemail.js");

//test function
test("has to mock SES#sendemail",  async (done) => {
    await run();
    expect(mockSendEmail).toHaveBeenCalled;
    done();
});
