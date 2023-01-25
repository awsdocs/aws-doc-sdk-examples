// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.StepFunctions.Model;

namespace SupportTests
{
    public class StepFunctionsWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly string _activityName;
        private readonly string _roleArn;
        private static string _activityArn;
        private static string _executionArn;
        private static string _stateMachineArn;
        private readonly string _stateMachineName;
        private readonly string _executionName;
        private static IAmazonStepFunctions _client;
        private static StepFunctionsWrapper _wrapper;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public StepFunctionsWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _activityName = _configuration["ActivityName"];
            _stateMachineName = _configuration["StateMachineName"];
            _roleArn = _configuration["RoleArn"];
            _executionName = _configuration["ExecutionName"];

            _client = new AmazonStepFunctionsClient();
            _wrapper = new StepFunctionsWrapper(_client);
        }

        /// <summary>
        /// Test the CreateActivity method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateActivityTest()
        {
            _activityArn = await _wrapper.CreateActivity(_activityName);
            Assert.NotNull(_activityArn);
        }

        /// <summary>
        /// Test the CreateStateMachine method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task CreateStateMachineTest()
        {
            var stateDefinition = @"{
              ""Comment"": ""An example using a Task state."",
              ""StartAt"": ""getGreeting"",
              ""Version"": ""1.0"",
              ""TimeoutSeconds"": 300,
              ""States"":
              {
                ""getGreeting"": {
                  ""Type"": ""Task"",
                  ""Resource"": """ + _activityArn + @""",
                  ""End"": true
                }
              }
            }";

            _stateMachineArn = await _wrapper.CreateStateMachine(_stateMachineName, stateDefinition, _roleArn);
            Assert.NotNull(_stateMachineArn);
        }

        /// <summary>
        /// Test the StartExecution method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task StartExecutionTest()
        {
            _executionArn = await _wrapper.StartExecution(_executionName, @"{ ""who"" : ""AWS Step Functions"" }", _stateMachineArn);
            Assert.NotNull(_executionArn);
        }

        /// <summary>
        /// Test the StopExecution method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task StopExecutionTest()
        {
            var success = await _wrapper.StopExecution(_executionArn);
            Assert.True(success);
        }

        /// <summary>
        /// Test the ListExecutions method. Expects to find that the state
        /// machine was executed at least once.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task ListExecutionsTest()
        {
            var executions = await _wrapper.ListExecutions(_stateMachineArn);
            Assert.True(executions.Count > 0);
        }

        /// <summary>
        /// Test the DeleteStateMachine method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task DeleteStateMachineTest()
        {
            var success = await _wrapper.DeleteStateMachine(_stateMachineArn);
            Assert.True(success);
        }

        /// <summary>
        /// Test the DeleteActivity test.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task DeleteActivityTest()
        {
            var success = await _wrapper.DeleteActivity(_activityArn);
            Assert.True(success);
        }
    }
}