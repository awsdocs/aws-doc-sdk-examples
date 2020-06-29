process.argv.push('--arg1', 'eu-west-1');

const mockListIdentities = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/ListIdentitiesCommand', () => ({
    SES: function SES() {
        this.ListIdentitiesCommand = mockListIdentities
    }
}));
const {run} = require("../../ses/ses_listidentities.js");

//test function
test("has to mock SES#listidentities",  async (done) => {
    await run();
    expect(mockListIdentities).toHaveBeenCalled;
    done();
});
