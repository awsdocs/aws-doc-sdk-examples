const mockDeleteSecGroup = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/DeleteSecurityGroupCommand", () => ({
  EC2: function EC2() {
    this.DeleteSecurityGroupCommand = mockDeleteSecGroup;
  },
}));
const { params, run } = require("../../ec2/src/ec2_deletesecuritygroup");

//test function
test("has to mock ec2#deleteSecurityGroup", async (done) => {
  await run();
  expect(mockDeleteSecGroup).toHaveBeenCalled;
  done();
});
