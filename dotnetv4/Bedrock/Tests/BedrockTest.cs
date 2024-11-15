// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.Bedrock;
using Amazon.Bedrock.Model;

namespace BedrockTests;

/// <summary>
/// Bedrock tests.
/// </summary>
public class BedrockTest
{
    private readonly AmazonBedrockClient bedrockClient;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public BedrockTest()
    {
        bedrockClient = new AmazonBedrockClient(Amazon.RegionEndpoint.USEast1);
    }

    /// <summary>
    /// List foundation models. Should not be null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    [Trait("Category", "Weathertop")]
    public async Task ListFoundationModelsAsync_ShouldNotBeNull()
    {
        var result = await bedrockClient.ListFoundationModelsAsync(new ListFoundationModelsRequest());
        Assert.NotEmpty(result.ModelSummaries);
    }
}