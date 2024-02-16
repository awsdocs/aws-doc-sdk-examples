// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudFormation;
using CloudFormationActions;

namespace CloudFormationTests;

public class CloudFormationTests
{
    /// <summary>
    /// Run the list resources action. Should return true.
    /// </summary>
    /// <returns></returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestListResources()
    {
        // Arrange.
        HelloCloudFormation._amazonCloudFormation = new AmazonCloudFormationClient();

        // Act.
        var success = await HelloCloudFormation.ListResources();

        // Assert.
        Assert.True(success);
    }
}