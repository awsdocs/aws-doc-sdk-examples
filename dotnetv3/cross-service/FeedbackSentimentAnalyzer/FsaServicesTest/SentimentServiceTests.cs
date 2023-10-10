// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using FsaServices.Services;
using Moq;
using Amazon.Comprehend;
using Amazon.Comprehend.Model;

namespace FsaServicesTest;

/// <summary>
/// Tests for the FSA SentimentService.
/// </summary>
public class SentimentServiceTests
{
    /// <summary>
    /// Verify that translating to English should return a string.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task DetectSentiment_ShouldReturnSentimentDetails()
    {
        // Arrange.
        var mockService = new Mock<IAmazonComprehend>();
        var dominantLanguageResponse = new DetectDominantLanguageResponse()
        {
            Languages = new List<DominantLanguage>(){new(){LanguageCode = "en"}}
        };

        _ = mockService.Setup(ms =>
            ms.DetectDominantLanguageAsync(It.IsAny<DetectDominantLanguageRequest>(),
                CancellationToken.None)).ReturnsAsync(dominantLanguageResponse);

        var detectSentimentResponse = new DetectSentimentResponse()
        {
            Sentiment = SentimentType.POSITIVE
        };

        _ = mockService.Setup(ms =>
            ms.DetectSentimentAsync(It.IsAny<DetectSentimentRequest>(),
                CancellationToken.None)).ReturnsAsync(detectSentimentResponse);

        var service = new SentimentService(mockService.Object);

        // Act.
        var sentimentDetails = await service.AnalyzeTextSentiment("Text to analyze.");

        // Assert.
        Assert.Equal("en", sentimentDetails.LanguageCode);
        Assert.Equal(SentimentType.POSITIVE, sentimentDetails.Sentiment);
    }

    /// <summary>
    /// Verify that an empty string should throw an Invalid Operation Exception.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task AnalyzeEmptyString_ShouldThrowException()
    {
        // Arrange.
        var mockService = new Mock<IAmazonComprehend>();
        var dominantLanguageResponse = new DetectDominantLanguageResponse()
        {
            Languages = new List<DominantLanguage>() { new() { LanguageCode = "en" } }
        };

        _ = mockService.Setup(ms =>
            ms.DetectDominantLanguageAsync(It.IsAny<DetectDominantLanguageRequest>(),
                CancellationToken.None)).ReturnsAsync(dominantLanguageResponse);

        var detectSentimentResponse = new DetectSentimentResponse()
        {
            Sentiment = SentimentType.POSITIVE
        };

        _ = mockService.Setup(ms =>
            ms.DetectSentimentAsync(It.IsAny<DetectSentimentRequest>(),
                CancellationToken.None)).ReturnsAsync(detectSentimentResponse);

        var service = new SentimentService(mockService.Object);

        // Act and Assert.
        await Assert.ThrowsAsync<InvalidOperationException>(async () =>
        {
            await service.AnalyzeTextSentiment("");
        });
    }
}