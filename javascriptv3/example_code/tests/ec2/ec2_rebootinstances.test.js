const mockRebootInstancesCommand = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/AllocateAddressCommand", () => ({
  EC2: function EC2() {
    this.RebootInstancesCommandInput = mockRebootInstancesCommand;
  },
}));
const { params, run } = require("../../ec2/src/ec2_rebootinstances");

test("has to mock ec2#rebootInstances", async (done) => {
  await run();
  expect(mockRebootInstancesCommand).toHaveBeenCalled;
  done();
});
