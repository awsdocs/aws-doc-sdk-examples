// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.ECS;
using ECSActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace ECSTests;

/// <summary>
/// ECS tests.
/// </summary>
public class ECSTests
{
    private readonly IConfiguration _configuration;
    private readonly ILoggerFactory _loggerFactory;
    private readonly ECSWrapper _ecsWrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public ECSTests()
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
    /// List the clusters. Should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task ListClusters_ShouldNotReturnException()
    {
        try
        {
            var result = await _ecsWrapper.GetClusterARNSAsync();
            Assert.True(true, "List clusters should not return an exception.");

        }
        catch (Exception e)
        {
            Assert.True(false, e.Message);
        }

    }
}