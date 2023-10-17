// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Polly;
using Amazon.Polly.Model;
using Amazon.S3;
using FsaServices.Models;
using FsaServices.Services;
using Moq;

namespace FsaServicesTest;

/// <summary>
/// Tests for the FSA SynthesizeService.
/// </summary>
public class SynthesizeServiceTests
{
    /// <summary>
    /// Verify that translating to English returns a string.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task SynthesizeSpeech_ShouldReturnObjectKey()
    {
        // Arrange.
        var mockService = new Mock<IAmazonPolly>();
        var synthesizeSpeechResponse = new SynthesizeSpeechResponse()
        {
            AudioStream = new MemoryStream(),
        };

        _ = mockService.Setup(ms =>
            ms.SynthesizeSpeechAsync(It.IsAny<SynthesizeSpeechRequest>(),
                CancellationToken.None)).ReturnsAsync(synthesizeSpeechResponse);

        var mockS3Service = new Mock<IAmazonS3>();

        var service = new SynthesizeService(mockService.Object, mockS3Service.Object);

        // Act.
        var audioKey = await service.SynthesizeSpeechFromText(new AudioSourceDestinationDetails()
        {
            bucket = "BucketName",
            Object = "ObjectKey.jpg",
            translated_text = "Text to synthesize."
        });

        // Assert.
        Assert.Equal("ObjectKey.jpg.mp3", audioKey);
    }

    /// <summary>
    /// Verify that an empty string throws an Invalid Operation Exception.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task AnalyzeEmptyString_ShouldThrowException()
    {
        // Arrange.
        var mockService = new Mock<IAmazonPolly>();
        var synthesizeSpeechResponse = new SynthesizeSpeechResponse()
        {
            AudioStream = new MemoryStream(),
        };

        _ = mockService.Setup(ms =>
            ms.SynthesizeSpeechAsync(It.IsAny<SynthesizeSpeechRequest>(),
                CancellationToken.None)).ReturnsAsync(synthesizeSpeechResponse);

        var mockS3Service = new Mock<IAmazonS3>();

        var service = new SynthesizeService(mockService.Object, mockS3Service.Object);

        // Act and Assert.
        await Assert.ThrowsAsync<InvalidOperationException>(async () =>
        {
            await service.SynthesizeSpeechFromText(new AudioSourceDestinationDetails()
            {
                bucket = "BucketName",
                Object = "ObjectKey.jpg",
                translated_text = ""
            });
        });
    }
}