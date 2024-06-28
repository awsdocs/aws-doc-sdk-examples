// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.CloudFormation;
using Amazon.CloudFormation.Model;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace SchedulerActions;

public class Program
{
    private static ILogger _logger = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            //.ConfigureServices((_, services) => 
                //services.AddAWSService<IAmazonService>()
                //.AddTransient<ServiceWrapper>()
            //)
            .Build();

        _logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<Program>();

    }

    private static async Task DeployCloudFormationStack()
    {
        var email = await PromptUserForEmail();
        var stackName = "EventBridgeSchedulerWorkflow";
        var templatePath = "cfn_template.yaml";

        var cloudFormationClient = new AmazonCloudFormationClient();

        var createStackRequest = new CreateStackRequest
        {
            StackName = stackName,
            TemplateBody = File.ReadAllText(templatePath),
            Parameters = new List<Parameter>
        {
            new Parameter
            {
                ParameterKey = "email",
                ParameterValue = email
            }
        },
            Capabilities = new List<string> { "CAPABILITY_NAMED_IAM" }
        };

        var createStackResponse = await cloudFormationClient.CreateStackAsync(createStackRequest);
        var stackId = createStackResponse.StackId;

        _logger.LogInformation($"CloudFormation stack creation started. StackId: {stackId}");

        var describeStacksRequest = new DescribeStacksRequest
        {
            StackName = stackId
        };

        DescribeStacksResponse describeStacksResponse;
        var stackStatus = "";

        do
        {
            describeStacksResponse = await cloudFormationClient.DescribeStacksAsync(describeStacksRequest);
            stackStatus = describeStacksResponse.Stacks[0].StackStatus;
            _logger.LogInformation($"CloudFormation stack status: {stackStatus}");
            await Task.Delay(5000);
        } while (stackStatus != "CREATE_COMPLETE");

        _logger.LogInformation("CloudFormation stack deployment successful.");

        var snsTopic = describeStacksResponse.Stacks[0].Outputs.First(o => o.OutputKey == "SNSTopicName");
        var snsTopicArn = describeStacksResponse.Stacks[0].Outputs.First(o => o.OutputKey == "SNSTopicARN");
        var roleArn = describeStacksResponse.Stacks[0].Outputs.First(o => o.OutputKey == "RoleARN");
        var eventBusArn = describeStacksResponse.Stacks[0].Outputs.First(o => o.OutputKey == "EventBusARN");

        _logger.LogInformation($"SNS Topic Name: {snsTopic.OutputValue}");
        _logger.LogInformation($"SNS Topic ARN: {snsTopicArn.OutputValue}");
        _logger.LogInformation($"Role ARN: {roleArn.OutputValue}");
        _logger.LogInformation($"EventBus ARN: {eventBusArn.OutputValue}");
    }

    private static async Task<string> PromptUserForEmail()
    {
        Console.Write("Enter an email address: ");
        string email = Console.ReadLine();

        if (!IsValidEmail(email))
        {
            _logger.LogError("Invalid email address. Please try again.");
            return await PromptUserForEmail();
        }

        return email;
    }

    private static bool IsValidEmail(string email)
    {
        try
        {
            var addr = new System.Net.Mail.MailAddress(email);
            return addr.Address == email;
        }
        catch
        {
            return false;
        }
    }
}