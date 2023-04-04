using Amazon.MediaConvert;
using MediaConvertActions;
using Microsoft.Extensions.Configuration;
using Xunit.Extensions.Ordering;

namespace MediaConvertTests;

/// <summary>
/// Tests for the MediaConvertWrapper class.
/// </summary>
public class MediaConvertTests
{
    private readonly IConfiguration _configuration;
    private readonly MediaConvertWrapper _mediaConvertWrapper;

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
    /// Create a job. The returned job Id should not be empty.
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
        var id = await _mediaConvertWrapper.CreateJob(mediaConvertRole!, fileInput!, fileOutput!);

        // Assert.
        Assert.False(string.IsNullOrEmpty(id));
    }
}