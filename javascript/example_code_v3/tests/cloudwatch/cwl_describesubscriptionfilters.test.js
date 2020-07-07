

const mockDescribeSubFilters = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-logs/commands/DescribeSubscriptionFiltersCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.DescribeSubscriptionFiltersCommand = mockDescribeSubFilters
    }
}));
const {params, run} = require("../../cloudwatch/cwl_describesubscriptionfilters");

//test function
test("has to mock cloudwatch-logs#describesubscriptionfilters",  async (done) => {
    await run();
    expect(mockDescribeSubFilters).toHaveBeenCalled;
    done();
});
