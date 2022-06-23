const mockUploadArchive = jest.fn();
jest.mock("@aws-sdk/client-glacier/commands/UploadArchiveCommand", () => ({
  Glacier: function Glacier() {
    this.UploadArchiveCommand = mockUploadArchive;
  },
}));
const { run } = require("../../glacier/src/uploadArchive");

test("has to mock glacier#uploadarchive", async (done) => {
  await run();
  expect(mockUploadArchive).toHaveBeenCalled;
  done();
});
