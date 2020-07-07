

const mockCreateKeyPair = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/CreateKeyPairCommand', () => ({
    EC2: function EC2() {
        this.CreateKeyPairCommand = mockCreateKeyPair
    }
}));
const {params, run} = require("../../ec2/ec2_createkeypair");

//test function
test("has to mock ec2#createKeyPair",  async (done) => {
    await run();
    expect(mockCreateKeyPair).toHaveBeenCalled;
    done();
});
