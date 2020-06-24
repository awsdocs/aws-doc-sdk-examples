process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'AMI_ID');
process.argv.push('--arg2', 'KEY_PAIR_NAME');
const mockCreateInstances = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/CreateKeyPairCommand', () => ({
    EC2: function EC2() {
        this.CreateKeyPairCommand = mockCreateInstances
    }
}));
const {params, run} = require("../../ec2/ec2_createinstances");

//test function
test("has to mock ec2#createInstances",  async (done) => {
    await run();
    expect(mockCreateInstances).toHaveBeenCalled;
    done();
});
