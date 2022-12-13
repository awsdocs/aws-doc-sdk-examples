using Amazon.EC2;
using CreateVPCforS3Example;
using Microsoft.Extensions.Configuration;
using Xunit.Extensions.Ordering;

namespace CreateVPCTests;

/// <summary>
/// Tests for the VPC endpoint examples.
/// </summary>
public class CreateVPCforS3Tests
{
    private readonly IConfiguration _configuration;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public CreateVPCforS3Tests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

    }

    /// <summary>
    /// Verify that we can create the VPC endpoint.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestCreateVPCforS3Endpoint()
    {
        var result = await CreateVPCforS3.CreateVPCforS3Client(_configuration, new AmazonEC2Client());

        Assert.NotNull(result);
    }

    /// <summary>
    /// Verify that we can create an S3 client using an endpoint.
    /// </summary>
    /// <returns></returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task TestUseEndpointInS3Client()
    {
        var result = await CreateVPCforS3.CreateS3ClientWithEndpoint(_configuration, "https://s3.amazonaws.com");

        Assert.NotEmpty(result);
    }
}