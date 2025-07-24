// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Scenarios;

// snippet-start:[S3.dotnetv4.CreatePresignedPostScenario]
/// <summary>
/// Scenario demonstrating the complete workflow for presigned POST URLs:
/// 1. Create an S3 bucket
/// 2. Create a presigned POST URL
/// 3. Upload a file using the presigned POST URL
/// 4. Clean up resources
/// </summary>
public class CreatePresignedPostScenario
{
    private readonly S3Wrapper _s3Wrapper;
    private readonly ILogger _logger;
    private readonly UiMethods _uiMethods;
    private readonly bool _isInteractive;
    private string? _bucketName;
    private string? _objectKey;

    /// <summary>
    /// Constructor for the scenario class.
    /// </summary>
    /// <param name="s3Wrapper">The S3Wrapper instance to use.</param>
    /// <param name="logger">The logger to use.</param>
    /// <param name="uiMethods">The UI methods to use.</param>
    /// <param name="isInteractive">Whether to run in interactive mode.</param>
    public CreatePresignedPostScenario(S3Wrapper s3Wrapper, ILogger logger, UiMethods uiMethods, bool isInteractive)
    {
        _s3Wrapper = s3Wrapper;
        _logger = logger;
        _uiMethods = uiMethods;
        _isInteractive = isInteractive;
    }

    /// <summary>
    /// Run the scenario steps.
    /// </summary>
    /// <returns>A Task representing the asynchronous operation.</returns>
    public async Task RunAsync()
    {
        try
        {
            // Display overview
            _uiMethods.DisplayOverview();
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 1: Create bucket
            await CreateBucketAsync();
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 2: Create presigned URL
            _uiMethods.DisplayTitle("Step 2: Create presigned POST URL");
            var response = await CreatePresignedPostAsync();
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 3: Display URL and fields
            _uiMethods.DisplayTitle("Step 3: Presigned POST URL details");
            PresignedPostUtils.DisplayPresignedPostFields(response);
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 4: Upload file
            _uiMethods.DisplayTitle("Step 4: Upload test file using presigned POST URL");
            await UploadFileAsync(response);
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 5: Verify file exists
            await VerifyFileExistsAsync();
            _uiMethods.PressEnter(_isInteractive);
            
            // Step 6: Cleanup
            _uiMethods.DisplayTitle("Step 6: Clean up resources");
            await CleanupAsync();
            
            _uiMethods.DisplayTitle("S3 Presigned POST Scenario completed successfully!");
            _uiMethods.PressEnter(_isInteractive);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error in scenario");
            Console.WriteLine($"Error: {ex.Message}");
            
            // Attempt cleanup if there was an error
            if (!string.IsNullOrEmpty(_bucketName))
            {
                _uiMethods.DisplayTitle("Cleaning up resources after error");
                await _s3Wrapper.DeleteBucketAsync(_bucketName);
                Console.WriteLine($"Cleaned up bucket: {_bucketName}");
            }
        }
    }

    /// <summary>
    /// Create an S3 bucket for the scenario.
    /// </summary>
    private async Task CreateBucketAsync()
    {
        _uiMethods.DisplayTitle("Step 1: Create an S3 bucket");
        
        // Create a unique bucket name for the scenario
        _bucketName = $"presigned-post-demo-{DateTime.Now:yyyyMMddHHmmss}".ToLower();
        
        Console.WriteLine($"Creating bucket: {_bucketName}");
        
        await _s3Wrapper.CreateBucketAsync(_bucketName);
        
        Console.WriteLine($"Successfully created bucket: {_bucketName}");
    }

    /// <summary>
    /// Create a presigned POST URL.
    /// </summary>
    private async Task<CreatePresignedPostResponse> CreatePresignedPostAsync()
    {
        _objectKey = "example-upload.txt";
        var expiration = DateTime.UtcNow.AddMinutes(10); // Short expiration for the demo
        
        Console.WriteLine($"Creating presigned POST URL for {_bucketName}/{_objectKey}");
        Console.WriteLine($"Expiration: {expiration} UTC");
        
        var response = await _s3Wrapper.CreatePresignedPostAsync(_bucketName!, _objectKey, expiration);
        
        Console.WriteLine("Successfully created presigned POST URL");
        return response;
    }

