// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Rekognition;
using Amazon.Rekognition.Model;
using Moq;

namespace PamServices.Test;

/// <summary>
/// Tests for the ImageService class.
/// </summary>
public class ImageServiceTests
{
    /// <summary>
    /// Verify that detecting labels returns a collection.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task DetectLabels_ShouldReturnCollection()
    {
        // Arrange.
        var mockService = new Mock<IAmazonRekognition>();
        var responseLabels = new List<Amazon.Rekognition.Model.Label>
        {
            new(){Name = "label1"},
            new(){Name = "label2"},
            new(){Name = "label3"}
        };
        var response = new DetectLabelsResponse { Labels = responseLabels };

        mockService.Setup(ms =>
            ms.DetectLabelsAsync(It.IsAny<DetectLabelsRequest>(), CancellationToken.None).Result).Returns(response);

        var service = new ImageService(mockService.Object);

        // Act.
        var labels = await service.DetectLabels("keyName", "bucketName");

        // Assert.
        Assert.Equal(3, labels.Count);
    }

    /// <summary>
    /// Verify that detecting labels should return an empty collection if no labels are detected.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task DetectLabels_ShouldReturnEmptyCollection()
    {
        // Arrange.
        var mockService = new Mock<IAmazonRekognition>();
        var responseLabels = new List<Amazon.Rekognition.Model.Label>();
        var response = new DetectLabelsResponse { Labels = responseLabels };

        mockService.Setup(ms =>
            ms.DetectLabelsAsync(It.IsAny<DetectLabelsRequest>(), CancellationToken.None).Result).Returns(response);

        var service = new ImageService(mockService.Object);

        // Act.
        var labels = await service.DetectLabels("keyName", "bucketName");

        // Assert.
        Assert.NotNull(labels);
        Assert.Empty(labels);
    }
}