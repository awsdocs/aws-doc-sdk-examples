

const mockPutRule = jest.fn();
jest.mock('@aws-sdk/client-cloudwatch-events/commands/PutRuleCommand', () => ({
    CloudWatch: function CloudWatch() {
        this.PutRuleCommand = mockPutRule
    }
}));
const {params, run} = require("../../cloudwatch/cwe_putrule");

//test function
test("has to mock cloudwatch-events#putrule",  async (done) => {
    await run();
    expect(mockPutRule).toHaveBeenCalled;
    done();
});
