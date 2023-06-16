// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System.Text;

namespace PamServices.Test;

/// <summary>
/// Tests for the StorageService class.
/// </summary>
public class StorageServiceTests
{
    /// <summary>
    /// Verify getting a presigned url for upload.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void GetPresignedUrlForImage_ShouldReturnUrl()
    {
        // Arrange.
        var mockService = new Mock<IAmazonS3>();

        var fileName = "imageFile.jpg";
        var bucketName = "imageBucket";

        var service = new StorageService(mockService.Object);

        // Act.
        service.GetPresignedUrlForImage(fileName, bucketName);

        // Assert.
        mockService.Verify(ms => ms.GetPreSignedURL(
            It.Is<GetPreSignedUrlRequest>(r =>
                r.BucketName == bucketName &&
                r.Key.EndsWith(fileName) &&
                r.ContentType == "image/jpeg" &&
                r.Expires > DateTime.UtcNow &&
                r.Verb == HttpVerb.PUT)), Times.Once);
    }

    /// <summary>
    /// Verify getting a presigned url for an archive download.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public void GetPresignedArchiveUrl_ShouldReturnUrl()
    {
        // Arrange.
        var mockService = new Mock<IAmazonS3>();

        var fileName = "archive.zip";
        var bucketName = "archiveBucket";

        var service = new StorageService(mockService.Object);

        // Act.
        service.GetPresignedUrlForArchive(fileName, bucketName);

        // Assert.
        mockService.Verify(ms => ms.GetPreSignedURL(
            It.Is<GetPreSignedUrlRequest>(r =>
                r.BucketName == bucketName &&
                r.Key == fileName &&
                r.Expires > DateTime.UtcNow &&
                r.Verb == HttpVerb.GET)), Times.Once);
    }

    /// <summary>
    /// Verify generating a zip file from images.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task GenerateZip_ShouldReturnUrl()
    {
        // Arrange.
        var mockService = new Mock<IAmazonS3>();

        var imageKeys = new List<string> { "file1.jpg", "file2.jpg" };
        var storageBucketName = "storageBucket";
        var archiveBucketName = "archiveBucket";

        string firstFileContents = "First file.";
        byte[] firstFileBytes = Encoding.UTF8.GetBytes(firstFileContents);

        MemoryStream firstMemoryStream = new MemoryStream(firstFileBytes);

        string secondFileContents = "Second file.";
        byte[] secondFileBytes = Encoding.UTF8.GetBytes(secondFileContents);

        MemoryStream secondMemoryStream = new MemoryStream(secondFileBytes);

        var service = new StorageService(mockService.Object);

        // Mock getting objects.
        var responseFirstFile = new GetObjectResponse()
        { ResponseStream = firstMemoryStream };
        mockService.Setup(ms =>
            ms.GetObjectAsync(
                It.Is<GetObjectRequest>(r => r.Key == "file1.jpg"),
                CancellationToken.None)).ReturnsAsync(responseFirstFile);

        var responseSecondFile = new GetObjectResponse()
        { ResponseStream = secondMemoryStream };
        mockService.Setup(ms =>
            ms.GetObjectAsync(
                It.Is<GetObjectRequest>(r => r.Key == "file2.jpg"),
                CancellationToken.None)).ReturnsAsync(responseSecondFile);

        var responseInitiateUpload = new InitiateMultipartUploadResponse()
        { UploadId = "uploadId" };

        mockService.Setup(ms =>
            ms.InitiateMultipartUploadAsync(
                It.Is<InitiateMultipartUploadRequest>(r => r.BucketName == archiveBucketName),
                CancellationToken.None)).ReturnsAsync(responseInitiateUpload);

        var responseUploadPart1 = new UploadPartResponse()
        {
            PartNumber = 1,
            ETag = "1"
        };

        mockService.Setup(ms =>
            ms.UploadPartAsync(
                It.Is<UploadPartRequest>(r => r.PartNumber == 1),
                CancellationToken.None)).ReturnsAsync(responseUploadPart1);

        var responseCompleteUpload = new CompleteMultipartUploadResponse()
        {
            Key = "completedKey"
        };

        mockService.Setup(ms =>
            ms.CompleteMultipartUploadAsync(
                It.Is<CompleteMultipartUploadRequest>(r => r.UploadId == "uploadId"),
                CancellationToken.None)).ReturnsAsync(responseCompleteUpload);

        // Act.
        await service.GenerateZipFromImages(imageKeys, storageBucketName, archiveBucketName);

        // Assert.
        mockService.Verify(ms => ms.UploadPartAsync(
            It.Is<UploadPartRequest>(r =>
                r.BucketName == archiveBucketName &&
                r.UploadId == "uploadId"), CancellationToken.None), Times.Once);

    }