    /// <summary>
    /// Upload a file using the presigned POST URL.
    /// </summary>
    private async Task UploadFileAsync(CreatePresignedPostResponse response)
    {
        
        // Create a temporary test file to upload
        string testFilePath = Path.GetTempFileName();
        string testContent = "This is a test file for the S3 presigned POST scenario.";
        
        await File.WriteAllTextAsync(testFilePath, testContent);
        Console.WriteLine($"Created test file at: {testFilePath}");
        
        // Upload the file using the presigned POST URL
        Console.WriteLine("\nUploading file using the presigned POST URL...");
        var uploadResult = await UploadFileWithPresignedPostAsync(response, testFilePath);
        
        // Display the upload result
        if (uploadResult.Success)
        {
            Console.WriteLine($"Upload successful! Status code: {uploadResult.StatusCode}");
            Console.WriteLine($"Response: {uploadResult.Response}");
        }
        else
        {
            Console.WriteLine($"Upload failed with status code: {uploadResult.StatusCode}");
            Console.WriteLine($"Error: {uploadResult.Response}");
            throw new Exception("File upload failed");
        }
        
        // Clean up the temporary file
        File.Delete(testFilePath);
        Console.WriteLine("Temporary file deleted");
    }

    /// <summary>
    /// Helper method to upload a file using a presigned POST URL.
    /// </summary>
    private async Task<(bool Success, HttpStatusCode StatusCode, string Response)> UploadFileWithPresignedPostAsync(
        CreatePresignedPostResponse response, 
        string filePath)
    {
        try
        {
            _logger.LogInformation("Uploading file {filePath} using presigned POST URL", filePath);
            
            // Create HttpClient to send the request
            using var httpClient = new HttpClient();
            using var formContent = new MultipartFormDataContent();
            
            // Add all the fields from the presigned POST response
            foreach (var field in response.Fields)
            {
                formContent.Add(new StringContent(field.Value), field.Key);
            }
            
            // Add the file content
            var fileStream = File.OpenRead(filePath);
            var fileName = Path.GetFileName(filePath);
            var fileContent = new StreamContent(fileStream);
            fileContent.Headers.ContentType = new MediaTypeHeaderValue("text/plain");
            formContent.Add(fileContent, "file", fileName);
            
            // Send the POST request
            var httpResponse = await httpClient.PostAsync(response.Url, formContent);
            var responseContent = await httpResponse.Content.ReadAsStringAsync();
            
            // Log and return the result
            _logger.LogInformation("Upload completed with status code {statusCode}", httpResponse.StatusCode);
            
            return (httpResponse.IsSuccessStatusCode, httpResponse.StatusCode, responseContent);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error uploading file");
            return (false, HttpStatusCode.InternalServerError, ex.Message);
        }
    }

    /// <summary>
    /// Verify that the uploaded file exists in the S3 bucket.
    /// </summary>
    private async Task VerifyFileExistsAsync()
    {
        _uiMethods.DisplayTitle("Step 5: Verify uploaded file exists");
        
        Console.WriteLine($"Checking if file exists at {_bucketName}/{_objectKey}...");
        
        try
        {
            var metadata = await _s3Wrapper.GetObjectMetadataAsync(_bucketName!, _objectKey!);
            
            Console.WriteLine($"File verification successful! File exists in the bucket.");
            Console.WriteLine($"File size: {metadata.ContentLength} bytes");
            Console.WriteLine($"File type: {metadata.Headers.ContentType}");
            Console.WriteLine($"Last modified: {metadata.LastModified}");
        }
        catch (AmazonS3Exception ex) when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            Console.WriteLine($"Error: File was not found in the bucket.");
            throw;
        }
    }

    /// <summary>
    /// Clean up resources created by the scenario.
    /// </summary>
    private async Task CleanupAsync()
    {
        
        if (!string.IsNullOrEmpty(_bucketName))
        {
            Console.WriteLine($"Deleting bucket {_bucketName} and its contents...");
            bool result = await _s3Wrapper.DeleteBucketAsync(_bucketName);
            
            if (result)
            {
                Console.WriteLine("Bucket deleted successfully");
            }
            else
            {
                Console.WriteLine("Failed to delete bucket - it may have been already deleted");
            }
        }
    }
}
// snippet-end:[S3.dotnetv4.CreatePresignedPostScenario]
