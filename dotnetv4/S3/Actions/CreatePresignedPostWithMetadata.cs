// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

/// <summary>
/// Demonstrates how to create an Amazon S3 presigned POST URL with metadata.
/// This example shows how to add custom metadata to objects uploaded via presigned POST URLs.
/// </summary>
public class CreatePresignedPostWithMetadata
{
    /// <summary>
    /// Create a presigned POST URL with metadata.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="objectKey">The object key where the uploaded file will be stored.</param>
    /// <returns>A CreatePresignedPostResponse containing the URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreateWithMetadata(
        S3Wrapper s3Wrapper,
        ILogger logger,
        string bucketName,
        string objectKey)
    {
        var expiration = DateTime.UtcNow.AddHours(1);
        
        var fields = new Dictionary<string, string>
        {
            // Add a custom metadata field
            { "x-amz-meta-uploaded-by", "dotnet-sdk-example" },
            
            // Return HTTP 201 on successful upload
            { "success_action_status", "201" }
        };
        
        logger.LogInformation("Creating presigned POST URL with metadata for {bucket}/{key}", 
            bucketName, objectKey);
            
        var response = await s3Wrapper.CreatePresignedPostWithFieldsAsync(
            bucketName, objectKey, expiration, fields);
        
        logger.LogInformation("Successfully created presigned POST URL with {count} custom fields", 
            fields.Count);
            
        return response;
    }


    /// <summary>
    /// Main method that demonstrates creating and using presigned POST URLs with metadata.
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
        var logger = loggerFactory.CreateLogger<CreatePresignedPostWithMetadata>();
        
        // Create the wrapper instance
        var s3Wrapper = new S3Wrapper(s3Client, loggerFactory.CreateLogger<S3Wrapper>());
        
        Console.WriteLine("Amazon S3 CreatePresignedPost with Metadata Example");
        Console.WriteLine("==================================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create an object key for this example
            string objectKey = "metadata-example.txt";

            // Generate the presigned POST URL with metadata
            Console.WriteLine("\nCreating a presigned POST URL with metadata fields...");
            var response = await CreateWithMetadata(s3Wrapper, logger, bucketName, objectKey);
            
            // Display the URL and fields
            Console.WriteLine("\nPresigned POST URL with metadata created successfully:");
            PresignedPostUtils.DisplayPresignedPostFields(response);

            Console.WriteLine("\nThis example adds:");
            Console.WriteLine("  • x-amz-meta-uploaded-by: dotnet-sdk-example - adds custom metadata");
            Console.WriteLine("  • success_action_status: 201 - returns HTTP 201 on successful upload");

            
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
