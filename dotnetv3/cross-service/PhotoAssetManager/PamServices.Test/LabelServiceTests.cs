// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;
using Moq;

namespace PamServices.Test;

/// <summary>
/// Tests for the LabelService class.
/// </summary>
public class LabelServiceTests
{
    /// <summary>
    /// Verify creating a new Label returns a new record.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task VerifyCreateLabel_ShouldReturnNew()
    {
        // Arrange.
        var mockContext = new Mock<IDynamoDBContext>();
        var newLabel = new Label
        { LabelID = "newLabel", Images = new List<string>() { "newLabelImage.jpg" } };

        // Mock adding a new label.
        mockContext.Setup(mc =>
            mc.LoadAsync<Label>(
                It.Is<string>(k => k == newLabel.LabelID),
                CancellationToken.None)).ReturnsAsync(newLabel);

        var service = new LabelService(mockContext.Object);

        // Act.
        var newLabelResponse = await service.CreateItem(newLabel);

        // Assert.
        mockContext.Verify(mc => mc.SaveAsync<Label>(
            It.Is<Label>(l => l.LabelID == "newLabel"), CancellationToken.None), Times.Once);
        Assert.Equal(newLabel, newLabelResponse);
    }

    /// <summary>
    /// Verify that the service can get all items.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task VerifyGetAllItems_ShouldReturnCollection()
    {
        // Arrange.
        var mockContext = new Mock<IDynamoDBContext>();
        var mockScan = new Mock<AsyncSearch<Label>>();
        var response = new List<Label> { new() { LabelID = "BLabel" }, new() { LabelID = "ALabel" } };

        // Only return the mock collection if there is no filter.

        mockScan.Setup(ms =>
            ms.GetRemainingAsync(CancellationToken.None).Result).Returns(response);

        mockContext.Setup(mc =>
            mc.FromScanAsync<Label>(
                It.Is<ScanOperationConfig>(r => r.Filter.ToConditions().Count == 0),
                It.IsAny<DynamoDBOperationConfig>())).Returns(mockScan.Object);

        var service = new LabelService(mockContext.Object);

        // Act.
        var labels = await service.GetAllItems();

        // Assert.
        Assert.Equal(2, labels.Count);
        Assert.True(labels.First().LabelID == "ALabel");
    }

    /// <summary>
    /// Verify that adding an image to a Label creates a new Label and list.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task VerifyAddImageLabels_ShouldAddNewImage()
    {
        // Arrange.
        var mockContext = new Mock<IDynamoDBContext>();

        var imageName = "image1.jpg";
        var labelsList = new List<string>() { "label1", "label2" };

        // Mock adding a new label.
        mockContext.Setup(mc =>
            mc.LoadAsync<Label>(
                It.Is<string>(k => labelsList.Contains(k)),
                CancellationToken.None)).ReturnsAsync((Label)null!);

        var service = new LabelService(mockContext.Object);

        // Act.
        await service.AddImageLabels(imageName, labelsList);

        // Assert.
        mockContext.Verify(mc => mc.SaveAsync<Label>(
            It.Is<Label>(l =>
                l.LabelID == "label1" && l.Images.Count == 1 && l.Images.Contains("image1.jpg")),
            CancellationToken.None), Times.Once);

        mockContext.Verify(mc => mc.SaveAsync<Label>(
            It.Is<Label>(l =>
                l.LabelID == "label2" && l.Images.Count == 1 && l.Images.Contains("image1.jpg")),
            CancellationToken.None), Times.Once);
    }

    /// <summary>
    /// Verify that adding an image to a label updates the image list.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task VerifyAddImageLabels_ShouldAddToList()
    {
        // Arrange.
        var mockContext = new Mock<IDynamoDBContext>();

        var imageName = "image2.jpg";
        var labelsList = new List<string>() { "label1" };

        // Mock adding a new label.
        mockContext.Setup(mc =>
            mc.LoadAsync<Label>(
                It.Is<string>(k => k == "label1"),
                CancellationToken.None)).ReturnsAsync(new Label() { LabelID = "label1", Images = new List<string>() { "image1.jpg" } });

        var service = new LabelService(mockContext.Object);

        // Act.
        await service.AddImageLabels(imageName, labelsList);

        // Assert.
        mockContext.Verify(mc => mc.SaveAsync<Label>(
            It.Is<Label>(l =>
                l.LabelID == "label1" && l.Images.Count == 2 && l.Images.Contains("image1.jpg")
                && l.Images.Contains("image2.jpg")),
            CancellationToken.None), Times.Once);
    }

    /// <summary>
    /// Verify that getting all images for Labels should return a distinct collection of images.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task VerifyGetAllImagesForLabels_ShouldReturnDistinctCollection()
    {
        // Arrange.
        var mockContext = new Mock<IDynamoDBContext>();

        var labelsList = new List<string>() { "label1", "label2" };

        // Mock adding a new label.
        mockContext.Setup(mc =>
            mc.LoadAsync<Label>(
                It.Is<string>(k => k == "label1"),
                CancellationToken.None)).ReturnsAsync(
            new Label() { LabelID = "label1", Images = new List<string>() { "image1.jpg", "image2.jpg", "image3.jpg" } });

        mockContext.Setup(mc =>
            mc.LoadAsync<Label>(
                It.Is<string>(k => k == "label2"),
                CancellationToken.None)).ReturnsAsync(
            new Label() { LabelID = "label2", Images = new List<string>() { "image2.jpg", "image3.jpg" } });

        var service = new LabelService(mockContext.Object);

        // Act.
        var images = await service.GetAllImagesForLabels(labelsList);

        // Assert.
        Assert.Contains("image1.jpg", images);
        Assert.Contains("image2.jpg", images);
        Assert.Contains("image3.jpg", images);
        Assert.Equal(3, images.Count);
    }
}