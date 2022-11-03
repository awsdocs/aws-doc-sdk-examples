// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using System.Text.Json;
using Amazon.RDSDataService;
using Amazon.RDSDataService.Model;

namespace AuroraItemTracker;

/// <summary>
/// Class for working with WorkItems using the Amazon Relational Database Service (Amazon RDS) data service.
/// </summary>
public class WorkItemService
{
    private readonly IAmazonRDSDataService _amazonRDSDataService;
    private readonly IConfiguration _configuration;
    private readonly string _databaseName;
    private readonly string _tableName;

    /// <summary>
    /// Constructor that uses the injected Amazon RDS Data Service client.
    /// </summary>
    /// <param name="amazonRDSDataService">Amazon RDS Data Service.</param>
    /// <param name="configuration">App configuration.</param>
    public WorkItemService(IAmazonRDSDataService amazonRDSDataService, IConfiguration configuration)
    {
        _amazonRDSDataService = amazonRDSDataService;
        _configuration = configuration;
        _databaseName = configuration["Database"];
        _tableName = configuration["WorkItemTable"];
    }

    /// <summary>
    /// Run a SQL statement using the Amazon RDS Data Service.
    /// </summary>
    /// <param name="sql">The SQL statement.</param>
    /// <param name="parameters">Optional parameters for the statement.</param>
    /// <returns>The statement response.</returns>
    public async Task<ExecuteStatementResponse> RunRDSStatement(ExecuteStatementRequest request)
    {
        var statementResult = await _amazonRDSDataService.ExecuteStatementAsync(
            request);
        return statementResult;
    }

    /// <summary>
    /// Get an RDS request for a statement to run.
    /// </summary>
    /// <param name="sql">The SQL statement.</param>
    /// <param name="parameters">Optional parameters for the statement.</param>
    /// <returns>The statement response.</returns>
    public ExecuteStatementRequest GetRDSRequest(string sql, List<SqlParameter> parameters = null!)
    {
        return new ExecuteStatementRequest
        {
            Database = _databaseName,
            FormatRecordsAs = "json",
            Sql = sql,
            Parameters = parameters,
            SecretArn = _configuration["RDSSecretArn"],
            ResourceArn = _configuration["RDSResourceArn"]
        };
    }

