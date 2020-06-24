process.argv.push('--arg1', 'REGION');
process.argv.push('--arg2', 'ALLOCATION_ID');
const mockReleaseAddressesCommand = jest.fn();
jest.mock('@aws-sdk/client-ec2/commands/ReleaseAddressCommand', () => ({
    EC2: function EC2() {
        this.ReleaseAddressCommand = mockReleaseAddressesCommand
    }
}));
const {params, run} = require("../../ec2/ec2_releaseaddress");

//test function
test("has to mock ec2#releaseAddresses",  async (done) => {
    await run();
    expect(mockReleaseAddressesCommand).toHaveBeenCalled;
    done();
});
