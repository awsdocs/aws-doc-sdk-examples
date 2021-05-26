const mockDescribeStackResources = jest.fn();
jest.mock("@aws-sdk/client-cloudformation/commands/DescribeStackResourcesCommand", () => ({
    CloudFormation: function CloudFormation() {
        this.DescribeStackResourcesCommand = mockDescribeStackResources;
    },
}));
import { run } from "../../../lambda/lambda_create_function/src/describe-stack-resources";

test("has to mock cloudformation#describestackresources", async (done) => {
    await run();
    expect(mockDescribeStackResources).toHaveBeenCalled;
    done();
});
