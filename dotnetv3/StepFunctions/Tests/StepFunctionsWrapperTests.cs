// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.StepFunctions.Model;

namespace SupportTests
{
    public class StepFunctionsWrapperTests
    {
        private readonly string? _activityName;
        private readonly string? _roleArn;
        private static string? _activityArn;
        private static string? _executionArn;
        private static string? _stateMachineArn;
        private readonly string? _stateMachineName;

        private static IAmazonStepFunctions _client;
        private static StepFunctionsWrapper _wrapper;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public StepFunctionsWrapperTests()
        {
            IConfiguration configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _activityName = configuration["ActivityName"];
            _stateMachineName = configuration["StateMachineName"];
            _roleArn = configuration["RoleArn"];

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
        /// Test the ListActivitiesAsync method. The list of activities
        /// should have at least one activity in it.
        /// </summary>
        /// <returns></returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task ListActivitiesAsyncTest()
        {
            var activities = await _wrapper.ListActivitiesAsync();
            Assert.True(activities.Count >= 1);
        }

        /// <summary>
        /// Test the CreateStateMachine method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task CreateStateMachineTest()
        {
            // Create a simple Step Functions state machine for testing
            // purposes only.
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
        /// Test the DescribeStateMachineAsync method. The StateMachineArn
        /// in the response should be equal to the value passed to the method.
        /// </summary>
        /// <returns></returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task DescribeStateMachineAsyncTest()
        {
            var describeStateMachineResponse = await _wrapper.DescribeStateMachineAsync(_stateMachineArn);
            Assert.Equal(describeStateMachineResponse.StateMachineArn, _stateMachineArn);
        }

        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task ListStateMachinesAsyncTest()
        {
            var stateMachines = await _wrapper.ListStateMachinesAsync();
            Assert.True(stateMachines.Count >= 1);
        }

        /// <summary>
        /// Test the StartExecutionAsync method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task StartExecutionTest()
        {
            // Starts execution of the simpler Step Functions state machine
            // created for testing pupposes only.
            _executionArn = await _wrapper.StartExecutionAsync(@"{ ""who"" : ""AWS Step Functions"" }", _stateMachineArn);
            Assert.NotNull(_executionArn);
        }

        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task GetActivityTaskAsyncTest()
        {
            var response = await _wrapper.GetActivityTaskAsync(_activityArn, "MvpWorker");
            Assert.NotNull(response);
        }

        /// <summary>
        /// Test the StopExecution method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task StopExecutionTest()
        {
            var success = await _wrapper.StopExecution(_executionArn);
            Assert.True(success);
        }

        /// <summary>
        /// Test the DescribeExecutionAsync method. The method continues to
        /// call DescribeExecutionAsync until the status is no longer RUNNING.
        /// </summary>
        /// <returns></returns>
        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task DescribeExecutionAsyncTest()
        {
            DescribeExecutionResponse executionResponse;
            do
            {
                executionResponse = await _wrapper.DescribeExecutionAsync(_executionArn);
            } while (executionResponse.Status == ExecutionStatus.RUNNING);
            Assert.NotEqual(ExecutionStatus.RUNNING, executionResponse.Status);
        }

        /// <summary>
        /// Test the ListExecutionsAsync method. Expects to find that the state
        /// machine was executed at least once.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task ListExecutionsTest()
        {
            var executions = await _wrapper.ListExecutionsAsync(_stateMachineArn);
            Assert.True(executions.Count > 0);
        }

        /// <summary>
        /// Test the DeleteStateMachine method.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(11)]
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
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task DeleteActivityTest()
        {
            var success = await _wrapper.DeleteActivity(_activityArn);
            Assert.True(success);
        }
    }
}