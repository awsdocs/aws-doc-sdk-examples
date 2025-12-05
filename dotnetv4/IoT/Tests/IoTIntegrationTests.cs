// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.IoT;
using Amazon.IotData;
using IoTActions;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Xunit;
using Xunit.Abstractions;

namespace IoTTests;

/// <summary>
/// Integration tests for the IoT wrapper methods.
/// </summary>
public class IoTIntegrationTests
{
    private readonly ITestOutputHelper _output;
    private readonly IoTWrapper _iotWrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    /// <param name="output">ITestOutputHelper object.</param>
    public IoTIntegrationTests(ITestOutputHelper output)
    {
        _output = output;

        // Set up dependency injection for the Amazon service.
        var host = Host.CreateDefaultBuilder()
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonIoT>()
                        .AddAWSService<IAmazonIotData>()
                        .AddTransient<IoTWrapper>()
                        .AddLogging(builder => builder.AddConsole())
            )
            .Build();

        _iotWrapper = host.Services.GetRequiredService<IoTWrapper>();
    }

    /// <summary>
    /// Test the IoT wrapper methods by running through the scenario.
    /// </summary>
    /// <returns>A Task object.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task IoTWrapperMethodsTest()
    {
        // Set to non-interactive mode for testing
        IoTBasics.IoTBasics.IsInteractive = false;
        
        var thingName = $"test-thing-{Guid.NewGuid():N}";
        var certificateArn = "";
        var certificateId = "";

        try
        {
            _output.WriteLine("Starting IoT integration test...");

            // 1. Create an IoT Thing
            _output.WriteLine($"Creating IoT Thing: {thingName}");
            var thingArn = await _iotWrapper.CreateThingAsync(thingName);
            Assert.False(string.IsNullOrEmpty(thingArn));
            _output.WriteLine($"Created Thing with ARN: {thingArn}");

            // 2. Create a certificate
            _output.WriteLine("Creating device certificate...");
            var certificateResult = await _iotWrapper.CreateKeysAndCertificateAsync();
            Assert.True(certificateResult.HasValue);
            var (certArn, certPem, certId) = certificateResult.Value;
            certificateArn = certArn;
            certificateId = certId;
            Assert.False(string.IsNullOrEmpty(certificateArn));
            Assert.False(string.IsNullOrEmpty(certPem));
            Assert.False(string.IsNullOrEmpty(certificateId));
            _output.WriteLine($"Created certificate with ARN: {certificateArn}");

            // 3. Attach certificate to Thing
            _output.WriteLine("Attaching certificate to Thing...");
            var attachResult = await _iotWrapper.AttachThingPrincipalAsync(thingName, certificateArn);
            Assert.True(attachResult);

            // 4. Update Thing with attributes
            _output.WriteLine("Updating Thing attributes...");
            var attributes = new Dictionary<string, string>
            {
                { "TestAttribute", "TestValue" },
                { "Environment", "Testing" }
            };
            var updateResult = await _iotWrapper.UpdateThingAsync(thingName, attributes);
            Assert.True(updateResult);

            // 5. Get IoT endpoint
            _output.WriteLine("Getting IoT endpoint...");
            var endpoint = await _iotWrapper.DescribeEndpointAsync();
            Assert.False(string.IsNullOrEmpty(endpoint));
            _output.WriteLine($"Retrieved endpoint: {endpoint}");

            // 6. List certificates
            _output.WriteLine("Listing certificates...");
            var certificates = await _iotWrapper.ListCertificatesAsync();
            Assert.NotNull(certificates);
            Assert.True(certificates.Count > 0);
            _output.WriteLine($"Found {certificates.Count} certificates");

            // 7. Update Thing shadow
            _output.WriteLine("Updating Thing shadow...");
            var shadowPayload = """{"state": {"desired": {"temperature": 22, "humidity": 45}}}""";
            var shadowResult = await _iotWrapper.UpdateThingShadowAsync(thingName, shadowPayload);
            Assert.True(shadowResult);

            // 8. Get Thing shadow
            _output.WriteLine("Getting Thing shadow...");
            var shadowData = await _iotWrapper.GetThingShadowAsync(thingName);
            Assert.False(string.IsNullOrEmpty(shadowData));
            _output.WriteLine($"Retrieved shadow data: {shadowData}");

            // 9. List topic rules
            _output.WriteLine("Listing topic rules...");
            var rules = await _iotWrapper.ListTopicRulesAsync();
            Assert.NotNull(rules);
            _output.WriteLine($"Found {rules.Count} IoT rules");

            // 10. Search Things
            _output.WriteLine("Searching for Things...");
            var searchResults = await _iotWrapper.SearchIndexAsync($"thingName:{thingName}");
            Assert.NotNull(searchResults);
            // Note: Search may not immediately return results for newly created Things
            _output.WriteLine($"Search returned {searchResults.Count} results");

            // 11. List Things
            _output.WriteLine("Listing Things...");
            var things = await _iotWrapper.ListThingsAsync();
            Assert.NotNull(things);
            Assert.True(things.Count > 0);
            _output.WriteLine($"Found {things.Count} Things");

            _output.WriteLine("IoT integration test completed successfully!");
        }
        finally
        {
            // Cleanup resources
            try
            {
                if (!string.IsNullOrEmpty(certificateArn))
                {
                    _output.WriteLine("Cleaning up: Detaching certificate from Thing...");
                    await _iotWrapper.DetachThingPrincipalAsync(thingName, certificateArn);
                    
                    _output.WriteLine("Cleaning up: Deleting certificate...");
                    await _iotWrapper.DeleteCertificateAsync(certificateId);
                }

                _output.WriteLine("Cleaning up: Deleting Thing...");
                await _iotWrapper.DeleteThingAsync(thingName);
                
                _output.WriteLine("Cleanup completed successfully.");
            }
            catch (Exception ex)
            {
                _output.WriteLine($"Warning: Cleanup failed: {ex.Message}");
            }
        }
    }
}
