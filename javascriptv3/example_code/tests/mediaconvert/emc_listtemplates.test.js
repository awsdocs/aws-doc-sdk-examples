const mockListJobTemps = jest.fn();
jest.mock(
  "@aws-sdk/client-mediaconvert/commands/ListJobTemplatesCommand",
  () => ({
    MediaConvert: function MediaConvert() {
      this.ListJobTemplatesCommand = mockListJobTemps;
    },
  })
);
const { params, run } = require("../../mediaconvert/src/emc_listtemplates");

test("has to mock mediaconvert#listjobstemp", async (done) => {
  await run();
  expect(mockListJobTemps).toHaveBeenCalled;
  done();
});
