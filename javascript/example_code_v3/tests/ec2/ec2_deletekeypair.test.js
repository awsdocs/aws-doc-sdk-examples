process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'KEY_PAIR_NAME');
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
