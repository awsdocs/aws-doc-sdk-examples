const mockCreateCluster = jest.fn();
jest.mock("@aws-sdk/client-redshift-node", () => ({
    Redshift: function Redshift() {
        this.CreateClusterCommand = mockCreateCluster;
    },
}));
const { run } = require("../../redshift/redshift-create-cluster.js");

//test function
test("has to mock RedShift#createCluster", async (done) => {
    await run();
    expect(mockCreateCluster).toHaveBeenCalled;
    done();
});
