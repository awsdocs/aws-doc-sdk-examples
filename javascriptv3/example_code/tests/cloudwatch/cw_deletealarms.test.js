const mockDeleteAlarms = jest.fn();
jest.mock("@aws-sdk/client-cloudwatch/commands/DeleteAlarmsCommand", () => ({
  CloudWatch: function CloudWatch() {
    this.DeleteAlarmsCommand = mockDeleteAlarms;
  },
}));
const { params, run } = require("../../cloudwatch/src/cw_deletealarms");

//test function
test("has to mock cloudwatch#deletealarms", async (done) => {
  await run();
  expect(mockDeleteAlarms).toHaveBeenCalled;
  done();
});
