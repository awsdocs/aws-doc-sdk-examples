// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using System.Text.Json.Serialization;
using System.Text.Json;
using System.Text;
using Amazon.Runtime;
using ECSActions;
using Amazon.ECS;

namespace ECSTests;

/// <summary>
/// ECS tests.
/// </summary>
public class CloudWatchTests
{
    private readonly IConfiguration _configuration;
    private readonly ILoggerFactory _loggerFactory;
    private readonly ECSWrapper _ecsWrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public CloudWatchTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .Build();

        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        _ecsWrapper = new ECSWrapper(
            new AmazonECSClient(),
            new Logger<ECSWrapper>(_loggerFactory)
        );
    }

    /// <summary>
    /// List the metrics. Should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task ListClusters_ShouldNotBeNull()
    {
        var result = await _ecsWrapper.GetClusterARNSAsync();

        Assert.NotEmpty(result);
    }
}