    /// <summary>
    /// Convert a statement response to a collection of WorkItems.
    /// </summary>
    /// <param name="statementResult">The response from the data service.</param>
    /// <returns>The collection of WorkItems.</returns>
    public WorkItem[] GetItemsFromResponse(ExecuteStatementResponse statementResult)
    {
        var results = JsonSerializer.Deserialize<WorkItem[]>(
            statementResult.FormattedRecords,
            new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });
        return results!;
    }

    /// <summary>
    /// Add a string parameter to a parameter collection.
    /// </summary>
    /// <param name="parameters">The parameter collection.</param>
    /// <param name="name">Name of the parameter.</param>
    /// <param name="value">Value for the parameter.</param>
    public void AddStringParameter(List<SqlParameter> parameters, string name, string value)
    {
        parameters.Add(new SqlParameter()
        {
            Name = name,
            Value = new Field { StringValue = value }
        });
    }

    /// <summary>
    /// Get all items request.
    /// </summary>
    /// <returns>The request to run.</returns>
    public ExecuteStatementRequest GetAllItemsRequest()
    {
        var request = GetRDSRequest($"SELECT * FROM {_tableName};");

        return request!;
    }

    /// <summary>
    /// Get a request for the items with a particular archive state.
    /// </summary>
    /// <param name="isArchived">The archive state of the items to get.</param>
    /// <returns>The request to run.</returns>
    public ExecuteStatementRequest GetItemsByArchiveStateRequest(bool isArchived)
    {
        // Set up the parameters.
        var parameters = new List<SqlParameter>();
        AddStringParameter(parameters, "isArchived", Convert.ToInt16(isArchived).ToString());

        var statement = GetRDSRequest(
            $"SELECT * FROM {_tableName} WHERE archived = :isArchived;",
            parameters);

        return statement!;
    }

    /// <summary>
    /// Get a request for an item by its ID.
    /// </summary>
    /// <param name="itemId">The ID of the item to get.</param>
    /// <returns>The request to run.</returns>
    public ExecuteStatementRequest GetItemByIdRequest(string itemId)
    {
        // Set up the parameters.
        var parameters = new List<SqlParameter>();
        AddStringParameter(parameters, "itemId", itemId);

        var statement = GetRDSRequest(
            $"SELECT * FROM {_tableName} WHERE id = :itemId;",
            parameters);

        return statement;
    }

    /// <summary>
    /// Get a request to create a new work item in the table.
    /// </summary>
    /// <param name="workItem">The WorkItem to create.</param>
    /// <returns>The request to run.</returns>
    public ExecuteStatementRequest CreateItemRequest(WorkItem workItem)
    {
        // Assign a new ID to the work item.
        workItem.Id = Guid.NewGuid().ToString();

        // Set up the parameters.
        var parameters = new List<SqlParameter>();
        AddStringParameter(parameters, "itemId", workItem.Id);
        AddStringParameter(parameters, "description", workItem.Description);
        AddStringParameter(parameters, "guide", workItem.Guide);
        AddStringParameter(parameters, "status", workItem.Status);
        AddStringParameter(parameters, "user", workItem.Name);
        AddStringParameter(parameters, "archived",
            Convert.ToInt16(workItem.Archived).ToString());

        var statement = GetRDSRequest(
            $"INSERT INTO {_tableName} VALUES (" +
            $":itemId," +
            $":description," +
            $":guide," +
            $":status," +
            $":user," +
            $":archived);",
            parameters);

        return statement;
    }

    /// <summary>
    /// Get a request to archive a work item.
    /// </summary>
    /// <param name="itemId">The ID of the item to archive.</param>
    /// <returns>The request to run.</returns>
    public ExecuteStatementRequest GetArchiveItemRequest(string itemId)
    {
        // Set up the parameters.
        var parameters = new List<SqlParameter>();
        AddStringParameter(parameters, "itemId", itemId);

        var statement = GetRDSRequest(
            $"UPDATE {_tableName} SET archived = 1 WHERE id = :itemId;",
            parameters);

        return statement;
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
        var statementResult = await RunRDSStatement(GetAllItemsRequest());

        var results = GetItemsFromResponse(statementResult);

        return results!;
    }

    /// <summary>
    /// Get the items with a particular archive state.
    /// </summary>
    /// <param name="isArchived">The archive state of the items to get.</param>
    /// <returns>A collection of WorkItems.</returns>
    public async Task<IList<WorkItem>> GetItemsByArchiveState(bool isArchived)
    {
        var statementResult =
            await RunRDSStatement(GetItemsByArchiveStateRequest(isArchived));

        var results = GetItemsFromResponse(statementResult);
        return results!;
    }

    /// <summary>
    /// Get an item by its ID.
    /// </summary>
    /// <param name="itemId">The ID of the item to get.</param>
    /// <returns>A WorkItem instance.</returns>
    public async Task<WorkItem> GetItem(string itemId)
    {
        var statementResult = await RunRDSStatement(GetItemByIdRequest(itemId));

        var results = GetItemsFromResponse(statementResult);

        return results.Any()
            ? results.First()
            : throw new NotFoundException(
                $"Work item could not be found with id {itemId}");
    }

    /// <summary>
    /// Create a new work item in the table.
    /// </summary>
    /// <param name="workItem">The new work item.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> CreateItem(WorkItem workItem)
    {
        var statementResult = await RunRDSStatement(CreateItemRequest(workItem));

        return statementResult.HttpStatusCode == HttpStatusCode.OK &&
               statementResult.NumberOfRecordsUpdated == 1;
    }

    /// <summary>
    /// Archive a work item.
    /// </summary>
    /// <param name="itemId">The ID of the item to archive.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> ArchiveItem(string itemId)
    {
        var statementResult = await RunRDSStatement(GetArchiveItemRequest(itemId));

        return statementResult.HttpStatusCode == HttpStatusCode.OK &&
               statementResult.NumberOfRecordsUpdated == 1;
    }

}