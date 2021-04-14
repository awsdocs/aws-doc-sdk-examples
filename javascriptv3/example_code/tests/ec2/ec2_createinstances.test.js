const mockCreateInstances = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/CreateKeyPairCommand", () => ({
  EC2: function EC2() {
    this.CreateKeyPairCommand = mockCreateInstances;
  },
}));
const { params, run } = require("../../ec2/src/ec2_createinstances");

test("has to mock ec2#createInstances", async (done) => {
  await run();
  expect(mockCreateInstances).toHaveBeenCalled;
  done();
});
