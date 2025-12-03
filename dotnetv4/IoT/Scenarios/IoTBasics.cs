// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Text.Json;
using Amazon;
using Amazon.Extensions.NETCore.Setup;
using Amazon.IdentityManagement;
using Amazon.IoT;
//using Amazon.IoT.Model;
using Amazon.IotData;
using Amazon.SimpleNotificationService;
using IoTActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace IoTScenarios;

// snippet-start:[iot.dotnetv4.IoTScenario]
/// <summary>
/// Scenario class for AWS IoT basics workflow.
/// </summary>
public class IoTBasics
{
    public static bool IsInteractive = true;
    private static IoTWrapper _iotWrapper = null!;
    private static IAmazonSimpleNotificationService _amazonSNS = null!;
    private static IAmazonIdentityManagementService _amazonIAM = null!;
    private static ILogger<IoTBasics> _logger = null!;

    /// <summary>
    /// Main method for the IoT Basics scenario.
    /// </summary>
    /// <param name="args">Command line arguments.</param>
    /// <returns>A Task object.</returns>
    public static async Task Main(string[] args)
    {
        //var config = new ConfigurationBuilder()
        //    .AddJsonFile("appsettings.json")
        //    .Build();

        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonIoT>(new AWSOptions(){Region = RegionEndpoint.USEast1})
                        //.AddAWSService<IAmazonIotData>(new AWSOptions(){DefaultClientConfig = new AmazonIotDataConfig(){ServiceURL = "https://data.iot.us-east-1.amazonaws.com/"}})
                        .AddAWSService<IAmazonSimpleNotificationService>()
                        .AddAWSService<IAmazonIdentityManagementService>()
                        .AddTransient<IoTWrapper>()
                        .AddLogging(builder => builder.AddConsole())
                        .AddSingleton<IAmazonIotData>(sp =>
                        {
                            return new AmazonIotDataClient(
                                "https://data.iot.us-east-1.amazonaws.com/");
                        })
            )
            .Build();

        

        _logger = LoggerFactory.Create(builder => builder.AddConsole())
            .CreateLogger<IoTBasics>();

        _iotWrapper = host.Services.GetRequiredService<IoTWrapper>();
        _amazonSNS = host.Services.GetRequiredService<IAmazonSimpleNotificationService>();
        _amazonIAM = host.Services.GetRequiredService<IAmazonIdentityManagementService>();

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the AWS IoT example workflow.");
        Console.WriteLine("This example program demonstrates various interactions with the AWS Internet of Things (IoT) Core service.");
        Console.WriteLine("The program guides you through a series of steps, including creating an IoT Thing, generating a device certificate,");
        Console.WriteLine("updating the Thing with attributes, and so on. It utilizes the AWS SDK for .NET and incorporates functionalities");
        Console.WriteLine("for creating and managing IoT Things, certificates, rules, shadows, and performing searches.");
        Console.WriteLine("The program aims to showcase AWS IoT capabilities and provides a comprehensive example for");
        Console.WriteLine("developers working with AWS IoT in a .NET environment.");
        Console.WriteLine();
        if (IsInteractive)
        {
            Console.WriteLine("Press Enter to continue...");
            Console.ReadLine();
        }
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
        string thingName = $"iot-thing-{Guid.NewGuid():N}";
        string certificateArn = "";
        string certificateId = "";
        string ruleName = $"iot-rule-{Guid.NewGuid():N}";
        string snsTopicArn = "";
        string iotRoleName = "";

