process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg1', 'us-west-2');
const mockEnableAlarmActions = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch/commands/PutMetricAlarmCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutMetricAlarmCommand = mockEnableAlarmActions
    }
}));
const {params, run} = require("../../cloudwatch/cw_enablealarmactions");

//test function
test("has to mock cloudwatch#enablealarmactions",  async (done) => {
    await run();
    expect(mockEnableAlarmActions).toHaveBeenCalled;
    done();
});
