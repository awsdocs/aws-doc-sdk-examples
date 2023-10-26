// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using Amazon.Lambda;
using Amazon.Lambda.Model;
using Microsoft.Extensions.Configuration;

namespace LambdaTests
{
    /// <summary>
    /// Test methods for the LambdaMethods class in the AWS Lambda Basics scenario.
    /// </summary>
    public class LambdaMethodsTests
    {
        private readonly IConfiguration _configuration;
        private readonly AmazonLambdaClient _lambdaService;
        private readonly IAmazonIdentityManagementService _identityManagementService;

        private readonly LambdaWrapper _lambdaWrapper;
        private readonly LambdaRoleWrapper _lambdaRoleWrapper;

        private readonly string? _functionName;
        private readonly string? _bucketName;
        private readonly string? _incrementKey;
        private readonly string? _incrementHandler;
        private readonly string? _calculatorKey;
        private readonly string? _calculatorHandler;
        private readonly string? _policyDocument;
        private readonly string? _policyArn;
        private static string? _roleName;
        private static string? _roleArn;

        /// <summary>
        /// Constructor for the LambdaMethodsTests class.
        /// </summary>
        public LambdaMethodsTests()
        {
            _lambdaService = new AmazonLambdaClient();
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from JSON file.
                .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

            _lambdaService = new AmazonLambdaClient();
            _identityManagementService = new AmazonIdentityManagementServiceClient();

            _lambdaWrapper = new LambdaWrapper(_lambdaService);
            _lambdaRoleWrapper = new LambdaRoleWrapper(_identityManagementService);

            _functionName = _configuration["FunctionName"];
            _bucketName = _configuration["BucketName"];
            _incrementKey = _configuration["IncrementKey"];
            _incrementHandler = _configuration["IncrementHandler"];
            _calculatorHandler = _configuration["CalculatorHandler"];
            _calculatorKey = _configuration["CalculatorKey"];
            _roleName = _configuration["RoleName"];
            _policyArn = _configuration["PolicyArn"];

            _policyDocument = "{" +
                " \"Version\": \"2012-10-17\"," +
                " \"Statement\": [ " +
                "    {" +
                "        \"Effect\": \"Allow\"," +
                "        \"Principal\": {" +
                "            \"Service\": \"lambda.amazonaws.com\" " +
                "    }," +
                "        \"Action\": \"sts:AssumeRole\" " +
                "    }" +
                "]" +
            "}";
        }

        /// <summary>
        /// Test the LambdaRoleWrapper method CreateLambdaRoleAsync by creating
        /// a new role to be used by the Lambda functions.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateLambdaRoleAsyncTest()
        {
            _roleArn = await _lambdaRoleWrapper.CreateLambdaRoleAsync(_roleName, _policyDocument);

            // Wait for the role to be active before continuing.
            try
            {
                System.Threading.Thread.Sleep(5000);
                var response = await _identityManagementService.GetRoleAsync(new GetRoleRequest { RoleName = _roleName });
                Assert.Equal(_roleArn, response.Role.Arn);
            }
            catch
            {
                System.Threading.Thread.Sleep(5000);
            }

            Assert.NotNull(_roleArn);
        }

        /// <summary>
        /// Test the LambdaRoleWrapper method AttachLambdaRolePolicyAsync which
        /// attaches an AWS Identity and Access Management (IAM) policy to the
        /// IAM role created for the scenario.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task AttachRolePolicyAsyncTest()
        {
            var success = await _lambdaRoleWrapper.AttachLambdaRolePolicyAsync(_policyArn, _roleName);
            System.Threading.Thread.Sleep(15000);
            Assert.True(success);
        }

        /// <summary>
        /// Tests the LambdaWrapper class CreateLambdaFunction method.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task CreateLambdaFunctionAsyncTest()
        {
            var functionArn = await _lambdaWrapper.CreateLambdaFunctionAsync(
               _functionName,
               _bucketName,
               _incrementKey,
               _roleArn,
               _incrementHandler);

            Assert.NotNull(functionArn);
        }

        /// <summary>
        /// Test the LambdaWrapper method GetFunction to get information
        /// about a Lambda function.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task GetFunctionAsyncTest()
        {
            var functionConfig = await _lambdaWrapper.GetFunctionAsync(_functionName);
            Assert.Equal(functionConfig.FunctionName, _functionName);
        }

        /// <summary>
        /// Tests the LambdaWrapper method ListFunctionsAsync to list existing
        /// Lambda functions for the current account.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task ListFunctionsAsyncTest()
        {
            var functions = await _lambdaWrapper.ListFunctionsAsync();
            Assert.True(functions.Count > 0, "Couldn't find any functions to list.");
        }

