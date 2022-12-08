// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDbItemTracker;

/// <summary>
/// Class for working with WorkItems using the Amazon DynamoDB context.
/// </summary>
public class WorkItemService
{
    private readonly IDynamoDBContext _amazonDynamoDbContext;

    /// <summary>
    /// Constructor that uses the injected Amazon DynamoDB context.
    /// </summary>
    /// <param name="amazonDynamoDbContext">Amazon DynamoDB context.</param>
    public WorkItemService(IDynamoDBContext amazonDynamoDbContext)
    {
        _amazonDynamoDbContext = amazonDynamoDbContext;
    }

    /// <summary>
    /// Get items, with or without an archive filter.
    /// </summary>
    /// <returns>A collection of WorkItems.</returns>
    public async Task<IList<WorkItem>> GetItems(bool? archived)
    {
        IList<WorkItem> result;

        switch (archived)
        {
            // If status is not sent, select all items.
            case null:
                result = await GetAllItems();
                break;
            default:
                result = await GetItemsByArchiveState(archived.Value);
                break;
        }

        return result;
    }

    /// <summary>
    /// Get all items.
    /// </summary>
    /// <returns>A collection of WorkItems.</returns>
    public async Task<IList<WorkItem>> GetAllItems()
    {
        var scan = _amazonDynamoDbContext.FromScanAsync<WorkItem>(
            new ScanOperationConfig()
        );

        var scanResponse = await scan.GetRemainingAsync();
        return scanResponse;
    }

    /// <summary>
    /// Get the items with a particular archive state.
    /// </summary>
    /// <param name="isArchived">The archive state of the items to get.</param>
    /// <returns>A collection of WorkItems.</returns>
    public async Task<IList<WorkItem>> GetItemsByArchiveState(bool isArchived)
    {
        var scanFilter = new ScanFilter();
        scanFilter.AddCondition("archived", ScanOperator.Equal, isArchived);
        var scan = _amazonDynamoDbContext.FromScanAsync<WorkItem>(new ScanOperationConfig()
        {
            Filter = scanFilter
        });

        var scanResponse = await scan.GetRemainingAsync();

        return scanResponse!;
    }

    /// <summary>
    /// Get an item by its ID.
    /// </summary>
    /// <param name="itemId">The ID of the item to get.</param>
    /// <returns>A WorkItem instance.</returns>
    public async Task<WorkItem> GetItem(string itemId)
    {
        var item = await _amazonDynamoDbContext.LoadAsync<WorkItem>(itemId);

        return item;
    }

    /// <summary>
    /// Create a new work item in the table.
    /// </summary>
    /// <param name="workItem">The new work item.</param>
    /// <returns>Async task.</returns>
    public async Task<WorkItem> CreateItem(WorkItem workItem)
    {
        // Assign a new ID to the work item.
        workItem.Id = Guid.NewGuid().ToString();
        await _amazonDynamoDbContext.SaveAsync(workItem);
        return await _amazonDynamoDbContext.LoadAsync<WorkItem>(workItem.Id);
    }

    /// <summary>
    /// Archive a work item.
    /// </summary>
    /// <param name="itemId">The ID of the item to archive.</param>
    /// <returns>True if successful.</returns>
    public async Task<WorkItem> ArchiveItem(string itemId)
    {
        var item = await _amazonDynamoDbContext.LoadAsync<WorkItem>(itemId);
        item.Archived = true;
        await _amazonDynamoDbContext.SaveAsync(item);
        return await _amazonDynamoDbContext.LoadAsync<WorkItem>(itemId);
    }

}