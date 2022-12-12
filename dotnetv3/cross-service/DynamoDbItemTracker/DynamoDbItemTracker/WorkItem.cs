// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;
using CsvHelper.Configuration.Attributes;
using System.Text.Json.Serialization;

namespace DynamoDbItemTracker;

/// <summary>
/// Work item object.
/// </summary>
[DynamoDBTable("doc-example-work-item-tracker")]
public class WorkItem
{
    /// <summary>
    /// The iditem key of the work item.
    /// This property must match the hash key for the table.
    /// </summary>
    [JsonIgnore]
    [DynamoDBHashKey]
    [Ignore]
    public string iditem { get; set; } = null!;

    /// <summary>
    /// Id of the work item, maps directly to iditem but with a more standard name.
    /// </summary>
    public string Id
    {
        get => iditem;
        set => iditem = value;
    }

    /// <summary>
    /// Description of the work item.
    /// </summary>
    [DynamoDBProperty("description")]
    public string Description { get; set; } = null!;

    /// <summary>
    /// The guide for the work item.
    /// </summary>
    [DynamoDBProperty("guide")]
    public string Guide { get; set; } = null!;

    /// <summary>
    /// User name for the work item.
    /// </summary>
    [DynamoDBProperty("name")]
    public string Name { get; set; } = null!;

    /// <summary>
    /// The current status of the work item.
    /// </summary>
    [DynamoDBProperty("status")]
    public string Status { get; set; } = null!;

    /// <summary>
    /// The archive state of the work item.
    /// </summary>
    [DynamoDBProperty("archived")]
    public bool Archived { get; set; }

}