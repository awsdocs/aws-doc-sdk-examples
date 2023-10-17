// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Translate;
using Amazon.Translate.Model;
using FsaServices.Services;
using Moq;

namespace FsaServicesTest;

/// <summary>
/// Tests for the FSA TranslationService.
/// </summary>
public class TranslationServiceTests
{
    /// <summary>
    /// Verify that translating to English returns a string.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task TranslateToEnglish_ShouldReturnString()
    {
        // Arrange.
        var mockService = new Mock<IAmazonTranslate>();
        var responseText = "Text in English.";
        var response = new TranslateTextResponse { TranslatedText = responseText };

        _ = mockService.Setup(ms =>
            ms.TranslateTextAsync(It.IsAny<TranslateTextRequest>(), CancellationToken.None)).ReturnsAsync(response);

        var service = new TranslationService(mockService.Object);

        // Act.
        var translation = await service.TranslateToEnglish("Text in French.", "fr");

        // Assert.
        Assert.Equal(responseText, translation);
    }

    /// <summary>
    /// Verify that an empty string throws an Invalid Operation Exception.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task TranslateEmptyString_ShouldThrowException()
    {
        // Arrange.
        var mockService = new Mock<IAmazonTranslate>();
        var responseText = String.Empty;
        var response = new TranslateTextResponse { TranslatedText = responseText };

        _ = mockService.Setup(ms =>
            ms.TranslateTextAsync(It.IsAny<TranslateTextRequest>(), CancellationToken.None)).ReturnsAsync(response);

        var service = new TranslationService(mockService.Object);

        // Act and Assert.
        await Assert.ThrowsAsync<InvalidOperationException>(async () =>
        {
            await service.TranslateToEnglish("", "fr");
        });
    }
}