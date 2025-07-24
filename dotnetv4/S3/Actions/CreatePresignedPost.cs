// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

// snippet-start:[S3.dotnetv4.CreatePresignedPost]
/// <summary>
/// Demonstrates how to use Amazon Simple Storage Service (Amazon S3)
/// CreatePresignedPost functionality to generate a pre-signed URL for browser-based uploads.
/// </summary>
public class CreatePresignedPost
{
    /// <summary>
    /// Create a basic presigned POST URL.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket where the file will be uploaded.</param>
    /// <param name="objectKey">The object key (path) where the file will be stored.</param>
    /// <returns>A CreatePresignedPostResponse containing the URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreateBasicPresignedPost(
        S3Wrapper s3Wrapper, 
        ILogger logger, 
        string bucketName, 
        string objectKey)
    {
        // Set expiration time (maximum is 7 days from now)
        var expiration = DateTime.UtcNow.AddHours(1); // 1 hour expiration

        logger.LogInformation("Creating presigned POST URL for {bucket}/{key} with expiration {expiration}", 
            bucketName, objectKey, expiration);

        var response = await s3Wrapper.CreatePresignedPostAsync(bucketName, objectKey, expiration);
        
        logger.LogInformation("Successfully created presigned POST URL: {url}", response.Url);
        
        return response;
    }


    /// <summary>
    /// Main method that demonstrates the CreatePresignedPost functionality.
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
        
        Console.WriteLine("Amazon S3 CreatePresignedPost Basic Example");
        Console.WriteLine("==========================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create a simple example object key
            string objectKey = "example-upload.txt";

            // Generate the presigned POST URL
            Console.WriteLine("\nCreating a presigned POST URL...");
            var response = await CreateBasicPresignedPost(s3Wrapper, logger, bucketName, objectKey);
            
            // Display the URL and fields that would be needed in an HTML form
            PresignedPostUtils.DisplayPresignedPostFields(response);
            
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
