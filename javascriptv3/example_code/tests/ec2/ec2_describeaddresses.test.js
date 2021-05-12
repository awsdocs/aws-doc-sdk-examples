const mockDescribeAddresses = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/DescribeAddressesCommand", () => ({
  EC2: function EC2() {
    this.DescribeAddressesCommand = mockDescribeAddresses;
  },
}));
const { params, run } = require("../../ec2/src/ec2_describeaddresses");

test("has to mock ec2#describeAddresses", async (done) => {
  await run();
  expect(mockDescribeAddresses).toHaveBeenCalled;
  done();
});
