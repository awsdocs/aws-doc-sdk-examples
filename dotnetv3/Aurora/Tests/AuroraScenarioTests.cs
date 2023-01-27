// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.Configuration;
using Amazon.RDS;
using Amazon.RDS.Model;
using AuroraActions;

namespace AuroraTests;

/// <summary>
/// Integration tests for the Amazon RDS DB cluster examples.
/// </summary>
public class AuroraScenarioTests
{
    private readonly IConfiguration _configuration;
    private readonly AuroraWrapper _wrapper;
    private string _parameterGroupName = "";


    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public AuroraScenarioTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();
        _parameterGroupName = _configuration["parameterGroupName"];
        _wrapper = new AuroraWrapper(new AmazonRDSClient());
    }

    /// <summary>
    /// Describe the DB engine versions. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    public async Task VerifyDescribeDBEngineVersions_ShouldSucceed()
    {
        var engineName = _configuration["engineName"];
        var versions = await _wrapper.DescribeDBEngineVersionsForEngineAsync(engineName);

        Assert.NotEmpty(versions);
    }

    /// <summary>
    /// Create a DB parameter group. Should return a new parameter group.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    public async Task CreateDBClusterParameterGroup_ShouldSucceed()
    {
        var groupFamilyName = _configuration["parameterGroupFamily"];
        var parameterGroup = await _wrapper.CreateCustomDBClusterParameterGroupAsync(_parameterGroupName,
            groupFamilyName, "New test parameter group");

        bool isParameterGroupReady = false;
        while (!isParameterGroupReady)
        {
            var parameterGroups = await _wrapper.DescribeCustomDBClusterParameterGroupAsync(_parameterGroupName);
            isParameterGroupReady = parameterGroups is not null;
            Thread.Sleep(30000);
        }

        Assert.NotNull(parameterGroup);
    }

    /// <summary>
    /// Describe the DB parameters within a parameter group. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    public async Task DescribeDBClusterParameters_ShouldNotBeEmpty()
    {
        var parameters =
            await _wrapper.DescribeDBClusterParametersInGroupAsync(_parameterGroupName);

        Assert.NotEmpty(parameters);
    }

    /// <summary>
    /// Modify the DB parameters within a parameter group. Should return the group name.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    public async Task ModifyClusterParameters_ShouldReturnGroupName()
    {
        var modifyParameters = new List<Parameter>();
        var parameters =
            await _wrapper.DescribeDBClusterParametersInGroupAsync(_parameterGroupName);
        foreach (var parameter in parameters)
        {
            if (parameter.ParameterName == _configuration["modifyParameterName"])
            {
                parameter.ParameterValue = _configuration["modifyParameterValue"];
                modifyParameters.Add(parameter);
                break;
            }
        }

        var groupName =
            await _wrapper.ModifyIntegerParametersInGroupAsync(_parameterGroupName, modifyParameters);

        Assert.Equal(_parameterGroupName, groupName);
    }

    /// <summary>
    /// Describe the user parameters within a group. Should return the modified parameter.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    public async Task DescribeDBClusterParameters_ShouldReturnUserParameters()
    {
        var parameters =
            await _wrapper.DescribeDBClusterParametersInGroupAsync(_parameterGroupName, "user");

        var parameterNames = parameters.Select(p => p.ParameterName);

        Assert.Contains(_configuration["modifyParameterName"], parameterNames);
    }

    /// <summary>
    /// Describe the orderable DB cluster options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    public async Task DescribeOrderableDBClusterOptions_ShouldNotBeEmpty()
    {
        var engineName = _configuration["engineName"];
        var engineVersion = _configuration["engineVersion"];
        var clusterOptions =
            await _wrapper.DescribeOrderableDBInstanceOptionsPagedAsync(engineName, engineVersion);

        Assert.NotEmpty(clusterOptions);
    }

    /// <summary>
    /// Create the DB cluster. Should return the new cluster.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    public async Task CreateDBCluster_ShouldReturnCluster()
    {
        var parameterGroupName = _configuration["parameterGroupName"];
        var engineName = _configuration["engineName"];
        var engineVersion = _configuration["engineVersion"];
        var clusterIdentifier = _configuration["clusterIdentifier"];
        var adminUserName = _configuration["adminUserName"];
        var adminPassword = _configuration["adminPassword"];

        bool isClusterReady = false;

        var newCluster = await _wrapper.CreateDBClusterWithAdminAsync(
            "ExampleTestCluster",
            clusterIdentifier,
            parameterGroupName,
            engineName,
            engineVersion,
            adminUserName,
            adminPassword
        );
        while (!isClusterReady)
        {
            var clusters = await _wrapper.DescribeDBClustersPagedAsync(clusterIdentifier);
            isClusterReady = clusters.FirstOrDefault()?.Status == "available";
            newCluster = clusters.First();
            Thread.Sleep(30000);
        }

        Assert.NotNull(newCluster);
    }

    /// <summary>
    /// Create a DB snapshot. Should return a snapshot cluster.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    public async Task CreateClusterSnapshot_ShouldNotBeEmpty()
    {
        var clusterIdentifier = _configuration["clusterIdentifier"];

        var snapshot = await _wrapper.CreateDBClusterSnapshotByIdentifierAsync(
            clusterIdentifier, "ExampleSnapshot-" + DateTime.Now.Ticks);

        // Wait for the snapshot to be available.
        bool isSnapshotReady = false;

        while (!isSnapshotReady)
        {
            var snapshots = await _wrapper.DescribeDBClusterSnapshotsByIdentifierAsync(clusterIdentifier);
            isSnapshotReady = snapshots.FirstOrDefault()?.Status == "available";
            snapshot = snapshots.First();
            Thread.Sleep(30000);
        }

        Assert.NotNull(snapshot);
    }

    /// <summary>
    /// Delete the DB cluster. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    public async Task DeleteCluster_ShouldNotFail()
    {
        var clusterIdentifier = _configuration["clusterIdentifier"];

        await _wrapper.DeleteDBClusterByIdentifierAsync(clusterIdentifier);

        // Wait for the DB cluster to delete.
        bool isClusterDeleted = false;

        while (!isClusterDeleted)
        {
            var cluster = await _wrapper.DescribeDBClustersPagedAsync();
            isClusterDeleted = cluster.All(i => i.DBClusterIdentifier != clusterIdentifier);
            Thread.Sleep(30000);
        }

        Assert.True(isClusterDeleted);
    }

    /// <summary>
    /// Delete the DB parameter group. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    public async Task DeleteParameterGroup_ShouldNotFail()
    {
        var result = await _wrapper.DeleteDBClusterParameterGroupByNameAsync(_parameterGroupName);

        Assert.True(result);
    }
}