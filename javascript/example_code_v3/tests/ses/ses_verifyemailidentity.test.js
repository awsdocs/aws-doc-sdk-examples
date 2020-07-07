


const mockVerifyDomainIdentity = jest.fn();
jest.mock('@aws-sdk/client-ses/commands/VerifyDomainIdentityCommand', () => ({
    SES: function SES() {
        this.VerifyDomainIdentityCommand = mockVerifyDomainIdentity
    }
}));
const {run} = require("../../ses/ses_verifyemailidentity.js");

//test function
test("has to mock SES#verifyemailidentity",  async (done) => {
    await run();
    expect(mockVerifyDomainIdentity).toHaveBeenCalled;
    done();
});
