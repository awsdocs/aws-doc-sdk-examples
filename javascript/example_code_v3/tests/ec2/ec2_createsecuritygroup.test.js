process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'MY_KEY_PAIR');
process.argv.push('--arg3', 'SECURITY_GROUP_NAME');
process.argv.push('--arg4', 'SECURITY_GROUP_ID');
const mockDeleteKeyPair = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/DeleteKeyPairCommand', () => ({
    EC2: function EC2() {
        this.DeleteKeyPairCommand = mockDeleteKeyPair
    }
}));
const {params, run} = require("../../ec2/ec2_deletekeypair");

//test function
test("has to mock ec2#deleteKeyPair",  async (done) => {
    await run();
    expect(mockDeleteKeyPair).toHaveBeenCalled;
    done();
});
