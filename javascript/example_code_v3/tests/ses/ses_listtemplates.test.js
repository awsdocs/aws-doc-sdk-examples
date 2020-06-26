process.argv.push('--arg1', 'eu-west-1');
process.argv.push('--arg2', 'ITEMS_COUNT');

const mockListTemplates = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/ListTemplatesCommand', () => ({
    SES: function SES() {
        this.ListTemplatesCommand = mockListTemplates
    }
}));
const {run} = require("../../ses/ses_listtemplates.js");

//test function
test("has to mock SES#ses_listtemplates",  async (done) => {
    await run();
    expect(mockListTemplates).toHaveBeenCalled;
    done();
});
