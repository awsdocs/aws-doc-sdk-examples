const mockCreateTemplate = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/CreateTemplateCommand", () => ({
  SES: function SES() {
    this.CreateTemplateCommand = mockCreateTemplate;
  },
}));
const { run } = require("../../ses/src/ses_createtemplate.js");

test("has to mock SES#createTemplate", async (done) => {
  await run();
  expect(mockCreateTemplate).toHaveBeenCalled;
  done();
});
