// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAMBasics;

public class IAMBasics
{
    private static ILogger logger = null!;

    // Values needed for user, role, and policies.
    private const string UserName = "iam-mvp-user";
    private const string S3PolicyName = "s3-list-buckets-policy";
    private const string RoleName = "mvp-temporary-role";
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
            .AddTransient<S3Wrapper>()
            .AddTransient<UIWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<IAMBasics>();

        var wrapper = host.Services.GetRequiredService<IAMWrapper>();
        var uiWrapper = host.Services.GetRequiredService<UIWrapper>();

        uiWrapper.DisplayOverview();
        uiWrapper.PressEnter();

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
        var stsClient1 = new AmazonSecurityTokenServiceClient(accessKeyId, secretAccessKey);

        var s3Wrapper = new S3Wrapper(s3Client1, stsClient1);
        var buckets = await s3Wrapper.ListMyBucketsAsync();

        if (buckets is null)
        {
            Console.WriteLine("As expected, the call to list the buckets has returned a null list.");
        }
        else
        {
            Console.WriteLine("Something went wrong. This shouldn't have worked.");
        }

        // Define a role policy document that allows the new user
        // to assume the role.
        // string assumeRolePolicyDocument = File.ReadAllText("assumePolicy.json");
        string assumeRolePolicyDocument = "{" +
          "\"Version\": \"2012-10-17\"," +
          "\"Statement\": [{" +
              "\"Effect\": \"Allow\"," +
              "\"Principal\": {" +
              $"	\"AWS\": \"{userArn}\"" +
              "}," +
              "\"Action\": \"sts:AssumeRole\"" +
          "}]" +
        "}";

        // Permissions to list all buckets.
        string policyDocument = "{" +
            "\"Version\": \"2012-10-17\"," +
            "	\"Statement\" : [{" +
                "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                "	\"Effect\" : \"Allow\"," +
                "	\"Resource\" : \"*\"" +
            "}]" +
        "}";

        // Create the role to allow listing the S3 buckets. Role names are
        // not case sensitive and must be unique to the account for which it
        // is created.
        var roleArn = await wrapper.CreateRoleAsync(RoleName, assumeRolePolicyDocument);

        // Create a policy with permissions to list S3 buckets
        var policy = await wrapper.CreatePolicyAsync(S3PolicyName, policyDocument);
        
        // Attach the policy to the role you created earlier.
        await wrapper.AttachRolePolicyAsync(policy.Arn, RoleName);

    }
}
