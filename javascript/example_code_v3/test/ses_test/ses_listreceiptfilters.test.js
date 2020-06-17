process.argv.push('--arg1', 'eu-west-1');

const mockListReceiptFilters = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/ListReceiptFiltersCommand', () => ({
    SES: function SES() {
        this.ListReceiptFiltersCommand = mockListReceiptFilters
    }
}));
const {run} = require("../../ses/ses_listreceiptfilters.js");

//test function
test("has to mock SES#listreceiptfilters",  async (done) => {
    await run();
    expect(mockListReceiptFilters).toHaveBeenCalled;
    done();
});