        try
        {
            // Step 1: Create an AWS IoT Thing
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("1. Create an AWS IoT Thing.");
            Console.WriteLine("An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.");
            Console.WriteLine();
            
            if (IsInteractive)
            {
                Console.Write("Enter Thing name: ");
                var userInput = Console.ReadLine();
                if (!string.IsNullOrEmpty(userInput))
                    thingName = userInput;
            }
            else
            {
                Console.WriteLine($"Using default Thing name: {thingName}");
            }
            
            var thingArn = await _iotWrapper.CreateThingAsync(thingName);
            Console.WriteLine($"{thingName} was successfully created. The ARN value is {thingArn}");
            Console.WriteLine(new string('-', 80));

            // Step 2: Generate a Device Certificate
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("2. Generate a device certificate.");
            Console.WriteLine("A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.");
            Console.WriteLine();
            
            var createCert = "y";
            if (IsInteractive)
            {
                Console.Write($"Do you want to create a certificate for {thingName}? (y/n)");
                createCert = Console.ReadLine();
            }
            else
            {
                Console.WriteLine($"Creating certificate for {thingName}...");
            }

            if (createCert?.ToLower() == "y")
            {
                var certificateResult = await _iotWrapper.CreateKeysAndCertificateAsync();
                if (certificateResult.HasValue)
                {
                    var (certArn, certPem, certId) = certificateResult.Value;
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
                    var attachResult = await _iotWrapper.AttachThingPrincipalAsync(thingName, certificateArn);
                    if (attachResult)
                    {
                        Console.WriteLine("Certificate attached to Thing successfully.");
                    }
                    else
                    {
                        Console.WriteLine("Failed to attach certificate to Thing.");
                    }

                    Console.WriteLine("Thing Details:");
                    Console.WriteLine($"Thing Name: {thingName}");
                    Console.WriteLine($"Thing ARN: {thingArn}");
                }
                else
                {
                    Console.WriteLine("Failed to create certificate.");
                }
            }
            Console.WriteLine(new string('-', 80));

            // Step 4: Update an AWS IoT Thing with Attributes
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("3. Update an AWS IoT Thing with Attributes.");
            Console.WriteLine("IoT Thing attributes, represented as key-value pairs, offer a pivotal advantage in facilitating efficient data");
            Console.WriteLine("management and retrieval within the AWS IoT ecosystem.");
            Console.WriteLine();
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

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
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

            var endpoint = await _iotWrapper.DescribeEndpointAsync();
            if (endpoint != null)
            {
                var subdomain = endpoint.Split('.')[0];
                Console.WriteLine($"Extracted subdomain: {subdomain}");
                Console.WriteLine($"Full Endpoint URL: https://{endpoint}");
            }
            else
            {
                Console.WriteLine("Failed to retrieve endpoint.");
            }
            Console.WriteLine(new string('-', 80));

            // Step 6: List your AWS IoT certificates
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("5. List your AWS IoT certificates");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

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
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

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
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

            var shadowData = await _iotWrapper.GetThingShadowAsync(thingName);
            Console.WriteLine($"Received Shadow Data: {shadowData}");
            Console.WriteLine(new string('-', 80));

            // Step 9: Set up resources (SNS topic and IAM role) and create a rule
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("8. Set up resources and create a rule");
            Console.WriteLine("Creates a rule that is an administrator-level action.");
            Console.WriteLine("Any user who has permission to create rules will be able to access data processed by the rule.");
            Console.WriteLine();
            
            if (IsInteractive)
            {
                Console.Write("Enter Rule name: ");
                var userRuleName = Console.ReadLine();
                if (!string.IsNullOrEmpty(userRuleName))
                    ruleName = userRuleName;
            }
            else
            {
                Console.WriteLine($"Using default rule name: {ruleName}");
            }

            // Set up SNS topic and IAM role for the IoT rule
            var topicName = $"iot-topic-{Guid.NewGuid():N}";
            iotRoleName = $"iot-role-{Guid.NewGuid():N}";
            
            Console.WriteLine("Setting up SNS topic and IAM role for the IoT rule...");
            var setupResult = await SetupAsync(topicName, iotRoleName);
            
            string roleArn = "";
            
            if (setupResult.HasValue)
            {
                (snsTopicArn, roleArn) = setupResult.Value;
                Console.WriteLine($"Successfully created SNS topic: {snsTopicArn}");
                Console.WriteLine($"Successfully created IAM role: {roleArn}");
                
                // Now create the IoT rule with the actual ARNs
                var ruleResult = await _iotWrapper.CreateTopicRuleAsync(ruleName, snsTopicArn, roleArn);
                if (ruleResult)
                {
                    Console.WriteLine("IoT Rule created successfully.");
                }
                else
                {
                    Console.WriteLine("Failed to create IoT rule.");
                }
            }
            else
            {
                Console.WriteLine("Failed to set up SNS topic and IAM role. Skipping rule creation.");
            }
            Console.WriteLine(new string('-', 80));

            // Step 10: List your rules
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("9. List your rules.");
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

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
            if (IsInteractive)
            {
                Console.WriteLine("Press Enter to continue...");
                Console.ReadLine();
            }

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
                var deleteCert = "y";
                if (IsInteractive)
                {
                    Console.Write($"Do you want to detach and delete the certificate for {thingName}? (y/n)");
                    deleteCert = Console.ReadLine();
                }
                else
                {
                    Console.WriteLine($"Detaching and deleting certificate for {thingName}...");
                }

                if (deleteCert?.ToLower() == "y")
                {
                    Console.WriteLine("11. You selected to detach and delete the certificate.");
                    if (IsInteractive)
                    {
                        Console.WriteLine("Press Enter to continue...");
                        Console.ReadLine();
                    }

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
            var deleteThing = "y";
            if (IsInteractive)
            {
                Console.Write($"Do you want to delete the IoT Thing? (y/n)");
                deleteThing = Console.ReadLine();
            }
            else
            {
                Console.WriteLine($"Deleting IoT Thing {thingName}...");
            }

            if (deleteThing?.ToLower() == "y")
            {
                await _iotWrapper.DeleteThingAsync(thingName);
                Console.WriteLine($"Deleted Thing {thingName}");
            }
            Console.WriteLine(new string('-', 80));

            // Step 14: Clean up SNS topic and IAM role
            if (!string.IsNullOrEmpty(snsTopicArn) && !string.IsNullOrEmpty(iotRoleName))
            {
                Console.WriteLine(new string('-', 80));
                Console.WriteLine("13. Clean up SNS topic and IAM role.");
                Console.WriteLine("Cleaning up the resources created for the IoT rule...");
                
                var cleanupSuccess = await CleanupAsync(snsTopicArn, iotRoleName);
                if (cleanupSuccess)
                {
                    Console.WriteLine("Successfully cleaned up SNS topic and IAM role.");
                }
                else
                {
                    Console.WriteLine("Some cleanup operations failed. Check the logs for details.");
                }
                Console.WriteLine(new string('-', 80));
            }
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

            // Clean up SNS topic and IAM role on error
            if (!string.IsNullOrEmpty(snsTopicArn) && !string.IsNullOrEmpty(iotRoleName))
            {
                try
                {
                    await CleanupAsync(snsTopicArn, iotRoleName);
                }
                catch (Exception cleanupEx)
                {
                    _logger.LogError(cleanupEx, "Error during SNS and IAM cleanup.");
                }
            }
            
            throw;
        }
    }

