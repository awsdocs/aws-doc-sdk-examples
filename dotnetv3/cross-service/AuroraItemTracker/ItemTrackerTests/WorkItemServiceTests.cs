// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using Amazon.RDSDataService;
using Amazon.RDSDataService.Model;
using AuroraItemTracker;
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

        _workItemService = new WorkItemService(new AmazonRDSDataServiceClient(), _configuration);
    }

    /// <summary>
    /// Verify running a request returns results from Amazon Relational Database Service (Amazon RDS).
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task VerifyCanRunRDSStatement_ShouldReturnResult()
    {
        var tableName = _configuration["WorkItemTable"];
        var request = _workItemService.GetRDSRequest($"SELECT * FROM {tableName};");
        var result = await _workItemService.RunRDSStatement(request);
        Assert.NotNull(result.FormattedRecords);
    }

    /// <summary>
    /// Verify that work items can be returned without an archive filter.
    /// </summary>
    [Fact]
    [Order(2)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItems_ShouldReturnCollection()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };

        // Only return the mock collection if there are no parameters.
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.Is<ExecuteStatementRequest>(r => r.Parameters == null),
                CancellationToken.None).Result).Returns(response);

        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var workItems = await service.GetItems(null);
        Assert.Equal(2, workItems.Count);
    }

    /// <summary>
    /// Verify that work items can be returned with an archive filter.
    /// </summary>
    [Fact]
    [Order(3)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemsWithFilter_ShouldReturnCollection()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };

        // Only return the mock collection if there is exactly one parameter.
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.Is<ExecuteStatementRequest>(r => r.Parameters.Count == 1),
                CancellationToken.None).Result).Returns(response);

        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        await service.GetItems(true);
        var workItems = await service.GetItems(true);
        Assert.Equal(2, workItems.Count);
    }

    /// <summary>
    /// Verify that work items are deserialized from a response.
    /// </summary>
    [Fact]
    [Order(4)]
    [Trait("Category", "Unit")]
    public void VerifyGetItemsFromResponse_ShouldReturnCollection()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        ExecuteStatementResponse response = new ExecuteStatementResponse()
        {
            FormattedRecords = responseString
        };
        var workItems = _workItemService.GetItemsFromResponse(response);
        Assert.Equal(2, workItems.Length);
    }

    /// <summary>
    /// Verify that a string parameter is correctly added with its value.
    /// </summary>
    [Fact]
    [Order(5)]
    [Trait("Category", "Unit")]
    public void VerifyAddStringParameter_ShouldReturnWithParameter()
    {
        var sqlParameters = new List<SqlParameter>();
        _workItemService.AddStringParameter(sqlParameters, "testParam", "testValue");
        Assert.Equal("testParam", sqlParameters.First().Name);
        Assert.Equal("testValue", sqlParameters.First().Value.StringValue);
    }

    /// <summary>
    /// Verify that an RDS request has the correct configuration.
    /// </summary>
    [Fact]
    [Order(6)]
    [Trait("Category", "Unit")]
    public void VerifyGetRDSRequest_ShouldHaveConfigValues()
    {
        var sqlParameters = new List<SqlParameter>();
        _workItemService.AddStringParameter(sqlParameters, "testParam", "testValue");
        var rdsRequest = _workItemService.GetRDSRequest("test sql", sqlParameters);
        var secretArn = _configuration["RDSSecretArn"];
        var resourceArn = _configuration["RDSResourceArn"];
        Assert.Equal(secretArn, rdsRequest.SecretArn);
        Assert.Equal(resourceArn, rdsRequest.ResourceArn);
        Assert.Equal("test sql", rdsRequest.Sql);
        Assert.Equal("JSON", rdsRequest.FormatRecordsAs);
    }

    /// <summary>
    /// Validate correct parameters and sql for getting all items.
    /// </summary>
    [Fact]
    [Order(7)]
    [Trait("Category", "Unit")]
    public void VerifyGetAllItemsRequest_ShouldMatchRequestSql()
    {
        var rdsRequest = _workItemService.GetAllItemsRequest();
        var tableName = _configuration["WorkItemTable"];
        Assert.Equal($"SELECT * FROM {tableName};", rdsRequest.Sql);
        Assert.Null(rdsRequest.Parameters);
    }

    /// <summary>
    /// Validate correct parameters and sql for getting archived items.
    /// </summary>
    [Fact]
    [Order(8)]
    [Trait("Category", "Unit")]
    public void VerifyGetArchivedItems_ShouldMatchRequestSql()
    {
        var rdsRequest = _workItemService.GetItemsByArchiveStateRequest(true);
        var tableName = _configuration["WorkItemTable"];
        Assert.Equal($"SELECT * FROM {tableName} WHERE archived = :isArchived;", rdsRequest.Sql);
        Assert.Equal("isArchived", rdsRequest.Parameters.First().Name);
        Assert.Equal("1", rdsRequest.Parameters.First().Value.StringValue);
    }

    /// <summary>
    /// Validate correct parameters and sql for getting an item by ID.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    [Trait("Category", "Unit")]
    public void VerifyGetById_ShouldMatchRequestSql()
    {
        var rdsRequest = _workItemService.GetItemByIdRequest("sampleId");
        var tableName = _configuration["WorkItemTable"];
        Assert.Equal($"SELECT * FROM {tableName} WHERE id = :itemId;", rdsRequest.Sql);
        Assert.Equal("itemId", rdsRequest.Parameters.First().Name);
        Assert.Equal("sampleId", rdsRequest.Parameters.First().Value.StringValue);
    }

    /// <summary>
    /// Validate correct parameters and sql for creating a new work item.
    /// </summary>
    [Fact]
    [Order(10)]
    [Trait("Category", "Unit")]
    public void VerifyCreateItemRequest_ShouldMatchRequestSql()
    {
        var workItem = new WorkItem
        {
            Description = "test description",
            Guide = "test guide",
            Status = "complete",
            Name = "username",
            Archived = true
        };
        var rdsRequest = _workItemService.CreateItemRequest(workItem);
        var tableName = _configuration["WorkItemTable"];
        Assert.Equal($"INSERT INTO {tableName} VALUES (" +
                     $":itemId," +
                     $":description," +
                     $":guide," +
                     $":status," +
                     $":user," +
                     $":archived);", rdsRequest.Sql);
        // Verify the parameters contain all the parameters we expect.
        Assert.Collection(rdsRequest.Parameters,
            p =>
            {
                Assert.Equal("itemId", p.Name);
                Assert.Equal(workItem.Id, p.Value.StringValue);
            },
            p =>
            {
                Assert.Equal(workItem.Description, p.Value.StringValue);
                Assert.Equal("description", p.Name);
            },
            p =>
            {
                Assert.Equal("guide", p.Name);
                Assert.Equal(workItem.Guide, p.Value.StringValue);

            },
            p =>
            {
                Assert.Contains("status", p.Name);
                Assert.Contains(workItem.Status, p.Value.StringValue);
            },
            p =>
            {
                Assert.Contains("user", p.Name);
                Assert.Contains(workItem.Name, p.Value.StringValue);
            },
            p =>
            {
                Assert.Contains("archived", p.Name);
                Assert.Contains("1", p.Value.StringValue);
            });
    }

    /// <summary>
    /// Validate correct parameters and sql for archiving an item.
    /// </summary>
    [Fact]
    [Order(11)]
    [Trait("Category", "Unit")]
    public void VerifyArchiveItemRequest_ShouldMatchRequestSql()
    {
        var rdsRequest = _workItemService.GetArchiveItemRequest("sampleId");
        var tableName = _configuration["WorkItemTable"];
        Assert.Equal($"UPDATE {tableName} SET archived = 1 WHERE id = :itemId;", rdsRequest.Sql);
        Assert.Equal("itemId", rdsRequest.Parameters.First().Name);
        Assert.Equal("sampleId", rdsRequest.Parameters.First().Value.StringValue);
    }

    /// <summary>
    /// Validate getting all items.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItems_ShouldReturnItems()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);

        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var items = await service.GetAllItems();
        Assert.NotEmpty(items);
    }

    /// <summary>
    /// Validate getting items by archive status.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(13)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemsByArchiveState_ShouldReturnItems()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);

        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var items = await service.GetItemsByArchiveState(false);
        Assert.Equal(2, items.Count);
    }

    /// <summary>
    /// Validate getting an item by ID.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(14)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemById_ShouldReturnItem()
    {
        var responseString = _configuration["WorkItemCollectionResponseString"];
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);

        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var item = await service.GetItem("sampleId");
        Assert.NotNull(item);
    }

    /// <summary>
    /// Validate getting an item by ID can return not found.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(15)]
    [Trait("Category", "Unit")]
    public async Task VerifyGetItemById_ShouldThrowException()
    {
        var responseString = "[]";
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { FormattedRecords = responseString };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);
        var service = new WorkItemService(mockRDSDataService.Object, _configuration);

        await Assert.ThrowsAsync<NotFoundException>(async () =>
        {
            await service.GetItem("sampleId");
        });
    }

    /// <summary>
    /// Validate creating an item checks the updated record count.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(16)]
    [Trait("Category", "Unit")]
    public async Task VerifyCreateItem_ShouldUpdateRecord()
    {
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { NumberOfRecordsUpdated = 1, HttpStatusCode = HttpStatusCode.OK };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);
        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var result = await service.CreateItem(new WorkItem());
        Assert.True(result);
    }

    /// <summary>
    /// Validate archiving an item checks the updated record count.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(17)]
    [Trait("Category", "Unit")]
    public async Task VerifyArchiveItem_ShouldUpdateRecord()
    {
        var mockRDSDataService = new Mock<IAmazonRDSDataService>();
        var response = new ExecuteStatementResponse { NumberOfRecordsUpdated = 1, HttpStatusCode = HttpStatusCode.OK };
        mockRDSDataService.Setup(ds =>
            ds.ExecuteStatementAsync(
                It.IsAny<ExecuteStatementRequest>(),
                CancellationToken.None).Result).Returns(response);
        var service = new WorkItemService(mockRDSDataService.Object, _configuration);
        var result = await service.ArchiveItem("sampleId");
        Assert.True(result);
    }
}
