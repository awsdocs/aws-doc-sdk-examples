process.argv.push('--arg1', 'us-west-2');
const mockDescribeAddresses = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/DescribeAddressesCommand', () => ({
    EC2: function EC2() {
        this.DescribeAddressesCommand = mockDescribeAddresses
    }
}));
const {params, run} = require("../../ec2/ec2_describeaddresses");

//test function
test("has to mock ec2#describeAddresses",  async (done) => {
    await run();
    expect(mockDescribeAddresses).toHaveBeenCalled;
    done();
});
