process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'FILTER');
process.argv.push('--arg3', 'LOG_GROUP');
const mockDeletSubFilter = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-logs/commands/DeleteSubscriptionFilterCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.DeleteSubscriptionFilterCommand = mockDeletSubFilter
    }
}));
const {params, run} = require("../../cloudwatch/cwl_deletesubscriptionfilter");

//test function
test("has to mock cloudwatch-logs#deletesubscriptionfilter",  async (done) => {
    await run();
    expect(mockDeletSubFilter).toHaveBeenCalled;
    done();
});
