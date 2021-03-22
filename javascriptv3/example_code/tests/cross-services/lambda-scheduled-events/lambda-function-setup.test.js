const mockCreateFunction = jest.fn();
jest.mock("@aws-sdk/client-lambda/commands/CreateFunctionCommand", () => ({
    Lambda: function Lambda() {
        this.CreateFunctionCommand = mockCreateFunction;
    },
}));
const { run } = require("../../../cross-services/lambda-scheduled-events/src/helper-functions/lambda-function-setup");

test("has to mock db#createfunction", async (done) => {
    await run();
    expect(mockCreateFunction).toHaveBeenCalled;
    done();
});

