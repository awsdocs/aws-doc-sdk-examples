// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Lambda_Basics.Tests
{
    public class LambdaMethodsTests
    {
        private readonly string _FunctionName = "test-function";
        private readonly AmazonLambdaClient _Client;
        private string _FunctionArn;

        public LambdaMethodsTests()
        {
            _Client = new AmazonLambdaClient();
        }

        [Fact()]
        public async Task DeleteLambdaFunctionTest()
        {
            var success = await LambdaMethods.DeleteLambdaFunction(_Client, _FunctionName);
            Assert.True(success, "Could not delete the function.");
        }

        [Fact()]
        public async Task DeleteLambdaFunctionTest_DoesntExist_ShouldFail()
        {
            var functionName = "nonexistent_function";
            var success = await LambdaMethods.DeleteLambdaFunction(_Client, functionName);
            Assert.False(success, "Should not be able to delete a non-existent function.");
        }

        [Fact()]
        public async Task UpdateFunctionCodeTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async Task InvokeFunctionTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async Task ListFunctionsTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async Task GetFunctionTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async static Task CreateLambdaFunctionTest()
        {
            Assert.True(false, "This test needs an implementation.");
        }
    }
}