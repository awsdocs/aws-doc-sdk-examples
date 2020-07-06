process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'TEMPLATE_NAME');

const mockUpdateTemplate = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/UpdateTemplateCommand', () => ({
  SES: function SES() {
    this.UpdateTemplateCommand = mockUpdateTemplate
  }
}));
const {run} = require("../../ses/ses_updatetemplate.js");

//test function
test("has to mock SES#updatetemplate",  async (done) => {
  await run();
  expect(mockUpdateTemplate).toHaveBeenCalled;
  done();
});
