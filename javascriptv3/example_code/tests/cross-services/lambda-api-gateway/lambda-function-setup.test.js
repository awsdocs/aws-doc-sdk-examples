const mockCreateFunction = jest.fn();
jest.mock("@aws-sdk/client-lambda/commands/CreateFunctionCommand", () => ({
    Lambda: function Lambda() {
        this.CreateFunctionCommand = mockCreateFunction;
    },
}));
import { run } from "../../../cross-services/lambda-api-gateway/src/helper-functions/lambda-function-setup";

test("has to mock db#createfunction", async (done) => {
    await run();
    expect(mockCreateFunction).toHaveBeenCalled;
    done();
});

