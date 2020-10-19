const mockListTemplates = jest.fn();
jest.mock("@aws-sdk/client-ses/commands/ListTemplatesCommand", () => ({
  SES: function SES() {
    this.ListTemplatesCommand = mockListTemplates;
  },
}));
const { run } = require("../../ses/src/ses_sendbulktemplatedemail.js");

//test function
test("has to mock SES#sendbulktemplatedemail", async (done) => {
  await run();
  expect(mockListTemplates).toHaveBeenCalled;
  done();
});
