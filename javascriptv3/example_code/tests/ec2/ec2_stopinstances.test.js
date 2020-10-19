const mockStopInstancesCommand = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/StopInstancesCommand", () => ({
  EC2: function EC2() {
    this.StopInstancesCommand = mockStopInstancesCommand;
  },
}));
const { params, run } = require("../../ec2/src/ec2_startstopinstances");

//test function
test("has to mock ec2#startstopInstances", async (done) => {
  await run();
  expect(mockStopInstancesCommand).toHaveBeenCalled;
  done();
});
