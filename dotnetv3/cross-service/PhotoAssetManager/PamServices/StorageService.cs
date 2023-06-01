// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using System.IO.Compression;

namespace PamServices;

/// <summary>
/// Service for working with storage for the photo analyzer.
/// </summary>
public class StorageService
{
    private readonly IAmazonS3 _amazonS3;

    public StorageService(IAmazonS3 amazonS3)
    {
        _amazonS3 = amazonS3;
    }

    /// <summary>
    /// Generate a unique filename and presigned url for uploading an image.
    /// </summary>
    /// <param name="fileName">The filename of the image.</param>
    /// <param name="storageBucket">The name of the storage bucket.</param>
    /// <returns>The presigned url.</returns>
    public string GetPresignedUrlForImage(string fileName, string storageBucket)
    {
        var uuid = Guid.NewGuid().ToString();
        var uniqueFileName = $"{uuid}-{fileName}";

        var preSignedUrlResponse = _amazonS3.GetPreSignedURL(
            new GetPreSignedUrlRequest()
            {
                BucketName = storageBucket,
                Key = uniqueFileName,
                ContentType = "image/jpeg",
                Expires = DateTime.UtcNow.AddMinutes(5),
                Verb = HttpVerb.PUT
            });

        return preSignedUrlResponse;
    }

    /// <summary>
    /// Generate a zip file for download for a set of images in a bucket.
    /// </summary>
    /// <param name="imageKeys">The list of image keys.</param>
    /// <param name="storageBucket">The storage bucket for the images.</param>
    /// <param name="workingBucket">The bucket for the zip file.</param>
    /// <returns>A presigned url to download the zip file.</returns>
    public async Task<string> GenerateZipFromImages(List<string> imageKeys,
        string storageBucket, string workingBucket)
    {
        var uuid = Guid.NewGuid().ToString();
        var archiveName = "image_archive_" + uuid + ".zip";

        var uploadId = await InitiateUploadZip(archiveName, workingBucket);

        using MemoryStream zipMS = new MemoryStream();
        var archive = new ZipArchive(zipMS, ZipArchiveMode.Create, true);

        // Add each image to the archive stream as they are fetched.
        foreach (var s3Key in imageKeys)
        {
            var entry = archive.CreateEntry(Path.GetFileName(s3Key),
                CompressionLevel.NoCompression);
            await using var entryStream = entry.Open();
            var request = new GetObjectRequest
            { BucketName = storageBucket, Key = s3Key };
            using var getObjectResponse = await _amazonS3.GetObjectAsync(request);
            await getObjectResponse.ResponseStream.CopyToAsync(entryStream);
        }

        archive.Dispose();
        zipMS.Position = 0;

        // Upload the archive from the stream.
        await UploadZip(archiveName, uploadId, zipMS, workingBucket);

        var downloadUrl = GetPresignedUrlForArchive(archiveName, workingBucket);

        await zipMS.DisposeAsync();
        return downloadUrl;
    }

    /// <summary>
    /// Initiate the multipart upload of the zip archive.
    /// </summary>
    /// <param name="archiveName">The name of the archive.</param>
    /// <param name="archiveBucket">The S3 bucket for the archive.</param>
    /// <returns>The ID for the upload.</returns>
    private async Task<string> InitiateUploadZip(string archiveName,
        string archiveBucket)
    {
        var initiateUploadResponse = await _amazonS3.InitiateMultipartUploadAsync(
            new InitiateMultipartUploadRequest()
            {
                BucketName = archiveBucket,
                Key = archiveName,
                ContentType = "application/zip"
            });
        var uploadId = initiateUploadResponse.UploadId;
        return uploadId;
    }

    /// <summary>
    /// Upload the zip archive with a multipart upload.
    /// </summary>
    /// <param name="archiveName">The name of the archive.</param>
    /// <param name="uploadId">The ID of the upload.</param>
    /// <param name="zipStream">The memory stream of the zip.</param>
    /// <param name="archiveBucket">The S3 bucket for the archive.</param>
    /// <returns>The key for the completed upload.</returns>
    private async Task<string> UploadZip(string archiveName, string uploadId, MemoryStream zipStream, string archiveBucket)
    {
        long contentLength = zipStream.Length;
        long partSize = 5 * (long)Math.Pow(2, 20); // 5 MB
        if (contentLength < partSize)
        {
            partSize = contentLength;
        }
        long filePosition = 0;

        List<UploadPartResponse> uploadResponses = new List<UploadPartResponse>();
        for (int i = 1; filePosition < contentLength; i++)
        {
            UploadPartRequest uploadRequest = new UploadPartRequest
            {
                BucketName = archiveBucket,
                Key = archiveName,
                UploadId = uploadId,
                PartNumber = i,
                PartSize = partSize,
                FilePosition = filePosition,
                InputStream = zipStream
            };

            // Upload a part and add the response to our list.
            uploadResponses.Add(await _amazonS3.UploadPartAsync(uploadRequest));

            filePosition += partSize;
        }

        return await CompleteUploadZip(archiveName, uploadId, archiveBucket, uploadResponses);
    }

    /// <summary>
    /// Complete the multipart upload.
    /// </summary>
    /// <param name="archiveName">The name of the archive.</param>
    /// <param name="uploadId">The ID of the upload.</param>
    /// <param name="archiveBucket">The S3 bucket for the archive.</param>
    /// <param name="uploadResponses">The multipart upload responses.</param>
    /// <returns>The key for the completed upload.</returns>
    private async Task<string> CompleteUploadZip(string archiveName, string uploadId,
        string archiveBucket, List<UploadPartResponse> uploadResponses)
    {
        // Setup to complete the upload.
        CompleteMultipartUploadRequest completeRequest =
            new CompleteMultipartUploadRequest
            {
                BucketName = archiveBucket,
                Key = archiveName,
                UploadId = uploadId
            };
        completeRequest.AddPartETags(uploadResponses);

        // Complete the upload.
        CompleteMultipartUploadResponse completeUploadResponse =
            await _amazonS3.CompleteMultipartUploadAsync(completeRequest);

        return completeUploadResponse.Key;
    }

    /// <summary>
    /// Get a presigned URL for a zip file to download.
    /// </summary>
    /// <param name="archiveKey">The key of the archive object.</param>
    /// <param name="storageBucket">The name of the storage bucket.</param>
    /// <returns>The presigned url.</returns>
    public string GetPresignedUrlForArchive(string archiveKey, string storageBucket)
    {
        var preSignedUrlResponse = _amazonS3.GetPreSignedURL(
            new GetPreSignedUrlRequest()
            {
                BucketName = storageBucket,
                Key = archiveKey,
                Expires = DateTime.UtcNow.AddHours(24),
                Verb = HttpVerb.GET

            });

        return preSignedUrlResponse;
    }
}