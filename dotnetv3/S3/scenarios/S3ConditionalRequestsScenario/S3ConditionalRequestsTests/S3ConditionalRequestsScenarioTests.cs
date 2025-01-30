// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.S3;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using S3ConditionalRequestsScenario;

namespace S3ConditionalRequestsTests;

/// <summary>
/// Tests for the Conditional Requests example.
/// </summary>
public class S3ConditionalRequestsScenarioTests
{
    private readonly IConfiguration _configuration;

    private readonly S3ActionsWrapper _s3ActionsWrapper = null!;
    private readonly string _resourcePrefix;
    private readonly ILoggerFactory _loggerFactory;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public S3ConditionalRequestsScenarioTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        _loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });

        _resourcePrefix = _configuration["resourcePrefix"] ?? "dotnet-example-test";

        _s3ActionsWrapper = new S3ActionsWrapper(
            new AmazonS3Client(), new Logger<S3ActionsWrapper>(_loggerFactory));

        S3ConditionalRequestsScenario.S3ConditionalRequestsScenario._s3ActionsWrapper = _s3ActionsWrapper;
        S3ConditionalRequestsScenario.S3ConditionalRequestsScenario._configuration = _configuration;
    }

    /// <summary>
    /// Run the setup step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        S3ConditionalRequestsScenario.S3ConditionalRequestsScenario._interactive = false;

        // Act.
        S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.ConfigurationSetup();
        var sourceName = S3ConditionalRequestsScenario.S3ConditionalRequestsScenario
            ._sourceBucketName;
        var destName = S3ConditionalRequestsScenario.S3ConditionalRequestsScenario
            ._destinationBucketName;
        var objKey = S3ConditionalRequestsScenario.S3ConditionalRequestsScenario
            ._sampleObjectKey;
        var sampleObjectEtag = await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.Setup(sourceName, destName, objKey);

        // Run all the options of the demo. No exceptions should be thrown.
        await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.DisplayDemoChoices(sourceName, destName, objKey, sampleObjectEtag, 1);
        await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.DisplayDemoChoices(sourceName, destName, objKey, sampleObjectEtag, 2);
        await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.DisplayDemoChoices(sourceName, destName, objKey, sampleObjectEtag, 3);
        await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.DisplayDemoChoices(sourceName, destName, objKey, sampleObjectEtag, 4);
        await S3ConditionalRequestsScenario.S3ConditionalRequestsScenario.Cleanup(false);

        // Assert.
        Assert.NotNull(sampleObjectEtag);
    }
}