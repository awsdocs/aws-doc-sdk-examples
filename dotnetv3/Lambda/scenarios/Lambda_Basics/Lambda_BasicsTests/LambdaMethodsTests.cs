// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Newtonsoft.Json.Linq;
using Xunit.Extensions.Ordering;

namespace Lambda_Basics.Tests
{
    public class LambdaMethodsTests
    {
        private readonly IConfiguration _configuration;
        private readonly AmazonLambdaClient _client;
        private readonly LambdaMethods _LambdaMethods;

        public LambdaMethodsTests()
        {
            _client = new AmazonLambdaClient();
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from JSON file.
                .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

            _LambdaMethods = new LambdaMethods();
        }

        [Fact()]
        [Order(1)]
        public async Task CreateLambdaFunctionTest()
        {
            var functionArn = await _LambdaMethods.CreateLambdaFunction(
                _client,
                _configuration["FunctionName"],
                _configuration["BucketName"],
                _configuration["Key"],
                _configuration["Role"],
                _configuration["Handler"]);
            Assert.NotNull(functionArn);
        }

        [Fact()]
        [Order(2)]
        public async Task GetFunctionTest()
        {
            var functionConfig = await _LambdaMethods.GetFunction(_client, _configuration["FunctionName"]);
            Assert.Equal(functionConfig.FunctionName, _configuration["FunctionName"]);
        }

        [Fact()]
        [Order(3)]
        public async Task ListFunctionsTest()
        {
            var functions = await _LambdaMethods.ListFunctions(_client);
            Assert.True(functions.Count > 0, "Couldn't find any functions to list.");
        }

        [Fact()]
        [Order(4)]
        public async Task InvokeArithmeticMultiplyFunctionTest()
        {
            var functionParameters = "{" +
                                     "\"action\": \"" + "multiply" + "\", " +
                                     "\"x\": \"" + 6 + "\"," +
                                     "\"y\": \"" + 7 + "\"" +
                                     "}";

            var response = await _LambdaMethods.InvokeFunctionAsync(
                _client,
                _configuration["FunctionName"],
                functionParameters);
            Assert.Equal("42", response);
        }

        [Fact()]
        [Order(5)]
        public async Task UpdateFunctionCodeTest()
        {
            await _LambdaMethods.UpdateFunctionCode(
                _client,
                _configuration["FunctionName"],
                _configuration["bucketName"],
                _configuration["key"]);
        }
        [Fact()]
        [Order(6)]
        public async Task DeleteLambdaFunctionTest()
        {
            var success = await _LambdaMethods.DeleteLambdaFunction(
                _client,
                _configuration["FunctionName"]);
            Assert.True(success, "Could not delete the function.");
        }

        [Fact()]
        [Order(7)]
        public async Task DeleteLambdaFunctionTest_DoesntExist_ShouldFail()
        {
            var functionName = "nonexistent_function";
            var success = await _LambdaMethods.DeleteLambdaFunction(_client, functionName);
            Assert.False(success, "Should not be able to delete a non-existent function.");
        }
    }
}