process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'AMBDA_FUNCTION_ARN');
process.argv.push('--arg2', 'FILTER_NAME');
process.argv.push('--arg2', 'LOG_GROUP');
const mockPutSubFilter = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-logs/commands/PutSubscriptionFilterCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutSubscriptionFilterCommand = mockPutSubFilter
    }
}));
const {params, run} = require("../../cloudwatch/cwl_putsubscriptionfilter");

//test function
test("has to mock cloudwatch-logs#putsubscriptionfilter",  async (done) => {
    await run();
    expect(mockPutSubFilter).toHaveBeenCalled;
    done();
});
