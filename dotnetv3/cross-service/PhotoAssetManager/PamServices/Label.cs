// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.DynamoDBv2.DataModel;

namespace PamServices;

/// <summary>
/// Label object, mapped from the Amazon DynamoDB table.
/// </summary>
[DynamoDBTable("Label")]
public class Label
{
    [DynamoDBProperty("Label")]
    public string LabelID { get; set; } = null!;

    [DynamoDBProperty("images")]
    public List<string> Images { get; set; } = null!;

    [DynamoDBProperty("count")]
    public int Count { get; set; }
}