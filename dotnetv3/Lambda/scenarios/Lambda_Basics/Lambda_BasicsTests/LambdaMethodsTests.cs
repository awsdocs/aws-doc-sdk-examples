// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Lambda_Basics.Tests
{
    public class LambdaMethodsTests
    {
        private static readonly string _FunctionName = "test-function";
        private static readonly AmazonLambdaClient _Client;
        private static string _FunctionArn;

        static LambdaMethodsTests()
        {
            _Client = new AmazonLambdaClient(RegionEndpoint.USWest2);
        }

        [Fact()]
        public static async Task DeleteLambdaFunctionTest()
        {
            var success = await LambdaMethods.DeleteLambdaFunction(_Client, _FunctionName);
            Assert.True(success, "Could not delete the function.");
        }

        [Fact()]
        public static async Task DeleteLambdaFunctionTest_DoesntExist_ShouldFail()
        {
            var functionName = "nonexistent_function";
            var success = await LambdaMethods.DeleteLambdaFunction(_Client, functionName);
            Assert.False(success, "Should not be able to delete a non-existent function.");
        }

        [Fact()]
        public void UpdateFunctionCodeTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void InvokeFunctionTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void ListFunctionsTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void GetFunctionTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async static Task CreateLambdaFunctionTest()
        {
            
        }
    }
}