// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

// snippet-start:[S3.dotnetv4.CreatePresignedPostWithFilename]
/// <summary>
/// Demonstrates how to create Amazon S3 presigned POST URLs with the ${filename} variable.
/// This example shows how to preserve the original filename when uploading files to S3.
/// </summary>
public class CreatePresignedPostWithFilename
{
    /// <summary>
    /// Create a presigned POST URL with the ${filename} variable to preserve original filenames.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="keyPrefix">The prefix for the key, final key will be prefix + actual filename.</param>
    /// <returns>A CreatePresignedPostResponse containing the URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreateWithFilename(
        S3Wrapper s3Wrapper,
        ILogger logger,
        string bucketName,
        string keyPrefix)
    {
        var expiration = DateTime.UtcNow.AddHours(1);
        
        // Using "${filename}" placeholder in the key lets the browser replace it with the actual filename
        string objectKey = keyPrefix + "${filename}";
        
        logger.LogInformation("Creating presigned POST URL with filename variable for {bucket}/{key}", 
            bucketName, objectKey);
            
        var response = await s3Wrapper.CreatePresignedPostAsync(bucketName, objectKey, expiration);
        
        logger.LogInformation("Successfully created presigned POST URL with filename variable");
        logger.LogInformation("When a file is uploaded, it will be stored at: {prefix} + actual filename", 
            keyPrefix);
            
        return response;
    }


    /// <summary>
    /// Main method that demonstrates creating and using presigned POST URLs with the ${filename} variable.
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
        var logger = loggerFactory.CreateLogger<CreatePresignedPostWithFilename>();
        
        // Create the wrapper instance
        var s3Wrapper = new S3Wrapper(s3Client, loggerFactory.CreateLogger<S3Wrapper>());
        
        Console.WriteLine("Amazon S3 CreatePresignedPost with ${filename} Variable Example");
        Console.WriteLine("========================================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create a key prefix for this example
            string keyPrefix = "uploads/";

            // Generate the presigned POST URL with filename variable
            Console.WriteLine("\nCreating a presigned POST URL that preserves the original filename...");
            var response = await CreateWithFilename(s3Wrapper, logger, bucketName, keyPrefix);
            
            // Display the URL and fields
            Console.WriteLine("\nPresigned POST URL with filename variable created successfully:");
            PresignedPostUtils.DisplayPresignedPostFields(response);

            Console.WriteLine("\nThis example uses ${filename} in the key:");
            Console.WriteLine("  • The uploaded file will be stored with its original name in the 'uploads/' prefix");
            Console.WriteLine("  • For example, if you upload 'document.pdf', it will be stored as 'uploads/document.pdf'");
            
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
// snippet-end:[S3.dotnetv4.CreatePresignedPostWithFilename]
