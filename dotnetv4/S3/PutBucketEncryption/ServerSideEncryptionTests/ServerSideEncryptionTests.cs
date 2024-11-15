// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using System.Net;
using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using Xunit.Extensions.Ordering;

namespace ServerSideEncryptionTests;

public class ServerSideEncryptionTests
{
    /// <summary>
    /// Test the method setting bucket encryption. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Unit")]
    public async Task TestSetEncryption()
    {
        // Arrange.
        var mockS3Service = new Mock<IAmazonS3>();

        mockS3Service.Setup(client => client.PutBucketEncryptionAsync(
                It.IsAny<PutBucketEncryptionRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((PutBucketEncryptionRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new PutBucketEncryptionResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                });
            });

        ServerSideEncryption.ServerSideEncryption._s3Client = mockS3Service.Object;

        // Act.
        var success =
            await ServerSideEncryption.ServerSideEncryption.SetBucketServerSideEncryption(
                "testName", "testKey");

        // Assert.
        Assert.True(success);

        mockS3Service.Verify(ms => ms.PutBucketEncryptionAsync(
            It.Is<PutBucketEncryptionRequest>(r =>
                r.BucketName == "testName"),
            CancellationToken.None), Times.Once);
    }

    /// <summary>
    /// Test the method getting bucket encryption. Should not have exceptions.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Unit")]
    public async Task TestGetEncryption()
    {
        // Arrange.
        var mockS3Service = new Mock<IAmazonS3>();

        mockS3Service.Setup(client => client.GetBucketEncryptionAsync(
                It.IsAny<GetBucketEncryptionRequest>(),
                It.IsAny<CancellationToken>()))
            .Returns((GetBucketEncryptionRequest r,
                CancellationToken token) =>
            {
                return Task.FromResult(new GetBucketEncryptionResponse()
                {
                    HttpStatusCode = HttpStatusCode.OK,
                    ServerSideEncryptionConfiguration = new ServerSideEncryptionConfiguration()
                    {
                        ServerSideEncryptionRules = new List<ServerSideEncryptionRule>()
                        {
                            new ServerSideEncryptionRule()
                            {
                                ServerSideEncryptionByDefault = new ServerSideEncryptionByDefault()
                                {
                                    ServerSideEncryptionAlgorithm = ServerSideEncryptionMethod.AWSKMS
                                }
                            }
                        }
                    }
                });
            });

        ServerSideEncryption.ServerSideEncryption._s3Client = mockS3Service.Object;

        // Act.
        await ServerSideEncryption.ServerSideEncryption.GetEncryptionSettings(
                "testName");

        // Assert.
        mockS3Service.Verify(ms => ms.GetBucketEncryptionAsync(
            It.Is<GetBucketEncryptionRequest>(r =>
                r.BucketName == "testName"),
            CancellationToken.None), Times.Once);
    }
}