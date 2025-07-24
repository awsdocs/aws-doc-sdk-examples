// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

/// <summary>
/// Demonstrates how to create Amazon S3 presigned POST URLs with conditions.
/// This example shows how to add restrictions to uploads such as content type and size limits.
/// </summary>
public class CreatePresignedPostWithConditions
{
    /// <summary>
    /// Create a presigned POST URL with conditions to restrict uploads.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="objectKey">The object key where the uploaded file will be stored.</param>
    /// <returns>A CreatePresignedPostResponse containing the URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreateWithConditions(
        S3Wrapper s3Wrapper,
        ILogger logger,
        string bucketName,
        string objectKey)
    {
        var expiration = DateTime.UtcNow.AddHours(1);
        
        var fields = new Dictionary<string, string>
        {
            { "Content-Type", "text/plain" }
        };
        
        var conditions = new List<S3PostCondition>
        {   
            // File size must be between 1 byte and 1 MB
            S3PostCondition.ContentLengthRange(1, 1048576)
        };
        
        logger.LogInformation("Creating presigned POST URL with conditions for {bucket}/{key}", 
            bucketName, objectKey);
            
        var response = await s3Wrapper.CreatePresignedPostWithConditionsAsync(
            bucketName, objectKey, expiration, fields, conditions);
        
        logger.LogInformation("Successfully created presigned POST URL with {fieldCount} fields and {conditionCount} conditions", 
            fields.Count, conditions.Count);
            
        return response;
    }


    /// <summary>
    /// Main method that demonstrates creating and using presigned POST URLs with conditions.
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
        var logger = loggerFactory.CreateLogger<CreatePresignedPostWithConditions>();
        
        // Create the wrapper instance
        var s3Wrapper = new S3Wrapper(s3Client, loggerFactory.CreateLogger<S3Wrapper>());
        
        Console.WriteLine("Amazon S3 CreatePresignedPost with Conditions Example");
        Console.WriteLine("===================================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create an object key for this example
            string objectKey = "conditions-example.txt";

            // Generate the presigned POST URL with conditions
            Console.WriteLine("\nCreating a presigned POST URL with upload restrictions...");
            var response = await CreateWithConditions(s3Wrapper, logger, bucketName, objectKey);
            
            // Display the URL and fields
            Console.WriteLine("\nPresigned POST URL with conditions created successfully:");
            PresignedPostUtils.DisplayPresignedPostFields(response);

            Console.WriteLine("\nThis example adds these restrictions:");
            Console.WriteLine("  • Content-Type must start with 'text/plain' (enforced by policy)");
            Console.WriteLine("  • File size must be between 1 byte and 1 MB (enforced by policy)");
            Console.WriteLine("\nIf these conditions are not met, the upload will be rejected.");
            
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
