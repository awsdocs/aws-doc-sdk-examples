const mockListJobs = jest.fn();
jest.mock("@aws-sdk/client-mediaconvert/commands/ListJobsCommand", () => ({
  MediaConvert: function MediaConvert() {
    this.ListJobsCommand = mockListJobs;
  },
}));
const { run } = require("../../mediaconvert/src/emc_listjobs");

test("has to mock mediaconvert#canceljob", async (done) => {
  await run();
  expect(mockListJobs).toHaveBeenCalled;
  done();
});
