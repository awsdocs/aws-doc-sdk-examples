const mockLambdaRoleSetup = jest.fn();
jest.mock("@aws-sdk/client-lambda/commands/CreateRoleCommand", () => ({
  Lambda: function lambda() {
    this.CreateRoleCommand = mockLambdaRoleSetup;
  },
}));
import { run } from "../../lambda/tutorial/slotassets/lambda-role-setup";

test("has to mock lambda#rolesetup", async (done) => {
  await run();
  expect(mockLambdaRoleSetup).toHaveBeenCalled;
  done();
});
