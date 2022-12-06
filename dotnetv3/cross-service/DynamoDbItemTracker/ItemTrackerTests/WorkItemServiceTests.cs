// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Diagnostics;
using System.Net;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.DynamoDBv2.Model;
using Amazon.SimpleEmailV2.Model;
using DynamoDbItemTracker;
using Microsoft.Extensions.Configuration;
using Moq;

namespace ItemTrackerTests;

/// <summary>
/// Tests for WorkItemService.
/// </summary>
public class WorkItemServiceTests
{
    private readonly IConfiguration _configuration;
    private readonly WorkItemService _workItemService;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public WorkItemServiceTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _workItemService = new WorkItemService(new DynamoDBContext(new AmazonDynamoDBClient()), _configuration);
    }

    /// <summary>
    /// Verify that work items can be returned without an archive filter.
    /// </summary>
    [Fact]
    [Order(1)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItems_ShouldReturnCollection()
    {
        var mockContext = new Mock<IDynamoDBContext>();
        var mockScan = new Mock<AsyncSearch<WorkItem>>();
        var response = new List<WorkItem> { new WorkItem(), new WorkItem() };

        // Only return the mock collection if there is no filter.

        mockScan.Setup(ms =>
            ms.GetRemainingAsync(CancellationToken.None).Result).Returns(response);

        mockContext.Setup(mc =>
            mc.FromScanAsync<WorkItem>(
                It.Is<ScanOperationConfig>(r => r.Filter.ToConditions().Count == 0),
                It.IsAny<DynamoDBOperationConfig>())).Returns(mockScan.Object);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var workItems = await service.GetItems(null);
        Assert.Equal(2, workItems.Count);
    }

    /// <summary>
    /// Verify that work items can be returned with an archive filter.
    /// </summary>
    [Fact]
    [Order(2)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemsWithFilter_ShouldReturnCollection()
    {
        var mockContext = new Mock<IDynamoDBContext>();
        var mockScan = new Mock<AsyncSearch<WorkItem>>();
        var response = new List<WorkItem> { new WorkItem(), new WorkItem() };

        // Only return the mock collection if there is no filter.

        mockScan.Setup(ms =>
            ms.GetRemainingAsync(CancellationToken.None).Result).Returns(response);

        mockContext.Setup(mc =>
            mc.FromScanAsync<WorkItem>(
                It.Is<ScanOperationConfig>(r => r.Filter.ToConditions().ContainsKey("archived")),
                It.IsAny<DynamoDBOperationConfig>())).Returns(mockScan.Object);

        var service = new WorkItemService(mockContext.Object, _configuration);
        await service.GetItems(true);
        var workItems = await service.GetItems(true);
        Assert.Equal(2, workItems.Count);
    }

    /// <summary>
    /// Validate getting all items.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItems_ShouldReturnItems()
    {
        var mockContext = new Mock<IDynamoDBContext>();
        var mockScan = new Mock<AsyncSearch<WorkItem>>();
        var response = new List<WorkItem> { new WorkItem(), new WorkItem() };

        // Only return the mock collection if there is no filter.

        mockScan.Setup(ms =>
            ms.GetRemainingAsync(CancellationToken.None).Result).Returns(response);

        mockContext.Setup(mc =>
            mc.FromScanAsync<WorkItem>(
                It.Is<ScanOperationConfig>(r => r.Filter.ToConditions().Count == 0),
                It.IsAny<DynamoDBOperationConfig>())).Returns(mockScan.Object);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var items = await service.GetAllItems();
        Assert.NotEmpty(items);
    }

    /// <summary>
    /// Validate getting items by archive status.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemsByArchiveState_ShouldReturnItems()
    {
        var mockContext = new Mock<IDynamoDBContext>();
        var mockScan = new Mock<AsyncSearch<WorkItem>>();
        var response = new List<WorkItem> { new WorkItem(), new WorkItem() };

        // Only return the mock collection if there is no filter.

        mockScan.Setup(ms =>
            ms.GetRemainingAsync(CancellationToken.None).Result).Returns(response);

        mockContext.Setup(mc =>
            mc.FromScanAsync<WorkItem>(
                It.Is<ScanOperationConfig>(r => r.Filter.ToConditions().ContainsKey("archived")),
                It.IsAny<DynamoDBOperationConfig>())).Returns(mockScan.Object);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var items = await service.GetItemsByArchiveState(false);
        Assert.Equal(2, items.Count);
    }

    /// <summary>
    /// Validate getting an item by ID.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemById_ShouldReturnItem()
    {
        var testId = "testId";
        var testWorkItem = new WorkItem() { Id = testId };
        var mockContext = new Mock<IDynamoDBContext>();
        mockContext.Setup(mc =>
            mc.LoadAsync<WorkItem>(
                It.Is<string>(id => id == testId), CancellationToken.None)).ReturnsAsync(testWorkItem);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var item = await service.GetItem(testId);
        Assert.Equal(testId, item.iditem);
    }

    /// <summary>
    /// Validate creating an item executes the save and load methods and sets an Id.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    [Trait("Category", "Unit")]
    public async Task VerifyCreateItem_ShouldUpdateRecord()
    {
        var testWorkItem = new WorkItem();
        var mockContext = new Mock<IDynamoDBContext>();
        mockContext.Setup(mc =>
            mc.LoadAsync<WorkItem>(
                It.Is<string>(id => !String.IsNullOrEmpty(id)), CancellationToken.None)).ReturnsAsync(testWorkItem);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var result = await service.CreateItem(new WorkItem());

        mockContext.Verify(mc => mc.SaveAsync<WorkItem>(It.Is<WorkItem>(wi => !string.IsNullOrEmpty(wi.iditem)), CancellationToken.None), Times.Once);
        mockContext.Verify(mc => mc.LoadAsync<WorkItem>(It.IsAny<string>(), CancellationToken.None), Times.Once);

        Assert.NotNull(result);
    }

    /// <summary>
    /// Validate archiving an item checks the updated record count.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Unit")]
    public async Task VerifyArchiveItem_ShouldUpdateRecord()
    {
        var testId = "testId";
        var testWorkItem = new WorkItem() { Id = testId };
        var mockContext = new Mock<IDynamoDBContext>();
        mockContext.Setup(mc =>
            mc.LoadAsync<WorkItem>(
                It.Is<string>(id => !String.IsNullOrEmpty(id)), CancellationToken.None)).ReturnsAsync(testWorkItem);

        var service = new WorkItemService(mockContext.Object, _configuration);
        var result = await service.ArchiveItem(testId);

        mockContext.Verify(mc => mc.SaveAsync<WorkItem>(It.Is<WorkItem>(w => w.Id == testId), CancellationToken.None), Times.Once);
        mockContext.Verify(mc => mc.LoadAsync<WorkItem>(It.IsAny<string>(), CancellationToken.None), Times.Exactly(2));

        Assert.True(result.Archived);
    }
}
