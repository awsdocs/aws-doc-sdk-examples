// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.RDS;
using Amazon.RDS.Model;
using AuroraActions;
using Microsoft.Extensions.Configuration;

namespace AuroraTests;

/// <summary>
/// Integration tests for the Amazon RDS DB cluster examples.
/// </summary>
public class AuroraScenarioTests
{
    private readonly IConfiguration configuration;
    private readonly AuroraWrapper wrapper;
    private readonly string parameterGroupName;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public AuroraScenarioTests()
    {
        configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();
        parameterGroupName = configuration["parameterGroupName"]!;
        wrapper = new AuroraWrapper(new AmazonRDSClient());
    }

    /// <summary>
    /// Describe the DB engine versions. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task VerifyDescribeDBEngineVersions_ShouldSucceed()
    {
        var engineName = configuration["engineName"];
        var versions = await wrapper.DescribeDBEngineVersionsForEngineAsync(engineName);

        Assert.NotEmpty(versions);
    }

    /// <summary>
    /// Create a DB parameter group. Should return a new parameter group.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task CreateDBClusterParameterGroup_ShouldSucceed()
    {
        var groupFamilyName = configuration["parameterGroupFamily"];
        var parameterGroup = await wrapper.CreateCustomClusterParameterGroupAsync(
            groupFamilyName,
            parameterGroupName,
            "New test parameter group");

        bool isParameterGroupReady = false;
        while (!isParameterGroupReady)
        {
            var parameterGroups = await wrapper.DescribeCustomDBClusterParameterGroupAsync(parameterGroupName);
            isParameterGroupReady = parameterGroups is not null;
            Thread.Sleep(5000);
        }

        Assert.NotNull(parameterGroup);
    }

    /// <summary>
    /// Describe the DB parameters within a parameter group. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DescribeDBClusterParameters_ShouldNotBeEmpty()
    {
        var parameters =
            await wrapper.DescribeDBClusterParametersInGroupAsync(parameterGroupName);

        Assert.NotEmpty(parameters);
    }

    /// <summary>
    /// Modify the DB parameters within a parameter group. Should return the group name.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task ModifyClusterParameters_ShouldReturnGroupName()
    {
        var modifyParameters = new List<Parameter>();
        var parameters =
            await wrapper.DescribeDBClusterParametersInGroupAsync(parameterGroupName);
        foreach (var parameter in parameters)
        {
            if (parameter.ParameterName == configuration["modifyParameterName"])
            {
                parameter.ParameterValue = configuration["modifyParameterValue"];
                modifyParameters.Add(parameter);
                break;
            }
        }

        var groupName =
            await wrapper.ModifyIntegerParametersInGroupAsync(parameterGroupName, modifyParameters, 1);

        Assert.Equal(parameterGroupName, groupName);
    }

    /// <summary>
    /// Describe the user parameters within a group. Should return the modified parameter.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DescribeDBClusterParameters_ShouldReturnUserParameters()
    {
        var parameters =
            await wrapper.DescribeDBClusterParametersInGroupAsync(parameterGroupName, "user");

        var parameterNames = parameters.Select(p => p.ParameterName);

        Assert.Contains(configuration["modifyParameterName"], parameterNames);
    }

    /// <summary>
    /// Describe the orderable DB instance options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DescribeOrderableDBInstanceOptions_ShouldNotBeEmpty()
    {
        var engineName = configuration["engineName"];
        var engineVersion = configuration["engineVersion"];
        var clusterOptions =
            await wrapper.DescribeOrderableDBInstanceOptionsPagedAsync(engineName, engineVersion);

        Assert.NotNull(clusterOptions);
    }

    /// <summary>
    /// Describe the DB instances. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DescribeDBInstanceOptionsDBInstancesPaged_ShouldNotBeEmpty()
    {
        var instances =
            await wrapper.DescribeDBInstancesPagedAsync();

        Assert.NotNull(instances);
    }

    /// <summary>
    /// Describe the DB engine version options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DescribeDBEngineVersionsForEngine_ShouldNotBeEmpty()
    {
        var engineName = configuration["engineName"];
        var groupFamilyName = configuration["parameterGroupFamily"];
        var engineVersions =
            await wrapper.DescribeDBEngineVersionsForEngineAsync(engineName, groupFamilyName);

        Assert.NotEmpty(engineVersions);
    }

    /// <summary>
    /// Delete the DB parameter group. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task DeleteParameterGroup_ShouldNotFail()
    {
        var result = await wrapper.DeleteClusterParameterGroupByNameAsync(parameterGroupName);

        Assert.True(result);
    }
}