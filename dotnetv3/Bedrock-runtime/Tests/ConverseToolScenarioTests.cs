// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon;
using Amazon.BedrockRuntime;
using ConverseToolScenario;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace BedrockRuntimeTests;

/// <summary>
/// Tests for the Converse Tool Use example.
/// </summary>
public class ConverseToolScenarioTests
{
    private readonly BedrockActionsWrapper _bedrockActionsWrapper = null!;
    private readonly WeatherTool _weatherTool = null!;
    private readonly ILoggerFactory _loggerFactory;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public ConverseToolScenarioTests()
    {

        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        IServiceCollection services = new ServiceCollection(); // [1]

        services.AddHttpClient();

        IHttpClientFactory _httpClientFactory = services
            .BuildServiceProvider()
            .GetRequiredService<IHttpClientFactory>();

        _bedrockActionsWrapper = new BedrockActionsWrapper(
            new AmazonBedrockRuntimeClient(RegionEndpoint.USEast1), new Logger<BedrockActionsWrapper>(_loggerFactory));
        _weatherTool = new WeatherTool(new Logger<WeatherTool>(_loggerFactory),
            _httpClientFactory);
        ConverseToolScenario.ConverseToolScenario._bedrockActionsWrapper = _bedrockActionsWrapper;
        ConverseToolScenario.ConverseToolScenario._weatherTool = _weatherTool;
    }

    /// <summary>
    /// Run the non-interactive scenario. Should return a non-empty conversation.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        ConverseToolScenario.ConverseToolScenario._interactive = false;

        // Act.
        var conversation = await ConverseToolScenario.ConverseToolScenario.RunConversationAsync();

        // Assert.
        Assert.NotEmpty(conversation);
    }
}