process.argv.push('--arg1', 'us-west-2');
process.argv.push('--arg2', 'LAMBDA_FUNCTION_ARN');
const mockPutTargets = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-events/commands/PutTargetsCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutTargetsCommand = mockPutTargets
    }
}));
const {params, run} = require("../../cloudwatch/cwe_puttargets");

//test function
test("has to mock cloudwatch-events#puttargets",  async (done) => {
    await run();
    expect(mockPutTargets).toHaveBeenCalled;
    done();
});
