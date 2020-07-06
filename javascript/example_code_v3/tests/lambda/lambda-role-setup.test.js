process.argv.push('--arg1', 'us-east-1');
process.argv.push('--arg1', 'NEW_ROLENAME');

const mockLambdaRoleSetup = jest.fn();
jest.mock('@aws-sdk/client-lambda/commands/CreateRoleCommand', () => ({
    Lambda: function lambda() {
        this.CreateRoleCommand = mockLambdaRoleSetup
    }
}));
const {run} = require("../../lambda/tutorial/slotassets/lambda-role-setup");

//test function
test("has to mock lambda#rolesetup",  async (done) => {
    await run();
    expect(mockLambdaRoleSetup).toHaveBeenCalled;
    done();
});
