
const mockDescribeInstances = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/DescribeInstancesCommand', () => ({
    EC2: function EC2() {
        this.DescribeInstancesCommand = mockDescribeInstances
    }
}));
const {params, run} = require("../../ec2/ec2_describeinstances");

//test function
test("has to mock ec2#describeInstances",  async (done) => {
    await run();
    expect(mockDescribeInstances).toHaveBeenCalled;
    done();
});
