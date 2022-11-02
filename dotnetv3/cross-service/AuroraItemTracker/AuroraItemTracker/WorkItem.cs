// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace AuroraItemTracker;

/// <summary>
/// Work item object.
/// </summary>
public class WorkItem
{
    /// <summary>
    /// Id of the work item.
    /// </summary>
    public string Id { get; set; } = null!;

    /// <summary>
    /// Description of the work item.
    /// </summary>
    public string Description { get; set; } = null!;

    /// <summary>
    /// The guide for the work item.
    /// </summary>
    public string Guide { get; set; } = null!;

    /// <summary>
    /// User name for the work item;
    /// </summary>
    public string Name { get; set; } = null!;

    /// <summary>
    /// The current status of the work item.
    /// </summary>
    public string Status { get; set; } = null!;

    /// <summary>
    /// The archive state of the work item.
    /// </summary>
    public bool Archived { get; set; }

}