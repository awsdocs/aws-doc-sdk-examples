// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.DynamoDBv2.DataModel;

namespace RecommendationService;

/// <summary>
/// Data model for the recommendation service.
/// </summary>
[DynamoDBTable("reslience-sdk-example-database")]
public class RecommendationModel
{
    [DynamoDBRangeKey]
    public int ItemId { get; set; }

    [DynamoDBHashKey]
    public string MediaType { get; set; } = null!;

    public string Title { get; set; } = null!;

    public string Creator { get; set; } = null!;
}