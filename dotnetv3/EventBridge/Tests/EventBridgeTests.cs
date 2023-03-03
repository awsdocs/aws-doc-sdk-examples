// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.EventBridge;
using EventBridgeActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace EventBridgeTests;

public class EventBridgeTests
{
    private readonly IConfiguration _configuration;
    private readonly ILoggerFactory _loggerFactory;
    private readonly EventBridgeWrapper _eventBridgeWrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public EventBridgeTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        _eventBridgeWrapper = new EventBridgeWrapper(
            new AmazonEventBridgeClient(),
            new Logger<EventBridgeWrapper>(_loggerFactory)
        );
    }

    /// <summary>
    /// Add a rule to an event bus. The returned ARN should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task AddRuleToEventBus_ShouldReturnNonEmptyArn()
    {
        // Arrange
        var description = "Test rule description";
        var eventPattern = "{\"source\":[\"test-source\"]}";
        var name = "TestRule_" + Guid.NewGuid().ToString();
        var eventBusName = "default";

        // Act
        var arn = await _eventBridgeWrapper.AddRuleToEventBus(description, eventPattern, name, eventBusName);

        // Assert
        Assert.False(string.IsNullOrEmpty(arn));
    }

    /// <summary>
    /// Get the state of a rule by rule name. The state should be Enabled.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task GetRuleStateByRuleName_ShouldReturnEnabled()
    {
        // Arrange
        var ruleName = "TestRule_" + Guid.NewGuid().ToString();
        var description = "Test rule description";
        var eventPattern = "{\"source\":[\"test-source\"]}";
        var eventBusName = "default";
        await _eventBridgeWrapper.AddRuleToEventBus(description, eventPattern, ruleName, eventBusName);

        // Act
        var ruleState = await _eventBridgeWrapper.GetRuleStateByRuleName(ruleName, eventBusName);

        // Assert
        Assert.Equal(RuleState.ENABLED, ruleState);
    }

    /// <summary>
    /// Test enabling a rule.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task EnableRuleByName_ShouldReturnTrue()
    {
        // Create a test rule
        var ruleName = "TestRule-" + Guid.NewGuid().ToString();
        await _eventBridgeWrapper.AddRuleToEventBus("Test Rule", "{}", ruleName);

        // Enable the rule
        var result = await _eventBridgeWrapper.EnableRuleByName(ruleName);

        // Check if the result is true
        Assert.True(result);
    }

    /// <summary>
    /// Disable a particular rule on an eventBus. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task DisableRuleByName_ShouldReturnTrue()
    {

        // Disable the rule and check if it was successful
        var success = await _eventBridgeWrapper.DisableRuleByName(ruleName);
        Assert.True(success);

    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task CheckReplayStatus_ReturnsNonEmptyArn()
    {
        // Arrange
        var replayName = "test-replay";

        // Act
        var result = await _eventBridgeWrapper.CheckReplayStatus(replayName);

        // Assert
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task ListAllTargetsOnRule_ReturnsSuccessfulResult()
    {
        // Arrange
        var ruleName = "test-rule";

        // Act
        var result = await _eventBridgeWrapper.ListAllTargetsOnRule(ruleName);

        // Assert
        Assert.NotNull(result);
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task ListAllRuleNamesByTarget_ReturnsSuccessfulResult()
    {
        // Arrange
        var targetArn = "test-target-arn";

        // Act
        var result = await _eventBridgeWrapper.ListAllRuleNamesByTarget(targetArn);

        // Assert
        Assert.NotNull(result);
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task PutS3UploadRule_ReturnsNonEmptyArn()
    {
        // Arrange
        var roleArn = "test-role-arn";
        var bucketName = "test-bucket";

        // Act
        var result = await _eventBridgeWrapper.PutS3UploadRule(roleArn, bucketName);

        // Assert
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task UpdateS3UploadRuleTargetWithTransform_ReturnsSuccessfulResult()
    {
        // Arrange
        var eventBusArn = "test-eventbus-arn";
        var ruleName = "test-rule";
        var roleArn = "test-role-arn";
        var targetArn = "test-target-arn";

        // Act
        var result = await _eventBridgeWrapper.UpdateS3UploadRuleTargetWithTransform(eventBusArn, ruleName, roleArn, targetArn);

        // Assert
        Assert.True(result);
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task PutEvents_ReturnsSuccessfulResult()
    {
        // Arrange
        var email = "test@example.com";

        // Act
        var result = await _eventBridgeWrapper.PutEvents(email);

        // Assert
        Assert.True(result);
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task UpdateCustomEventPattern_ReturnsNonEmptyArn()
    {
        // Arrange
        var ruleName = "test-rule";

        // Act
        var result = await _eventBridgeWrapper.UpdateCustomEventPattern(ruleName);

        // Assert
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Test for CheckReplayStatus method
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task AddSnsTargetToRule_ReturnsSuccessfulResult()
    {
        // Arrange
        var eventBusArn = "test-eventbus-arn";
        var ruleName = "test-rule";
        var roleArn = "test-role-arn";
        var targetArn = "test-target-arn";

        // Act
        var result = await _eventBridgeWrapper.AddSnsTargetToRule(eventBusArn, ruleName, roleArn, targetArn);

        // Assert
        Assert.True(result);
    }
}