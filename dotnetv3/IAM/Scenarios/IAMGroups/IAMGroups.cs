// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Runtime;

namespace IAMGroups;

public class IAMGroups
{
    private static ILogger logger = null!;

    // Represents json code for AWS full access policy for Amazon Simple
    // Storage Service (Amazon S3).
    private const string S3FullAccessPolicy = "{" +
        "	\"Statement\" : [{" +
            "	\"Action\" : [\"s3:*\"]," +
            "	\"Effect\" : \"Allow\"," +
            "	\"Resource\" : \"*\"" +
        "}]" +
    "}";

    private const string UserName = "S3FullAccessUser";
    private const string GroupName = "S3FullAccessGroup";
    private const string PolicyName = "S3FullAccess";
    private const string BucketName = "temporary-doc-example-bucket";

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
            .AddTransient<S3Wrapper>()
            .AddTransient<UIWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<IAMGroups>();

        var wrapper = host.Services.GetRequiredService<IAMWrapper>();
        var uiWrapper = host.Services.GetRequiredService<UIWrapper>();

        uiWrapper.DisplayGroupsOverview();
        uiWrapper.PressEnter();

        // Create an IAM group.
        uiWrapper.DisplayTitle("Create IAM group");
        Console.WriteLine("Let's begin by creating a new IAM group.");
        var group = await wrapper.CreateGroupAsync(GroupName);

        // Create a policy and add it to the group.
        await wrapper.PutGroupPolicyAsync(group.GroupName, PolicyName, S3FullAccessPolicy);

        // Now create a new user.
        var readOnlyUser = await wrapper.CreateUserAsync(UserName);

        // Create access and secret keys for the user.
        var accessKey = await wrapper.CreateAccessKeyAsync(UserName);

        // Add the new user to the group.
        await wrapper.AddUserToGroupAsync(readOnlyUser.UserName, group.GroupName);

        // Show that the user can access Amazon S3 by listing the buckets on
        // the account.
        Console.Write("Waiting for user status to be Active.");
        do
        {
            Console.Write(" .");
        }
        while (accessKey.Status != StatusType.Active);

        var s3Client1 = new AmazonS3Client(accessKey.AccessKeyId, accessKey.SecretAccessKey);
        var stsClient1 = new AmazonSecurityTokenServiceClient(accessKey.AccessKeyId, accessKey.SecretAccessKey);

        var s3Wrapper = new S3Wrapper(s3Client1, stsClient1);

        var buckets = await s3Wrapper.ListMyBucketsAsync();

        if (buckets is not null)
        {
            buckets.ForEach(bucket =>
            {
                Console.WriteLine($"{bucket.BucketName}\tcreated on: {bucket.CreationDate}");
            });
        }

        // Show that the user also has write access to Amazon S3 by creating
        // a new bucket.
        var success = await s3Wrapper.PutBucketAsync(BucketName);

        if (success)
        {
            Console.WriteLine($"Successfully created the bucket: {BucketName}.");
        }

        uiWrapper.DisplayTitle("Clean up resources");


    }
}
