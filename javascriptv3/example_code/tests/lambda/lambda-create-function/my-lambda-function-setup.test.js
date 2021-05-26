const mockCreateFunction = jest.fn();
jest.mock("@aws-sdk/client-lambda/commands/CreateFunctionCommand", () => ({
  Lambda: function Lambda() {
    this.CreateFunctionCommand = mockCreateFunction;
  },
}));
import { run } from "../../../lambda/lambda_create_function/src/lambda-function-setup";

test("has to mock lambda#createfunction", async (done) => {
  await run();
  expect(mockCreateFunction).toHaveBeenCalled;
  done();
});
