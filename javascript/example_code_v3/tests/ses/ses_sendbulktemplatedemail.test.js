process.argv.push('--arg1', 'eu-west-1');

const mockListTemplates = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/ListTemplatesCommand', () => ({
  SES: function SES() {
    this.ListTemplatesCommand = mockListTemplates
  }
}));
const {run} = require("../../ses/ses_sendbulktemplatedemail.js");

//test function
test("has to mock SES#sendbulktemplatedemail",  async (done) => {
  await run();
  expect(mockListTemplates).toHaveBeenCalled;
  done();
});
