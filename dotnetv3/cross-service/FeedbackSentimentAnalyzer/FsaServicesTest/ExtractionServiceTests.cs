// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Textract;
using Amazon.Textract.Model;
using FsaServices.Services;
using Moq;

namespace FsaServicesTest;

/// <summary>
/// Tests for the FSA ExtractionService.
/// </summary>
public class ExtractionServiceTests
{
    /// <summary>
    /// Verify that extracting words from an object returns a string.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task SynthesizeSpeech_ShouldReturnObjectKey()
    {
        // Arrange.
        var mockService = new Mock<IAmazonTextract>();
        var detectDocumentTextResponse = new DetectDocumentTextResponse()
        {
            Blocks = new List<Block>()
            {
                new(){BlockType = BlockType.WORD, Text = "This"},
                new(){BlockType = BlockType.WORD, Text = "is"},
                new(){BlockType = BlockType.WORD, Text = "the"},
                new(){BlockType = BlockType.WORD, Text = "text"},
                new(){BlockType = BlockType.LINE}
            }
        };

        _ = mockService.Setup(ms =>
            ms.DetectDocumentTextAsync(It.IsAny<DetectDocumentTextRequest>(),
                CancellationToken.None)).ReturnsAsync(detectDocumentTextResponse);

        var service = new ExtractionService(mockService.Object);

        // Act.
        var words = await service.ExtractWordsFromBucketObject("testBucket", "testName");

        // Assert.
        Assert.Equal("This is the text", words);
    }

    /// <summary>
    /// Verify that no words in the extraction returns an empty string.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task ExtractNoWords_ShouldReturnEmptyString()
    {
        // Arrange.
        var mockService = new Mock<IAmazonTextract>();
        var detectDocumentTextResponse = new DetectDocumentTextResponse()
        {
            Blocks = new List<Block>()
            {
                new(){BlockType = BlockType.LINE}
            }
        };

        _ = mockService.Setup(ms =>
            ms.DetectDocumentTextAsync(It.IsAny<DetectDocumentTextRequest>(),
                CancellationToken.None)).ReturnsAsync(detectDocumentTextResponse);

        var service = new ExtractionService(mockService.Object);

        // Act.
        var words = await service.ExtractWordsFromBucketObject("testBucket", "testName");

        // Assert.
        Assert.Equal("", words);
    }
}