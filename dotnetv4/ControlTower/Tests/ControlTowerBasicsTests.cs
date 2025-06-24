// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.ControlTower;
using Amazon.ControlCatalog;
using Microsoft.Extensions.Configuration;
using Moq;
using ControlTowerActions;

namespace ControlTowerTests;

/// <summary>
/// Tests for the ControlTowerWrapper class.
/// </summary>
public class ControlTowerBasicsTests
{
    private readonly IConfiguration _configuration;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public ControlTowerBasicsTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from JSON file.
            .AddJsonFile("testsettings.local.json",
                true) // Load local test settings from JSON file.
            .Build();
    }

    /// <summary>
    /// Test that ListLandingZonesAsync returns a list.
    /// </summary>
    [Fact]
    public async Task ListLandingZonesAsync_ShouldReturnList()
    {
        // Arrange
        var mockControlTowerService = new Mock<IAmazonControlTower>();
        var mockControlCatalogService = new Mock<IAmazonControlCatalog>();
        var wrapper = new ControlTowerWrapper(mockControlTowerService.Object, mockControlCatalogService.Object);

        // Act & Assert
        var exception = await Record.ExceptionAsync(() => wrapper.ListLandingZonesAsync());
        Assert.Null(exception);
    }

    /// <summary>
    /// Test that ListBaselinesAsync returns a list.
    /// </summary>
    [Fact]
    public async Task ListBaselinesAsync_ShouldReturnList()
    {
        // Arrange
        var mockControlTowerService = new Mock<IAmazonControlTower>();
        var mockControlCatalogService = new Mock<IAmazonControlCatalog>();
        var wrapper = new ControlTowerWrapper(mockControlTowerService.Object, mockControlCatalogService.Object);

        // Act & Assert
        var exception = await Record.ExceptionAsync(() => wrapper.ListBaselinesAsync());
        Assert.Null(exception);
    }

    /// <summary>
    /// Test that ListControlsAsync returns a list.
    /// </summary>
    [Fact]
    public async Task ListControlsAsync_ShouldReturnList()
    {
        // Arrange
        var mockControlTowerService = new Mock<IAmazonControlTower>();
        var mockControlCatalogService = new Mock<IAmazonControlCatalog>();
        var wrapper = new ControlTowerWrapper(mockControlTowerService.Object, mockControlCatalogService.Object);

        // Act & Assert
        var exception = await Record.ExceptionAsync(() => wrapper.ListControlsAsync());
        Assert.Null(exception);
    }
}