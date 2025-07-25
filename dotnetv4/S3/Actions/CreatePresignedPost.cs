// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

// snippet-start:[S3.dotnetv4.CreatePresignedPost]
/// <summary>
/// Class for creating Amazon S3 presigned POST URLs with various conditions.
/// </summary>
public class CreatePresignedPost
{
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
            )
            .Build();

        // Get the services
        var s3Client = host.Services.GetRequiredService<IAmazonS3>();
        var loggerFactory = LoggerFactory.Create(builder => builder.AddConsole());
        var logger = loggerFactory.CreateLogger<CreatePresignedPost>();

        Console.WriteLine("Amazon S3 CreatePresignedPost Example");
        Console.WriteLine("===================================");

        try
        {
            const string bucketName = "amzn-s3-demo-bucket";
            Console.WriteLine($"Using bucket: {bucketName}");
            Console.WriteLine("Note: You must have an existing bucket with this name or create one first.");

            // Create a key prefix for this example
            string keyPrefix = "uploads/";
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

            // Generate the presigned POST URL with combined features
            Console.WriteLine("\nCreating a presigned POST URL with both filename preservation and upload restrictions...");
            logger.LogInformation("Creating presigned POST URL with filename variable and conditions for {bucket}/{key}",
                bucketName, objectKey);

            var response = await CreatePresignedPostAsync(
                s3Client, logger, bucketName, objectKey, expiration, fields, conditions);

            logger.LogInformation("Successfully created presigned POST URL with filename variable and conditions");

            // Display the URL and fields
            Console.WriteLine("\nPresigned POST URL with combined features created successfully:");
            DisplayPresignedPostFields(response);

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
    
    /// <summary>
    /// Create a presigned POST URL with conditions.
    /// </summary>
    /// <param name="s3Client">The Amazon S3 client.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="objectKey">The object key (path) where the uploaded file will be stored.</param>
    /// <param name="expires">When the presigned URL expires.</param>
    /// <param name="fields">Dictionary of fields to add to the form.</param>
    /// <param name="conditions">List of conditions to apply.</param>
    /// <returns>A CreatePresignedPostResponse object with URL and form fields.</returns>
    public static async Task<CreatePresignedPostResponse> CreatePresignedPostAsync(
        IAmazonS3 s3Client,
        ILogger logger,
        string bucketName, 
        string objectKey, 
        DateTime expires, 
        Dictionary<string, string>? fields = null, 
        List<S3PostCondition>? conditions = null)
    {
        var request = new CreatePresignedPostRequest
        {
            BucketName = bucketName,
            Key = objectKey,
            Expires = expires
        };

        // Add custom fields if provided
        if (fields != null)
        {
            foreach (var field in fields)
            {
                request.Fields.Add(field.Key, field.Value);
            }
        }

        // Add conditions if provided
        if (conditions != null)
        {
            foreach (var condition in conditions)
            {
                request.Conditions.Add(condition);
            }
        }

        return await s3Client.CreatePresignedPostAsync(request);
    }

    /// <summary>
    /// Display the fields from a presigned POST response.
    /// </summary>
    /// <param name="response">The CreatePresignedPostResponse to display.</param>
    public static void DisplayPresignedPostFields(CreatePresignedPostResponse response)
    {
        Console.WriteLine($"Presigned POST URL: {response.Url}");
        Console.WriteLine("Form fields to include:");
        
        foreach (var field in response.Fields)
        {
            Console.WriteLine($"  {field.Key}: {field.Value}");
        }
    }
}
// snippet-end:[S3.dotnetv4.CreatePresignedPost]
