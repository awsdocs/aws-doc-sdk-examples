// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.S3;
using Microsoft.Extensions.Configuration;
using S3ObjectLockScenario;
using Xunit.Extensions.Ordering;

namespace S3ObjectLockTests;

/// <summary>
/// Tests for the ObjectLockScenario example.
/// </summary>
public class S3ObjectLockScenarioTests
{
    private readonly IConfiguration _configuration;

    private readonly S3ActionsWrapper _s3ActionsWrapper = null!;
    private readonly string _resourcePrefix;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public S3ObjectLockScenarioTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        _resourcePrefix = _configuration["resourcePrefix"] ?? "dotnet-example";

        _s3ActionsWrapper = new S3ActionsWrapper(
            new AmazonS3Client(),
            _configuration);

        S3ObjectLockWorkflow._s3ActionsWrapper = _s3ActionsWrapper;
        S3ObjectLockWorkflow._configuration = _configuration;
    }

    /// <summary>
    /// Run the setup step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestSetup()
    {
        // Arrange.
        S3ObjectLockWorkflow.ConfigurationSetup();

        // Act.
        var success = await S3ObjectLockWorkflow.Setup(false);

        var finished = false;
        while (!finished)
        {
            // Make sure the buckets are available before moving on.
            var created = await S3ObjectLockWorkflow.ListBucketsAndObjects(false);
            finished = created.Count > 0;
        }

        // Assert.
        Assert.True(success);
    }

    /// <summary>
    /// Run the list object step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task TestObjects()
    {
        // Arrange.
        S3ObjectLockWorkflow.ConfigurationSetup();

        // Act.
        var objects = await S3ObjectLockWorkflow.ListBucketsAndObjects(false);

        // Assert.
        Assert.NotEmpty(objects);
    }


    /// <summary>
    /// Run the cleanup step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task TestCleanup()
    {
        // Arrange.
        S3ObjectLockWorkflow.ConfigurationSetup();

        // Act.
        var success = await S3ObjectLockWorkflow.Cleanup(false);

        // Assert.
        Assert.True(success);
    }
}