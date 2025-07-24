// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Actions;

/// <summary>
/// Wrapper methods for common Amazon Simple Storage Service (Amazon S3) 
/// operations.
/// </summary>
public class S3Wrapper
{
    private readonly IAmazonS3 _s3Client;
    private readonly ILogger<S3Wrapper> _logger;

    /// <summary>
    /// Constructor for the wrapper class.
    /// </summary>
    /// <param name="s3Client">The injected S3 client.</param>
    /// <param name="logger">The injected logger for use with this class.</param>
    public S3Wrapper(IAmazonS3 s3Client, ILogger<S3Wrapper> logger)
    {
        _s3Client = s3Client;
        _logger = logger;
    }

    /// <summary>
    /// Get the Amazon S3 client.
    /// </summary>
    /// <returns>The Amazon S3 client.</returns>
    public IAmazonS3 GetS3Client()
    {
        return _s3Client;
    }

    /// <summary>
    /// Create a bucket and wait until it's ready to use.
    /// </summary>
    /// <param name="bucketName">The name of the bucket to create.</param>
    /// <returns>The name of the newly created bucket.</returns>
    public async Task<string> CreateBucketAsync(string bucketName)
    {
        _logger.LogInformation("Creating bucket {bucket}", bucketName);
        
        var request = new PutBucketRequest
        {
            BucketName = bucketName
        };

        var response = await _s3Client.PutBucketAsync(request);
        
        _logger.LogInformation("Created bucket {bucket} with status {status}", 
            bucketName, response.HttpStatusCode);

        // Wait for the bucket to be available
        var exist = await Amazon.S3.Util.AmazonS3Util.DoesS3BucketExistV2Async(_s3Client, bucketName);
        
        if (!exist)
        {
            _logger.LogInformation("Waiting for bucket {bucket} to be ready", bucketName);
            
            while (!exist)
            {
                await Task.Delay(2000);
                exist = await Amazon.S3.Util.AmazonS3Util.DoesS3BucketExistV2Async(_s3Client, bucketName);
            }
        }
        
        return bucketName;
    }

    /// <summary>
    /// Delete an object from an S3 bucket.
    /// </summary>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="objectKey">The object key to delete.</param>
    /// <returns>The response from the DeleteObjectAsync call.</returns>
    public async Task<DeleteObjectResponse> DeleteObjectAsync(
        string bucketName, string objectKey)
    {
        var request = new DeleteObjectRequest
        {
            BucketName = bucketName,
            Key = objectKey
        };

        return await _s3Client.DeleteObjectAsync(request);
    }

    /// <summary>
    /// Delete an S3 bucket and all its objects.
    /// </summary>
    /// <param name="bucketName">The name of the bucket to delete.</param>
    /// <returns>A boolean value indicating the success of the operation.</returns>
    public async Task<bool> DeleteBucketAsync(string bucketName)
    {
        try
        {
            // Delete all objects in the bucket
            await AmazonS3Util.DeleteS3BucketWithObjectsAsync(_s3Client, bucketName);
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting bucket {bucket}", bucketName);
            return false;
        }
    }

    /// <summary>
    /// Get object metadata.
    /// </summary>
    /// <param name="bucketName">The name of the bucket.</param>
    /// <param name="objectKey">The object key.</param>
    /// <returns>Object metadata.</returns>
    public async Task<GetObjectMetadataResponse> GetObjectMetadataAsync(
        string bucketName, string objectKey)
    {
        var request = new GetObjectMetadataRequest
        {
            BucketName = bucketName,
            Key = objectKey
        };

        return await _s3Client.GetObjectMetadataAsync(request);
    }
}
