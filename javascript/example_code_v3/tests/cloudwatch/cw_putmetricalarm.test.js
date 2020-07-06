process.argv.push('--arg1', 'us-west-2');
const mockPutMetricAlarm = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch/commands/PutMetricAlarmCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutMetricAlarmCommand = mockPutMetricAlarm
    }
}));
const {params, run} = require("../../cloudwatch/cw_putmetricalarm");

//test function
test("has to mock cloudwatch#putmetricalarm",  async (done) => {
    await run();
    expect(mockPutMetricAlarm).toHaveBeenCalled;
    done();
});
