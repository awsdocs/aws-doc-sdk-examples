// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.EventBridge;
using EventBridgeActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace EventBridgeTests;

/// <summary>
/// Tests for the EventBridgeWrapper class.
/// </summary>
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
        // Arrange.
        var roleArn = _configuration["roleArn"];
        var eventRuleName = _configuration["eventRuleName"];
        var testBucketName = _configuration["testBucketName"];

        // Act.
        var arn = await _eventBridgeWrapper.PutS3UploadRule(roleArn, eventRuleName, testBucketName);

        // Assert.
        Assert.False(string.IsNullOrEmpty(arn));
    }

    /// <summary>
    /// Get the state of a rule by rule name. The state should be Enabled.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task GetRuleStateByRuleName_ShouldReturnEnabled()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var ruleState = await _eventBridgeWrapper.GetRuleStateByRuleName(eventRuleName);

        // Assert.
        Assert.Equal(RuleState.ENABLED, ruleState);
    }

    /// <summary>
    /// Disable a particular rule on an eventBus. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task DisableRuleByName_ShouldReturnTrue()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var success = await _eventBridgeWrapper.DisableRuleByName(eventRuleName);

        // Assert.
        Assert.True(success);
    }

    /// <summary>
    /// Test enabling a rule.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task EnableRuleByName_ShouldReturnTrue()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var result = await _eventBridgeWrapper.EnableRuleByName(eventRuleName);

        // Assert.
        Assert.True(result);
    }

    /// <summary>
    /// Verify adding an Amazon SNS target to a rule returns successfully.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task AddSnsTargetToRule_ReturnsSuccessfulResult()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];
        var topicArn = _configuration["topicArn"];

        // Act.
        var result = await _eventBridgeWrapper.AddSnsTargetToRule(eventRuleName, topicArn);

        // Assert.
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Verify listing all targets on a rule. Should not be empty.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task ListAllTargetsOnRule_ReturnsSuccessfulResult()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var result = await _eventBridgeWrapper.ListAllTargetsOnRule(eventRuleName);

        // Assert.
        Assert.NotNull(result);
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Verify listing all rule names by target. Should not be empty.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Integration")]
    public async Task ListAllRuleNamesByTarget_ReturnsSuccessfulResult()
    {
        // Arrange.
        var topicArn = _configuration["topicArn"];

        // Act.
        var result = await _eventBridgeWrapper.ListAllRuleNamesByTarget(topicArn);

        // Assert.
        Assert.NotNull(result);
        Assert.NotEmpty(result);
    }

    /// <summary>
    /// Verify updating a rule with a transform returns a successful result.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Integration")]
    public async Task UpdateS3UploadRuleTargetWithTransform_ReturnsSuccessfulResult()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];
        var topicArn = _configuration["topicArn"];

        // Act.
        var result = await _eventBridgeWrapper.UpdateS3UploadRuleTargetWithTransform(
            eventRuleName, topicArn);

        // Assert.
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Verify updating an event with a custom pattern returns an ARN.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task UpdateCustomEventPattern_ReturnsNonEmptyArn()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var result = await _eventBridgeWrapper.UpdateCustomEventPattern(eventRuleName);

        // Assert.
        Assert.False(string.IsNullOrEmpty(result));
    }

    /// <summary>
    /// Verify putting a custom event returns a successful result.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    [Trait("Category", "Integration")]
    public async Task PutEvents_ReturnsSuccessfulResult()
    {
        // Arrange.
        var email = _configuration["testEmail"];

        // Act.
        var result = await _eventBridgeWrapper.PutCustomEmailEvent(email);

        // Assert.
        Assert.True(result);
    }

    /// <summary>
    /// Verify removing rule targets returns a successful result.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    [Trait("Category", "Integration")]
    public async Task RemoveTargets_ReturnsSuccessfulResult()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var result = await _eventBridgeWrapper.RemoveAllTargetsFromRule(eventRuleName);

        // Assert.
        Assert.True(result);
    }

    /// <summary>
    /// Verify deleting an event rule returns a successful result.
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    [Trait("Category", "Integration")]
    public async Task DeleteEventRule_ReturnsSuccessfulResult()
    {
        // Arrange.
        var eventRuleName = _configuration["eventRuleName"];

        // Act.
        var result = await _eventBridgeWrapper.DeleteRuleByName(eventRuleName);

        // Assert.
        Assert.True(result);
    }
}