const mockUpdateTemplate = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/UpdateTemplateCommand", () => ({
  SES: function SES() {
    this.UpdateTemplateCommand = mockUpdateTemplate;
  },
}));
const { run } = require("../../ses/src/ses_updatetemplate.js");

test("has to mock SES#updatetemplate", async (done) => {
  await run();
  expect(mockUpdateTemplate).toHaveBeenCalled;
  done();
});