    /// <summary>
    /// Verify generating a zip file from images can use multiple parts.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task GenerateZip_ShouldUseMultipleParts()
    {
        // Arrange.
        var mockService = new Mock<IAmazonS3>();

        var imageKeys = new List<string> { "file1.jpg", "file2.jpg" };
        var storageBucketName = "storageBucket";
        var archiveBucketName = "archiveBucket";

        byte[] firstFileBytes = new byte[6 * 1024 * 1024];

        MemoryStream firstMemoryStream = new MemoryStream(firstFileBytes);

        byte[] secondFileBytes = new byte[6 * 1024 * 1024];

        MemoryStream secondMemoryStream = new MemoryStream(secondFileBytes);

        var service = new StorageService(mockService.Object);

        // Mock getting objects.
        var responseFirstFile = new GetObjectResponse()
        { ResponseStream = firstMemoryStream };
        mockService.Setup(ms =>
            ms.GetObjectAsync(
                It.Is<GetObjectRequest>(r => r.Key == "file1.jpg"),
                CancellationToken.None)).ReturnsAsync(responseFirstFile);

        var responseSecondFile = new GetObjectResponse()
        { ResponseStream = secondMemoryStream };
        mockService.Setup(ms =>
            ms.GetObjectAsync(
                It.Is<GetObjectRequest>(r => r.Key == "file2.jpg"),
                CancellationToken.None)).ReturnsAsync(responseSecondFile);

        var responseInitiateUpload = new InitiateMultipartUploadResponse()
        { UploadId = "uploadId" };

        mockService.Setup(ms =>
            ms.InitiateMultipartUploadAsync(
                It.Is<InitiateMultipartUploadRequest>(r => r.BucketName == archiveBucketName),
                CancellationToken.None)).ReturnsAsync(responseInitiateUpload);

        var responseUploadPart1 = new UploadPartResponse()
        {
            PartNumber = 1,
            ETag = "1"
        };

        mockService.Setup(ms =>
            ms.UploadPartAsync(
                It.Is<UploadPartRequest>(r => r.PartNumber == 1),
                CancellationToken.None)).ReturnsAsync(responseUploadPart1);

        var responseUploadPart2 = new UploadPartResponse()
        {
            PartNumber = 2,
            ETag = "2"
        };

        mockService.Setup(ms =>
            ms.UploadPartAsync(
                It.Is<UploadPartRequest>(r => r.PartNumber == 2),
                CancellationToken.None)).ReturnsAsync(responseUploadPart2);

        var responseUploadPart3 = new UploadPartResponse()
        {
            PartNumber = 3,
            ETag = "3"
        };

        mockService.Setup(ms =>
            ms.UploadPartAsync(
                It.Is<UploadPartRequest>(r => r.PartNumber == 3),
                CancellationToken.None)).ReturnsAsync(responseUploadPart3);

        var responseCompleteUpload = new CompleteMultipartUploadResponse()
        {
            Key = "completedKey"
        };

        mockService.Setup(ms =>
            ms.CompleteMultipartUploadAsync(
                It.Is<CompleteMultipartUploadRequest>(r => r.UploadId == "uploadId"),
                CancellationToken.None)).ReturnsAsync(responseCompleteUpload);

        // Act.
        await service.GenerateZipFromImages(imageKeys, storageBucketName, archiveBucketName);

        // Assert.
        mockService.Verify(ms => ms.UploadPartAsync(
            It.Is<UploadPartRequest>(r =>
                r.BucketName == archiveBucketName &&
                r.UploadId == "uploadId"), CancellationToken.None), Times.Exactly(3));
    }
}