const mockAllocateAddresses = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/AllocateAddressCommand", () => ({
  EC2: function EC2() {
    this.AllocateAddressCommand = mockAllocateAddresses;
  },
}));
const { params, run } = require("../../ec2/src/ec2_allocateaddress");

test("has to mock ec2#allocateAddresses", async (done) => {
  await run();
  expect(mockAllocateAddresses).toHaveBeenCalled;
  done();
});
