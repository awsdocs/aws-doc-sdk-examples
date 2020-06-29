process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'TEMPLATE_NAME');

const mockGetTemplate = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/GetTemplateCommand', () => ({
    SES: function SES() {
        this.GetTemplateCommand = mockGetTemplate
    }
}));
const {run} = require("../../ses/ses_gettemplate");

//test function
test("has to mock SES#gettemplate",  async (done) => {
    await run();
    expect(mockGetTemplate).toHaveBeenCalled;
    done();
});
