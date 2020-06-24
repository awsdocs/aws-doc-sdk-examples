process.argv.push('--arg1', 'REGION');
process.argv.push('--arg2', 'ON');
process.argv.push('--arg2', 'INSTANCE_ID');
const mockMonitorInstancesCommand = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/MonitorInstancesCommand', () => ({
    EC2: function EC2() {
        this.MonitorInstancesCommand = mockMonitorInstancesCommand
    }
}));
const {params, run} = require("../../ec2/ec2_monitorinstances");

//test function
test("has to mock ec2#monitorInstances",  async (done) => {
    await run();
    expect(mockMonitorInstancesCommand).toHaveBeenCalled;
    done();
});
