// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.IoT;
using Amazon.IotData;
using Amazon.IoT.Model;
using IoTActions;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using System.Text.Json;

namespace IoTScenarios;

// snippet-start:[iot.dotnetv4.IoTScenario]
/// <summary>
/// Scenario class for AWS IoT basics workflow.
/// </summary>
public class IoTBasics
{
    private static IoTWrapper _iotWrapper = null!;
    private static ILogger<IoTBasics> _logger = null!;

    /// <summary>
    /// Main method for the IoT Basics scenario.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    /// <returns>A Task object.</returns>
    public static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonIoT>()
                        .AddAWSService<IAmazonIotData>()
                        .AddTransient<IoTWrapper>()
                        .AddLogging(builder => builder.AddConsole())
            )
            .Build();

        _logger = LoggerFactory.Create(builder => builder.AddConsole())
            .CreateLogger<IoTBasics>();

        _iotWrapper = host.Services.GetRequiredService<IoTWrapper>();

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the AWS IoT example workflow.");
        Console.WriteLine("This example program demonstrates various interactions with the AWS Internet of Things (IoT) Core service.");
        Console.WriteLine("The program guides you through a series of steps, including creating an IoT Thing, generating a device certificate,");
        Console.WriteLine("updating the Thing with attributes, and so on. It utilizes the AWS SDK for .NET and incorporates functionalities");
        Console.WriteLine("for creating and managing IoT Things, certificates, rules, shadows, and performing searches.");
        Console.WriteLine("The program aims to showcase AWS IoT capabilities and provides a comprehensive example for");
        Console.WriteLine("developers working with AWS IoT in a .NET environment.");
        Console.WriteLine();
        Console.WriteLine("Press Enter to continue...");
        Console.ReadLine();
        Console.WriteLine(new string('-', 80));

        try
        {
            await RunScenarioAsync();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "There was a problem running the scenario.");
            Console.WriteLine($"\nAn error occurred: {ex.Message}");
        }

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("The AWS IoT workflow has successfully completed.");
        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Run the IoT Basics scenario.
    /// </summary>
    /// <returns>A Task object.</returns>
    private static async Task RunScenarioAsync()
    {
        string thingName = "";
        string certificateArn = "";
        string certificateId = "";
        string ruleName = "";

        try
        {
            // Step 1: Create an AWS IoT Thing
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("1. Create an AWS IoT Thing.");
            Console.WriteLine("An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.");
            Console.WriteLine();
            Console.Write("Enter Thing name: ");
            thingName = Console.ReadLine()!;
            
            var thingArn = await _iotWrapper.CreateThingAsync(thingName);
            Console.WriteLine($"{thingName} was successfully created. The ARN value is {thingArn}");
            Console.WriteLine(new string('-', 80));

            // Step 2: Generate a Device Certificate
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("2. Generate a device certificate.");
            Console.WriteLine("A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.");
            Console.WriteLine();
            Console.Write($"Do you want to create a certificate for {thingName}? (y/n)");
            var createCert = Console.ReadLine();

            if (createCert?.ToLower() == "y")
            {
                var (certArn, certPem, certId) = await _iotWrapper.CreateKeysAndCertificateAsync();
                certificateArn = certArn;
                certificateId = certId;

                Console.WriteLine($"\nCertificate:");
                // Show only first few lines of certificate for brevity
                var lines = certPem.Split('\n');
                for (int i = 0; i < Math.Min(lines.Length, 5); i++)
                {
                    Console.WriteLine(lines[i]);
                }
                if (lines.Length > 5)
                {
                    Console.WriteLine("...");
                }

                Console.WriteLine($"\nCertificate ARN:");
                Console.WriteLine(certificateArn);

                // Step 3: Attach the Certificate to the AWS IoT Thing
                Console.WriteLine("Attach the certificate to the AWS IoT Thing.");
                await _iotWrapper.AttachThingPrincipalAsync(thingName, certificateArn);
                Console.WriteLine("Certificate attached to Thing successfully.");

                Console.WriteLine("Thing Details:");
                Console.WriteLine($"Thing Name: {thingName}");
                Console.WriteLine($"Thing ARN: {thingArn}");
            }
            Console.WriteLine(new string('-', 80));

            // Step 4: Update an AWS IoT Thing with Attributes
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("3. Update an AWS IoT Thing with Attributes.");
            Console.WriteLine("IoT Thing attributes, represented as key-value pairs, offer a pivotal advantage in facilitating efficient data");
            Console.WriteLine("management and retrieval within the AWS IoT ecosystem.");
            Console.WriteLine();
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var attributes = new Dictionary<string, string>
            {
                { "Location", "Seattle" },
                { "DeviceType", "Sensor" },
                { "Firmware", "1.2.3" }
            };

            await _iotWrapper.UpdateThingAsync(thingName, attributes);
            Console.WriteLine("Thing attributes updated successfully.");
            Console.WriteLine(new string('-', 80));

            // Step 5: Return a unique endpoint specific to the Amazon Web Services account
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("4. Return a unique endpoint specific to the Amazon Web Services account.");
            Console.WriteLine("An IoT Endpoint refers to a specific URL or Uniform Resource Locator that serves as the entry point for communication between IoT devices and the AWS IoT service.");
            Console.WriteLine();
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var endpoint = await _iotWrapper.DescribeEndpointAsync();
            var subdomain = endpoint.Split('.')[0];
            Console.WriteLine($"Extracted subdomain: {subdomain}");
            Console.WriteLine($"Full Endpoint URL: https://{endpoint}");
            Console.WriteLine(new string('-', 80));

            // Step 6: List your AWS IoT certificates
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("5. List your AWS IoT certificates");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var certificates = await _iotWrapper.ListCertificatesAsync();
            foreach (var cert in certificates.Take(5)) // Show first 5 certificates
            {
                Console.WriteLine($"Cert id: {cert.CertificateId}");
                Console.WriteLine($"Cert Arn: {cert.CertificateArn}");
            }
            Console.WriteLine();
            Console.WriteLine(new string('-', 80));

            // Step 7: Create an IoT shadow
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("6. Create an IoT shadow that refers to a digital representation or virtual twin of a physical IoT device");
            Console.WriteLine("A Thing Shadow refers to a feature that enables you to create a virtual representation, or \"shadow,\"");
            Console.WriteLine("of a physical device or thing. The Thing Shadow allows you to synchronize and control the state of a device between");
            Console.WriteLine("the cloud and the device itself. and the AWS IoT service. For example, you can write and retrieve JSON data from a Thing Shadow.");
            Console.WriteLine();
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var shadowPayload = JsonSerializer.Serialize(new
            {
                state = new
                {
                    desired = new
                    {
                        temperature = 25,
                        humidity = 50
                    }
                }
            });

            await _iotWrapper.UpdateThingShadowAsync(thingName, shadowPayload);
            Console.WriteLine("Thing Shadow updated successfully.");
            Console.WriteLine(new string('-', 80));

            // Step 8: Write out the state information, in JSON format
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("7. Write out the state information, in JSON format.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var shadowData = await _iotWrapper.GetThingShadowAsync(thingName);
            Console.WriteLine($"Received Shadow Data: {shadowData}");
            Console.WriteLine(new string('-', 80));

            // Step 9: Creates a rule
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("8. Creates a rule");
            Console.WriteLine("Creates a rule that is an administrator-level action.");
            Console.WriteLine("Any user who has permission to create rules will be able to access data processed by the rule.");
            Console.WriteLine();
            Console.Write("Enter Rule name: ");
            ruleName = Console.ReadLine()!;

            // Note: For demonstration, we'll use placeholder ARNs
            // In real usage, these should be actual SNS topic and IAM role ARNs
            var snsTopicArn = "arn:aws:sns:us-east-1:123456789012:example-topic";
            var roleArn = "arn:aws:iam::123456789012:role/IoTRole";
            
            Console.WriteLine("Note: Using placeholder ARNs for SNS topic and IAM role.");
            Console.WriteLine("In production, ensure these ARNs exist and have proper permissions.");
            
            try
            {
                await _iotWrapper.CreateTopicRuleAsync(ruleName, snsTopicArn, roleArn);
                Console.WriteLine("IoT Rule created successfully.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Note: Rule creation failed (expected with placeholder ARNs): {ex.Message}");
            }
            Console.WriteLine(new string('-', 80));

            // Step 10: List your rules
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("9. List your rules.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var rules = await _iotWrapper.ListTopicRulesAsync();
            Console.WriteLine("List of IoT Rules:");
            foreach (var rule in rules.Take(5)) // Show first 5 rules
            {
                Console.WriteLine($"Rule Name: {rule.RuleName}");
                Console.WriteLine($"Rule ARN: {rule.RuleArn}");
                Console.WriteLine("--------------");
            }
            Console.WriteLine();
            Console.WriteLine(new string('-', 80));

            // Step 11: Search things using the Thing name
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("10. Search things using the Thing name.");
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();

            var searchResults = await _iotWrapper.SearchIndexAsync($"thingName:{thingName}");
            if (searchResults.Any())
            {
                Console.WriteLine($"Thing id found using search is {searchResults.First().ThingId}");
            }
            else
            {
                Console.WriteLine($"No search results found for Thing: {thingName}");
            }
            Console.WriteLine(new string('-', 80));

            // Step 12: Cleanup - Detach and delete certificate
            if (!string.IsNullOrEmpty(certificateArn))
            {
                Console.WriteLine(new string('-', 80));
                Console.Write($"Do you want to detach and delete the certificate for {thingName}? (y/n)");
                var deleteCert = Console.ReadLine();

                if (deleteCert?.ToLower() == "y")
                {
                    Console.WriteLine("11. You selected to detach and delete the certificate.");
                    Console.WriteLine("Press Enter to continue...");
                    Console.ReadLine();

                    await _iotWrapper.DetachThingPrincipalAsync(thingName, certificateArn);
                    Console.WriteLine($"{certificateArn} was successfully removed from {thingName}");

                    await _iotWrapper.DeleteCertificateAsync(certificateId);
                    Console.WriteLine($"{certificateArn} was successfully deleted.");
                }
                Console.WriteLine(new string('-', 80));
            }

            // Step 13: Delete the AWS IoT Thing
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("12. Delete the AWS IoT Thing.");
            Console.Write($"Do you want to delete the IoT Thing? (y/n)");
            var deleteThing = Console.ReadLine();

            if (deleteThing?.ToLower() == "y")
            {
                await _iotWrapper.DeleteThingAsync(thingName);
                Console.WriteLine($"Deleted Thing {thingName}");
            }
            Console.WriteLine(new string('-', 80));
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error occurred during scenario execution.");
            
            // Cleanup on error
            if (!string.IsNullOrEmpty(certificateArn) && !string.IsNullOrEmpty(thingName))
            {
                try
                {
                    await _iotWrapper.DetachThingPrincipalAsync(thingName, certificateArn);
                    await _iotWrapper.DeleteCertificateAsync(certificateId);
                }
                catch (Exception cleanupEx)
                {
                    _logger.LogError(cleanupEx, "Error during cleanup.");
                }
            }
            
            if (!string.IsNullOrEmpty(thingName))
            {
                try
                {
                    await _iotWrapper.DeleteThingAsync(thingName);
                }
                catch (Exception cleanupEx)
                {
                    _logger.LogError(cleanupEx, "Error during Thing cleanup.");
                }
            }
            
            throw;
        }
    }
}
// snippet-end:[iot.dotnetv4.IoTScenario]
