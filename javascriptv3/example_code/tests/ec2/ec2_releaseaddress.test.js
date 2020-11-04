const mockReleaseAddressesCommand = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/ReleaseAddressCommand", () => ({
  EC2: function EC2() {
    this.ReleaseAddressCommand = mockReleaseAddressesCommand;
  },
}));
const { params, run } = require("../../ec2/src/ec2_releaseaddress");

test("has to mock ec2#releaseAddresses", async (done) => {
  await run();
  expect(mockReleaseAddressesCommand).toHaveBeenCalled;
  done();
});
