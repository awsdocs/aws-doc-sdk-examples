const mockInvokeLambdaFunction = jest.fn();
jest.mock("@aws-sdk/client-lambda/commands/InvokeCommand", () => ({
  Lambda: function Lambda() {
    this.InvokeCommand = mockInvokeLambdaFunction;
  },
}));
import { run } from "../../../lambda/lambda_create_function/src/LambdaApp/index.js";

test("has to mock ddb#invokefunction", async (done) => {
  await run();
  expect(mockInvokeLambdaFunction).toHaveBeenCalled;
  done();
});
