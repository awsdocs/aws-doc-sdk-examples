// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

// snippet-start:[S3.dotnetv4.CreatePresignedPost]
/// <summary>
/// Demonstrates how to create Amazon S3 presigned POST URLs with both conditions and filename variables.
/// This example shows how to add restrictions to uploads and preserve original filenames.
/// </summary>
public class CreatePresignedPost
{
    /// <summary>
    /// Create a presigned POST URL with both filename variable and conditions.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="keyPrefix">The prefix for the key, final key will be prefix + actual filename.</param>
    /// <returns>A CreatePresignedPostResponse containing the URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreateWithFilenameAndConditions(
        S3Wrapper s3Wrapper,
        ILogger logger,
        string bucketName,
        string keyPrefix)
    {
        var expiration = DateTime.UtcNow.AddHours(1);
        
        // Using "${filename}" placeholder in the key lets the browser replace it with the actual filename
        string objectKey = keyPrefix + "${filename}";
        
        // Add custom metadata and fields
        var fields = new Dictionary<string, string>
        {
            // Add a custom metadata field
            { "x-amz-meta-uploaded-by", "dotnet-sdk-example" },
            
            // Return HTTP 201 on successful upload
            { "success_action_status", "201" },
            
            // Set the content type
            { "Content-Type", "text/plain" }
        };
        
        // Add policy conditions
        var conditions = new List<S3PostCondition>
        {   
            // File size must be between 1 byte and 1 MB
            S3PostCondition.ContentLengthRange(1, 1048576)
        };
        
        logger.LogInformation("Creating presigned POST URL with filename variable and conditions for {bucket}/{key}", 
            bucketName, objectKey);
            
        var response = await s3Wrapper.CreatePresignedPostWithConditionsAsync(
            bucketName, objectKey, expiration, fields, conditions);
        
        logger.LogInformation("Successfully created presigned POST URL with filename variable and conditions");
            
        return response;
    }

    /// <summary>
    /// Main method that demonstrates creating and using presigned POST URLs with combined features.
    /// </summary>
    /// <param name="args">Command line arguments. Not used in this example.</param>
    /// <returns>Async task.</returns>
    public static async Task Main(string[] args)
    {
        // Set up dependency injection for Amazon S3
        using var host = Microsoft.Extensions.Hosting.Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonS3>()
                    .AddTransient<S3Wrapper>()
            )
            .Build();

        // Get the services
        var s3Client = host.Services.GetRequiredService<IAmazonS3>();
        var loggerFactory = LoggerFactory.Create(builder => builder.AddConsole());
        var logger = loggerFactory.CreateLogger<CreatePresignedPost>();
        
        // Create the wrapper instance
        var s3Wrapper = new S3Wrapper(s3Client, loggerFactory.CreateLogger<S3Wrapper>());
        
        Console.WriteLine("Amazon S3 CreatePresignedPost Example");
        Console.WriteLine("===================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create a key prefix for this example
            string keyPrefix = "uploads/";

            // Generate the presigned POST URL with combined features
            Console.WriteLine("\nCreating a presigned POST URL with both filename preservation and upload restrictions...");
            var response = await CreateWithFilenameAndConditions(s3Wrapper, logger, bucketName, keyPrefix);
            
            // Display the URL and fields
            Console.WriteLine("\nPresigned POST URL with combined features created successfully:");
            PresignedPostUtils.DisplayPresignedPostFields(response);

            Console.WriteLine("\nThis example combines multiple features:");
            Console.WriteLine("  • Uses ${filename} to preserve the original filename in the 'uploads/' prefix");
            Console.WriteLine("  • Adds custom metadata (x-amz-meta-uploaded-by)");
            Console.WriteLine("  • Sets success_action_status to return HTTP 201 on success");
            Console.WriteLine("  • Restricts content type to text/plain");
            Console.WriteLine("  • Limits file size to between 1 byte and 1 MB");
            
            Console.WriteLine("\nExample completed successfully.");
        }
        catch (AmazonS3Exception ex)
        {
            Console.WriteLine($"Amazon S3 error: {ex.Message}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error: {ex.Message}");
        }
    }
}
// snippet-end:[S3.dotnetv4.CreatePresignedPost]