    // snippet-start:[iot.dotnetv4.Setup]
    /// <summary>
    /// Sets up the necessary resources for the IoT scenario (SNS topic and IAM role).
    /// </summary>
    /// <param name="topicName">The name of the SNS topic to create.</param>
    /// <param name="roleName">The name of the IAM role to create.</param>
    /// <returns>A tuple containing the SNS topic ARN and IAM role ARN, or null if setup failed.</returns>
    private static async Task<(string SnsTopicArn, string RoleArn)?> SetupAsync(string topicName, string roleName)
    {
        try
        {
            // Create SNS topic
            var createTopicRequest = new Amazon.SimpleNotificationService.Model.CreateTopicRequest
            {
                Name = topicName
            };

            var topicResponse = await _amazonSNS.CreateTopicAsync(createTopicRequest);
            var snsTopicArn = topicResponse.TopicArn;
            _logger.LogInformation($"Created SNS topic {topicName} with ARN {snsTopicArn}");

            // Create IAM role for IoT
            var trustPolicy = @"{
                ""Version"": ""2012-10-17"",
                ""Statement"": [
                    {
                        ""Effect"": ""Allow"",
                        ""Principal"": {
                            ""Service"": ""iot.amazonaws.com""
                        },
                        ""Action"": ""sts:AssumeRole""
                    }
                ]
            }";

            var createRoleRequest = new Amazon.IdentityManagement.Model.CreateRoleRequest
            {
                RoleName = roleName,
                AssumeRolePolicyDocument = trustPolicy,
                Description = "Role for AWS IoT to publish to SNS topic"
            };

            var roleResponse = await _amazonIAM.CreateRoleAsync(createRoleRequest);
            var roleArn = roleResponse.Role.Arn;
            _logger.LogInformation($"Created IAM role {roleName} with ARN {roleArn}");

            // Attach policy to allow SNS publishing
            var policyDocument = $@"{{
                ""Version"": ""2012-10-17"",
                ""Statement"": [
                    {{
                        ""Effect"": ""Allow"",
                        ""Action"": ""sns:Publish"",
                        ""Resource"": ""{snsTopicArn}""
                    }}
                ]
            }}";

            var putRolePolicyRequest = new Amazon.IdentityManagement.Model.PutRolePolicyRequest
            {
                RoleName = roleName,
                PolicyName = "IoTSNSPolicy",
                PolicyDocument = policyDocument
            };

            await _amazonIAM.PutRolePolicyAsync(putRolePolicyRequest);
            _logger.LogInformation($"Attached SNS policy to role {roleName}");

            // Wait a bit for the role to propagate
            await Task.Delay(10000);

            return (snsTopicArn, roleArn);
        }
        catch (Exception ex)
        {
            _logger.LogError($"Couldn't set up resources. Here's why: {ex.Message}");
            return null;
        }
    }
    // snippet-end:[iot.dotnetv4.Setup]

    // snippet-start:[iot.dotnetv4.Cleanup]
    /// <summary>
    /// Cleans up the resources created during setup (SNS topic and IAM role).
    /// </summary>
    /// <param name="snsTopicArn">The ARN of the SNS topic to delete.</param>
    /// <param name="roleName">The name of the IAM role to delete.</param>
    /// <returns>True if cleanup was successful, false otherwise.</returns>
    private static async Task<bool> CleanupAsync(string snsTopicArn, string roleName)
    {
        var success = true;

        try
        {
            // Delete role policy first
            try
            {
                var deleteRolePolicyRequest = new Amazon.IdentityManagement.Model.DeleteRolePolicyRequest
                {
                    RoleName = roleName,
                    PolicyName = "IoTSNSPolicy"
                };

                await _amazonIAM.DeleteRolePolicyAsync(deleteRolePolicyRequest);
                _logger.LogInformation($"Deleted role policy for {roleName}");
            }
            catch (Exception ex)
            {
                _logger.LogWarning($"Failed to delete role policy: {ex.Message}");
                success = false;
            }

            // Delete IAM role
            try
            {
                var deleteRoleRequest = new Amazon.IdentityManagement.Model.DeleteRoleRequest
                {
                    RoleName = roleName
                };

                await _amazonIAM.DeleteRoleAsync(deleteRoleRequest);
                _logger.LogInformation($"Deleted IAM role {roleName}");
            }
            catch (Exception ex)
            {
                _logger.LogWarning($"Failed to delete IAM role: {ex.Message}");
                success = false;
            }

            // Delete SNS topic
            try
            {
                var deleteTopicRequest = new Amazon.SimpleNotificationService.Model.DeleteTopicRequest
                {
                    TopicArn = snsTopicArn
                };

                await _amazonSNS.DeleteTopicAsync(deleteTopicRequest);
                _logger.LogInformation($"Deleted SNS topic {snsTopicArn}");
            }
            catch (Exception ex)
            {
                _logger.LogWarning($"Failed to delete SNS topic: {ex.Message}");
                success = false;
            }

            return success;
        }
        catch (Exception ex)
        {
            _logger.LogError($"Couldn't clean up resources. Here's why: {ex.Message}");
            return false;
        }
    }
    // snippet-end:[iot.dotnetv4.Cleanup]
}
// snippet-end:[iot.dotnetv4.IoTScenario]
