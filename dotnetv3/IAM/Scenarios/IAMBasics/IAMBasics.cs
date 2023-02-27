// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMBasics;

public class IAMBasics
{
    private static ILogger logger = null!;

    // Values needed for user, role, and policies.
    private const string UserName = "example-user";
    private const string S3PolicyName = "s3-list-buckets-policy";
    private const string RoleName = "temporary-role";
    private const string AssumePolicyName = "sts-trust-user";

    private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;


    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
            services.AddAWSService<IAmazonIdentityManagementService>()
            .AddTransient<IAMWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<IAMBasics>();

        var wrapper = host.Services.GetRequiredService<IAMWrapper>();

        // First create a user. By default, the new user has
        // no permissions.
        Console.WriteLine($"Creating a new user with user name: {UserName}.");
        var user = await wrapper.CreateUserAsync(UserName);
        var userArn = user.Arn;
        Console.WriteLine($"Successfully created user: {UserName} with ARN: {userArn}.");

        // Create an AccessKey for the user.
        var accessKey = await wrapper.CreateAccessKeyAsync(UserName);

        var accessKeyId = accessKey.AccessKeyId;
        var secretAccessKey = accessKey.SecretAccessKey;

        // Try listing the Amazon Simple Storage Service (Amazon S3)
        // buckets. This should fail at this point because the user doesn't
        // have permissions to perform this task.
        var s3Client1 = new AmazonS3Client(accessKeyId, secretAccessKey);
        await ListMyBucketsAsync(s3Client1);


    }
}
