// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System.Net;
using System.Text.RegularExpressions;
using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace ServerSideEncryption;

// snippet-start:[S3.dotnetv3.ServerSideEncryption]
/// <summary>
/// Set and read the server side encryption settings for a bucket.
/// </summary>
public static class ServerSideEncryption
{
    public static IAmazonS3 _s3Client = null!;
    public static string _bucketName = null!;
    public static string _kmsKeyId = null!;
    public static bool _interactive = true;

    static async Task Main(string[] args)
    {
        // Use the AWS .NET Core Setup package to set up dependency injection for the Amazon S3 service.
        // Use your AWS profile name, or leave it blank to use the default profile.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonS3>()
            ).Build();

        // Now the client is available for injection.
        _s3Client = host.Services.GetRequiredService<IAmazonS3>();

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("This example demonstrates setting and getting the server side encryption for a bucket.");
        Console.WriteLine(new string('-', 80));

        var retries = 5;
        var success = false;
        while (!success && retries > 0)
        {
            if (_interactive)
            {
                _bucketName = PromptUserForResource("Enter the name of a bucket to set encryption:");
                _kmsKeyId = PromptUserForResource("Enter the Id of the KMS Key to use for encryption:");
            }
            success = await SetBucketServerSideEncryption(_bucketName, _kmsKeyId);
            retries--;
        }

        if (success)
        {
            await GetEncryptionSettings(_bucketName);
        }

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Example complete.");
        Console.WriteLine(new string('-', 80));

    }

    // snippet-start:[S3.dotnetv3.PutBucketServerSideEncryption]
    /// <summary>
    /// Set the bucket server side encryption to use AWSKMS with a customer-managed key id.
    /// </summary>
    /// <param name="bucketName">Name of the bucket.</param>
    /// <param name="kmsKeyId">The Id of the KMS Key.</param>
    /// <returns>True if successful.</returns>
    public static async Task<bool> SetBucketServerSideEncryption(string bucketName, string kmsKeyId)
    {
        var serverSideEncryptionByDefault = new ServerSideEncryptionConfiguration
        {
            ServerSideEncryptionRules = new List<ServerSideEncryptionRule>
            {
                new ServerSideEncryptionRule
                {
                    ServerSideEncryptionByDefault = new ServerSideEncryptionByDefault
                    {
                        ServerSideEncryptionAlgorithm = ServerSideEncryptionMethod.AWSKMS,
                        ServerSideEncryptionKeyManagementServiceKeyId = kmsKeyId
                    }
                }
            }
        };
        try
        {
            var encryptionResponse = await _s3Client.PutBucketEncryptionAsync(new PutBucketEncryptionRequest
            {
                BucketName = bucketName,
                ServerSideEncryptionConfiguration = serverSideEncryptionByDefault,
            });
            
            return encryptionResponse.HttpStatusCode == HttpStatusCode.OK;
        }
        catch (AmazonS3Exception ex)
        {
            Console.WriteLine(ex.ErrorCode == "AccessDenied"
                ? $"This account does not have permission to set encryption on {bucketName}, please try again."
                : $"Unable to set bucket encryption for bucket {bucketName}, {ex.Message}");
        }
        return false;
    }
    // snippet-end:[S3.dotnetv3.PutBucketServerSideEncryption]

    // snippet-start:[S3.dotnetv3.GetBucketServerSideEncryption]
    /// <summary>
    /// Get and print the encryption settings of a bucket.
    /// </summary>
    /// <param name="bucketName">Name of the bucket.</param>
    /// <returns>Async task.</returns>
    public static async Task GetEncryptionSettings(string bucketName)
    {
        // Check and print the bucket encryption settings.
        Console.WriteLine($"Getting encryption settings for bucket {bucketName}.");

        try
        {
            var settings =
                await _s3Client.GetBucketEncryptionAsync(
                    new GetBucketEncryptionRequest() { BucketName = bucketName });

            foreach (var encryptionSettings in settings?.ServerSideEncryptionConfiguration?.ServerSideEncryptionRules!)
            {
                Console.WriteLine(
                    $"\tAlgorithm: {encryptionSettings.ServerSideEncryptionByDefault.ServerSideEncryptionAlgorithm}");
                Console.WriteLine(
                    $"\tKey: {encryptionSettings.ServerSideEncryptionByDefault.ServerSideEncryptionKeyManagementServiceKeyId}");
            }
        }
        catch (AmazonS3Exception ex)
        {
            Console.WriteLine(ex.ErrorCode == "InvalidBucketName"
                ? $"Bucket {bucketName} was not found."
                : $"Unable to get bucket encryption for bucket {bucketName}, {ex.Message}");
        }
    }
    // snippet-end:[S3.dotnetv3.GetBucketServerSideEncryption]

    /// <summary>
    /// Prompt the user for a non-empty resource.
    /// </summary>
    /// <returns>The valid resource.</returns>
    private static string PromptUserForResource(string prompt)
    {
        Console.WriteLine(prompt);
        string resourceName = Console.ReadLine()!;
        var regex = "[0-9a-zA-Z-_.]+";
        if (!Regex.IsMatch(resourceName, regex))
        {
            Console.WriteLine($"Invalid resource. Please use a name that matches the pattern {regex}.");
            return PromptUserForResource(prompt);
        }
        return resourceName!;
    }
}
// snippet-end:[S3.dotnetv3.ServerSideEncryption]