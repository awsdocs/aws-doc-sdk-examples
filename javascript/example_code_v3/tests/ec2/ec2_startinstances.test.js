const mockStartInstancesCommand = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/StartInstancesCommand", () => ({
  EC2: function EC2() {
    this.StartInstancesCommand = mockStartInstancesCommand;
  },
}));
const { params, run } = require("../../ec2/ec2_startstopinstances");

//test function
test("has to mock ec2#startstopInstances", async (done) => {
  await run();
  expect(mockStartInstancesCommand).toHaveBeenCalled;
  done();
});
