
const mockLambdaFunSetup = jest.fn();
jest.mock('@aws-sdk/client-lambda/commands/CreateFunctionCommand', () => ({
    Lambda: function lambda() {
        this.CreateFunctionCommand = mockLambdaFunSetup
    }
}));
const {run} = require("../../lambda/tutorial/slotassets/lambda-function-setup");

//test function
test("has to mock lambda#functionsetup",  async (done) => {
    await run();
    expect(mockLambdaFunSetup).toHaveBeenCalled;
    done();
});
