// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DynamoDbItemTracker;

/// <summary>
/// A request for the work item report.
/// </summary>
public class ReportRequest
{
    /// <summary>
    /// Recipient email for the report.
    /// </summary>
    public string Email { get; set; } = null!;
}