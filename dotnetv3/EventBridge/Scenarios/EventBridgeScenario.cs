// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.EventBridge;
using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;
using EventBridgeActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace EventBridgeScenario;

public class EventBridgeScenario
{
    /*
    Before running this .NET code example, set up your development environment, including your credentials.

    This .NET example performs the following tasks:

    */

    private static ILogger logger = null!;
    private static EventBridgeWrapper _eventBridgeWrapper = null!;
    private static IConfiguration _configuration = null!;

    private static AmazonIdentityManagementServiceClient? _iamClient = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
            services.AddAWSService<IAmazonEventBridge>()
            .AddAWSService<IAmazonIdentityManagementService>()
            .AddAWSService<IAmazonS3>()
            .AddAWSService<IAmazonSimpleNotificationService>()
            .AddTransient<EventBridgeWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<EventBridgeScenario>();

        _eventBridgeWrapper = host.Services.GetRequiredService<EventBridgeWrapper>();
        var snsClient =
            host.Services.GetRequiredService<IAmazonSimpleNotificationService>();
        var s3Client =
            host.Services.GetRequiredService<IAmazonS3>();

        string topicArn = "";

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the Amazon EventBridge example scenario.");
        Console.WriteLine(new string('-', 80));

        try
        {
            Console.WriteLine(new string('-', 80));
            Console.WriteLine("Creating a role to use with EventBridge.");
            Console.WriteLine(new string('-', 80));

            var roleArn = await CreateRole();

            await CreateBucketWithEventBridgeEvents(s3Client);

            topicArn = await CreateSnsTopic(snsClient);

            var email = await SubscribeToSnsTopic(snsClient, topicArn);

            await AddSnsEventRule(roleArn, topicArn);

            await ListEventRules();

            await ListRulesForTarget();

            await ListTargets();

            await UploadS3File(s3Client);

            await ChangeRuleState(false);

            await UpdateSnsEventRule(roleArn);

            await ChangeRuleState(true);

            await UploadS3File(s3Client);

            await UpdateToCustomRule();

            await TriggerCustomRule(email);

            await CleanupResources(snsClient, s3Client, topicArn);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "There was a problem executing the scenario.");
            await CleanupResources(snsClient, s3Client, topicArn);
        }
    }

    // snippet-start:[EventBridge.dotnetv3.CreateRole]
    public static async Task<string> CreateRole()
    {
        var roleName = _configuration["roleName"];

        string assumeRolePolicy = @"{
            'Version': '2012-10-17',
            'Statement':
            [
                {
                    'Effect': 'Allow',
                    'Principal': {
                    'Service': 'events.amazonaws.com'
                    },
                    'Action': 'sts:AssumeRole'
                },
            ],
        }";

        var roleResult = await _iamClient.CreateRoleAsync(
            new CreateRoleRequest()
            {
                AssumeRolePolicyDocument = assumeRolePolicy,
                Path = "/",
                RoleName = roleName
            });

        string addAccessPolicy = @"{
            'Version': '2012-10-17',
            'Statement': [
                {
                  'Sid': ""CloudWatchEventsFullAccess"",
                  'Effect': ""Allow"",
                  'Resource': ""*"",
                  'Action': ""events:*""
                },
                {
                  'Sid': ""IAMPassRoleForCloudWatchEvents"",
                  'Effect': ""Allow"",
                  'Resource': ""arn:aws:iam::*:role/AWS_Events_Invoke_Targets"",
                  'Action': ""iam:PassRole""
                }
            ]
        }";

        await _iamClient.PutRolePolicyAsync(
            new PutRolePolicyRequest()
            {
                PolicyName = "CloudWatchEventsPolicy",
                RoleName = roleName,
                PolicyDocument = addAccessPolicy
            });

        return roleResult.Role.Arn;
    }
    // snippet-end:[EventBridge.dotnetv3.CreateRole]

    // snippet-start:[EventBridge.dotnetv3.CreateBucketWithEvents]
    /// <summary>
    /// Create an Amazon S3 bucket with EventBridge events enabled
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task CreateBucketWithEventBridgeEvents(IAmazonS3 s3Client)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Creating an Amazon S3 bucket with EventBridge events enabled.");

        var testBucketName = _configuration["testBucketName"];

        var bucketExists = await Amazon.S3.Util.AmazonS3Util.DoesS3BucketExistV2Async(s3Client,
            testBucketName);

        if (!bucketExists)
        {
            await s3Client.PutBucketAsync(new PutBucketRequest()
            {
                BucketName = testBucketName,
                UseClientRegion = true
            });
        }

        await s3Client.PutBucketNotificationAsync(new PutBucketNotificationRequest()
        {
           BucketName = testBucketName,
           EventBridgeConfiguration = new EventBridgeConfiguration()
        });

        Console.WriteLine($"\tAdded bucket {testBucketName} with EventBridge events enabled.");

        Console.WriteLine(new string('-', 80));
    }
    // snippet-end:[EventBridge.dotnetv3.CreateBucketWithEvents]

    /// <summary>
    /// Create and upload a file to an S3 bucket to trigger an event.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task UploadS3File(IAmazonS3 s3Client)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Uploading a file to the test bucket.");

        var testBucketName = _configuration["testBucketName"];

        var fileName = $"example_upload_{DateTime.UtcNow.Ticks}.txt";

        // Create the file if it does not already exist.
        if (!File.Exists(fileName))
        {
            await using StreamWriter sw = File.CreateText(fileName);
            await sw.WriteLineAsync(
                "This is a sample file for testing uploads.");
        }

        await s3Client.PutObjectAsync(new PutObjectRequest()
        {
            FilePath = fileName,
            BucketName = testBucketName
        });

        Console.WriteLine($"\tPress Enter to continue.");
        Console.ReadLine();

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Create an SNS topic that can used as an EventBridge target.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task<string> CreateSnsTopic(
        IAmazonSimpleNotificationService snsClient)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine(
            "Creating an Amazon Simple Notification Service (Amazon SNS) topic for email subscriptions.");

        var topicName = _configuration["topicName"];
        var topicPolicy = @"{
            'Version': '2012-10-17',
            'Statement': [
                {
                  'Sid': ""EventBridgePublishTopic"",
                  'Effect': ""Allow"",
                  `Principal`: {
                    'Service': ""events.amazonaws.com""
                    },
                  'Resource': ""*"",
                  'Action': ""sns:Publish""
                }
            ]
        }";

        var topicAttributes = new Dictionary<string, string>()
        {
            { "Policy", topicPolicy }
        };

        var topicResponse = await snsClient.CreateTopicAsync(new CreateTopicRequest()
        {
            Name = topicName,
            Attributes = topicAttributes
            
        });

        Console.WriteLine($"\tAdded topic {topicName} for email subscriptions.");

        Console.WriteLine(new string('-', 80));

        return topicResponse.TopicArn;
    }

    /// <summary>
    /// Create an Amazon S3 bucket with EventBridge events enabled
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task<string> SubscribeToSnsTopic(IAmazonSimpleNotificationService snsClient, string topicArn)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Enter your email to subscribe to the Amazon SNS topic:");

        var email = Console.ReadLine();

        var subscriptions = new List<string>();
        var paginatedSubscriptions = snsClient.Paginators.ListSubscriptionsByTopic(
            new ListSubscriptionsByTopicRequest()
            {
                TopicArn = topicArn
            });

        // Get the entire list using the paginator.
        await foreach (var subscription in paginatedSubscriptions.Subscriptions)
        {
            subscriptions.Add(subscription.Endpoint);
        }

        if (subscriptions.Contains(email))
        {
            Console.WriteLine($"\tYour email is already subscribed.");
            Console.WriteLine(new string('-', 80));
            return email;
        }

        await snsClient.SubscribeAsync(new SubscribeRequest()
        {
            TopicArn = topicArn,
            Protocol = "email",
            Endpoint = email
        });

        Console.WriteLine($"\tEnter the subscription confirmation code to confirm your subscription.");

        var code = Console.ReadLine();

        await snsClient.ConfirmSubscriptionAsync(new ConfirmSubscriptionRequest()
        {
            TopicArn = topicArn,
            Token = code
        });

        Console.WriteLine($"\tYou are now subscribed to emails for the test topic.");
        
        Console.WriteLine(new string('-', 80));
        return email;
    }

    /// <summary>
    /// Add a rule which triggers an SNS target when a file is uploaded to an S3 bucket.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task AddSnsEventRule(string roleArn, string topicArn)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Creating an EventBridge event that sends an email when an Amazon S3 object is created.");

        var eventRuleName = _configuration["eventRuleName"];
        var testBucketName = _configuration["testBucketName"];
        var topicName = _configuration["topicName"];

        await _eventBridgeWrapper.PutS3UploadRule(roleArn, eventRuleName, testBucketName);
        await _eventBridgeWrapper.AddSnsTargetToRule(eventRuleName, roleArn, topicArn);
        Console.WriteLine($"\tAdded event rule {eventRuleName} with SNS target {topicName} for bucket {testBucketName}.");

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Add a rule which triggers an SNS target when a file is uploaded to an S3 bucket.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task ListEventRules()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Current event rules:");

        var rules = await _eventBridgeWrapper.ListAllRulesForEventBus();
        rules.ForEach(r => Console.WriteLine($"\tRule: {r.Name} Description: {r.Description} State: {r.State}"));

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Add a rule which triggers an SNS target when a file is uploaded to an S3 bucket.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task UpdateSnsEventRule(string roleArn)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Let's update the event target with a transform.");

        var eventRuleName = _configuration["eventRuleName"];
        var testBucketName = _configuration["testBucketName"];
        var topicArn = _configuration["topicArn"];
        var topicName = _configuration["topicName"];

        await _eventBridgeWrapper.UpdateS3UploadRuleTargetWithTransform(eventRuleName, roleArn, topicArn);
        Console.WriteLine($"\tUpdated event rule {eventRuleName} with SNS target {topicName} for bucket {testBucketName}.");

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Add a rule which triggers an SNS target when a file is uploaded to an S3 bucket.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task UpdateToCustomRule()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Let's update the event pattern to be triggered by a custom event instead.");

        var eventRuleName = _configuration["eventRuleName"];

        await _eventBridgeWrapper.UpdateCustomEventPattern(eventRuleName);

        Console.WriteLine($"\tUpdated event rule {eventRuleName} to custom pattern.");

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Add a rule which triggers an SNS target when a file is uploaded to an S3 bucket.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task TriggerCustomRule(string email)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Let's send some events to trigger the rule.");

        await _eventBridgeWrapper.PutCustomEmailEvent(email);

        Console.WriteLine($"\tEvents have been sent. Press enter to continue.");
        Console.ReadLine();

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// List all of the targets for a rule.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task ListTargets()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("We can list all of the targets for a particular rule.");
        var eventRuleName = _configuration["eventRuleName"];
        var targets = await _eventBridgeWrapper.ListAllTargetsOnRule(eventRuleName);
        targets.ForEach(t => Console.WriteLine($"\tTarget: {t.Arn} Id: {t.Id} Input: {t.Input}"));

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// List all of the rules for a particular target.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task ListRulesForTarget()
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine("We can list all of the rules for a particular target.");

        var topicArn = _configuration["topicArn"];

        var rules = await _eventBridgeWrapper.ListAllRuleNamesByTarget(topicArn);
        rules.ForEach(r => Console.WriteLine($"\tRule: {r}"));

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// List all of the rules for a particular target.
    /// </summary>
    /// <returns>Async task.</returns>
    private static async Task ChangeRuleState(bool isEnabled)
    {
        Console.WriteLine(new string('-', 80));
        var eventRuleName = _configuration["eventRuleName"];

        if (!isEnabled)
        {
            Console.WriteLine($"Disabling the rule: {eventRuleName}");
            await _eventBridgeWrapper.DisableRuleByName(eventRuleName);
        }
        else
        {
            Console.WriteLine($"Enabling the rule: {eventRuleName}");
            await _eventBridgeWrapper.EnableRuleByName(eventRuleName);
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Clean up created resources.
    /// </summary>
    /// <param name="s3Client">The SNS client.</param>
    /// <param name="s3Client">The S3 client.</param>
    /// <param name="topicArn">The Arn of the SNS topic to clean up.</param>
    /// <returns>Async task.</returns>
    private static async Task CleanupResources(IAmazonSimpleNotificationService snsClient, IAmazonS3 s3Client, string topicArn)
    {
        Console.WriteLine(new string('-', 80));
        Console.WriteLine($"Clean up resources.");

        var eventRuleName = _configuration["eventRuleName"];
        if (GetYesNoResponse($"\tDelete event rule {eventRuleName}? (y/n)"))
        {
            Console.WriteLine($"\tDeleting event rule.");
            await _eventBridgeWrapper.DeleteRuleByName(eventRuleName);
        }

        var topicName = _configuration["topicName"];
        if (GetYesNoResponse($"\tDelete Amazon SNS subscription topic {topicName}? (y/n)"))
        {
            Console.WriteLine($"\tDeleting topic.");
            await snsClient.DeleteTopicAsync(new DeleteTopicRequest()
            {
                TopicArn = topicArn
            });
        }

        var bucketName = _configuration["testBucketName"];
        if (GetYesNoResponse($"\tDelete Amazon S3 bucket {bucketName}? (y/n)"))
        {
            Console.WriteLine($"\tDeleting bucket.");
            // Delete all objects in the bucket.
            var deleteList = await s3Client.ListObjectsV2Async(new ListObjectsV2Request());
            await s3Client.DeleteObjectsAsync(new DeleteObjectsRequest()
            {
                Objects = deleteList.S3Objects
                    .Select(o => new KeyVersion{Key = o.Key}).ToList()
            });
            // Now delete the bucket.
            await s3Client.DeleteBucketAsync(new DeleteBucketRequest()
            {
                BucketName = bucketName
            });
        }

        Console.WriteLine(new string('-', 80));
    }

    /// <summary>
    /// Helper method to get a yes or no response from the user.
    /// </summary>
    /// <param name="question">The question string to print on the console.</param>
    /// <returns>True if the user responds with a yes.</returns>
    private static bool GetYesNoResponse(string question)
    {
        Console.WriteLine(question);
        var ynResponse = Console.ReadLine();
        var response = ynResponse != null &&
                       ynResponse.Equals("y",
                           StringComparison.InvariantCultureIgnoreCase);
        return response;
    }
}