        /// <summary>
        /// Test the LambdaWrapper method InvokeIncrementerAsync to invoke the
        /// increment function.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task InvokeIncrementerAsyncTest()
        {
            // First make sure that the function is active.
            FunctionConfiguration config;
            do
            {
                config = await _lambdaWrapper.GetFunctionAsync(_functionName);
            }
            while (config.State != State.Active);

            string functionParameters = "{" +
                "\"action\": \"increment\", " +
                "\"x\": \"" + 12 + "\"" +
            "}";

            var answer = await _lambdaWrapper.InvokeFunctionAsync(
                _functionName,
                functionParameters);
            Assert.Equal("13", answer);
        }

        /// <summary>
        /// Test the LambdaWrapper method UpdateFunctionCodeAsync to update the
        /// Lambda function's code.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task UpdateFunctionCodeAsyncTest()
        {
            await _lambdaWrapper.UpdateFunctionCodeAsync(
                _functionName,
                _bucketName,
                _calculatorKey);

            FunctionConfiguration config;
            do
            {
                config = await _lambdaWrapper.GetFunctionAsync(_functionName);
            }
            while (config.LastUpdateStatus == LastUpdateStatus.InProgress);

            Assert.Equal(config.LastUpdateStatus, LastUpdateStatus.Successful);
        }

        /// <summary>
        /// Test the LambdaWrapper UpdateFunctionConfigurationAsync method to
        /// update the configuration settings for the Lambda function.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task UpdateFunctionConfigurationAsyncTest()
        {
            var success = await _lambdaWrapper.UpdateFunctionConfigurationAsync(
                _functionName,
                _calculatorHandler,
                new Dictionary<string, string> { { "LOG_LEVEL", "DEBUG" } });

            // Give the configuration a chance to be updated.
            FunctionConfiguration config;
            do
            {
                config = await _lambdaWrapper.GetFunctionAsync(_functionName);
            }
            while (config.LastUpdateStatus == LastUpdateStatus.InProgress);

            Assert.Equal(config.LastUpdateStatus, LastUpdateStatus.Successful);
        }

        /// <summary>
        /// Test the LambdaWrapper method InvokeArithmeticMultiplyFunctionAsync
        /// to test the calculator version of the Lambda function.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task InvokeArithmeticMultiplyFunctionAsyncTest()
        {
            var functionParameters = "{" +
                 "\"action\": \"" + "multiply" + "\", " +
                 "\"x\": \"" + 6 + "\"," +
                 "\"y\": \"" + 7 + "\"" +
            "}";

            var answer = await _lambdaWrapper.InvokeFunctionAsync(
                _functionName,
                functionParameters);
            Assert.Equal("42", answer);
        }

        /// <summary>
        /// Test the LambdaWrapper method DeleteLambdaFunctionAsync to delete
        /// the function created at the beginning of testing. It should succeed.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task DeleteLambdaFunctionAsyncTest()
        {
            var success = await _lambdaWrapper.DeleteFunctionAsync(
                _functionName);
            Assert.True(success, "Could not delete the function.");
        }

        /// <summary>
        /// Test the LambdaWrapper method DeleteFunctionAsync with the name of
        /// a function that doesn't exist. The call should fail by raising a
        /// ResourceNotFoundException.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task DeleteLambdaFunctionTest_DoesntExist_ShouldFail()
        {
            var functionName = "nonexistent_function";
            await Assert.ThrowsAsync<Amazon.Lambda.Model.ResourceNotFoundException>(async () =>
            {
                var success = await _lambdaWrapper.DeleteFunctionAsync(functionName);
                Assert.False(success, "Deleted a non-existent function.");
            });
        }

        /// <summary>
        /// Test the LambdaRoleWrapper DetachLambdaRolePolicyAsync method.
        /// Includes a wait for the IAM policy to be detached before
        /// executing the test to delete the IAM role.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task DetachLambdaRoleAsyncTest()
        {
            var success = await _lambdaRoleWrapper.DetachLambdaRolePolicyAsync(_policyArn, _roleName);

            // Allow time for the IAM policy to be detached.
            System.Threading.Thread.Sleep(15000);

            Assert.True(success);
        }

        /// <summary>
        /// Test the LambdaRoleWrapper method DeleteLambdaRoleAsync method to
        /// delete the role created at the beginning of the tests.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(13)]
        [Trait("Category", "Integration")]
        public async Task DeleteLambdaRoleAsyncTest()
        {
            var success = await _lambdaRoleWrapper.DeleteLambdaRoleAsync(_roleName);
            Assert.True(success);
        }
    }
}