// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.MediaConvert;
using MediaConvertActions;
using Microsoft.Extensions.Configuration;

namespace MediaConvertTests;

/// <summary>
/// Tests for the MediaConvertWrapper class.
/// </summary>
public class MediaConvertTests
{
    private readonly IConfiguration _configuration;
    private readonly MediaConvertWrapper _mediaConvertWrapper;
    public static string? _jobId;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public MediaConvertTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        var mediaConvertEndpoint = _configuration["mediaConvertEndpoint"];
        AmazonMediaConvertConfig mcConfig = new AmazonMediaConvertConfig
        {
            ServiceURL = mediaConvertEndpoint,
        };

        AmazonMediaConvertClient mcClient = new AmazonMediaConvertClient(mcConfig);

        _mediaConvertWrapper = new MediaConvertWrapper(mcClient);
    }

    /// <summary>
    /// Create a job. The returned job ID should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task CreateJob_ShouldReturnNonEmptyId()
    {
        // Arrange.
        var mediaConvertRole = _configuration["mediaConvertRoleARN"];

        // Include the file input and output locations in settings.json or settings.local.json.
        var fileInput = _configuration["fileInput"];
        var fileOutput = _configuration["fileOutput"];

        // Act.
        var jobId = await _mediaConvertWrapper.CreateJob(mediaConvertRole!, fileInput!, fileOutput!);
        _jobId = jobId;

        // Assert.
        Assert.False(string.IsNullOrEmpty(_jobId));
    }

    /// <summary>
    /// List jobs. The returned list should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task ListJobs_ShouldNotBeEmpty()
    {
        // Arrange.

        // Act.
        var jobs = await _mediaConvertWrapper.ListAllJobsByStatus();

        // Assert.
        Assert.NotEmpty(jobs);
    }

    /// <summary>
    /// Get a job by ID. Should not return null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task GetJob_ShouldNotBeNull()
    {
        // Arrange.

        // Act.
        var job = await _mediaConvertWrapper.GetJobById(_jobId!);

        // Assert.
        Assert.NotNull(job);
    }
}