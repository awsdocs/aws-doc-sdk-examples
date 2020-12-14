const mockDescribeSecurityGroups = jest.fn();
jest.mock("@aws-sdk/client-ec2/commands/DescribeSecurityGroupsCommand", () => ({
  EC2: function EC2() {
    this.DescribeSecurityGroupsCommand = mockDescribeSecurityGroups;
  },
}));
const { params, run } = require("../../ec2/src/ec2_describesecuritygroups");

test("has to mock ec2#describeSecurityGroups", async (done) => {
  await run();
  expect(mockDescribeSecurityGroups).toHaveBeenCalled;
  done();
});
