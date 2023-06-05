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
        parameterGroupName = configuration["parameterGroupName"];
        wrapper = new AuroraWrapper(new AmazonRDSClient());
    }

    /// <summary>
    /// Describe the DB engine versions. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
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
    [Order(2)]
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
    [Order(3)]
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
    [Order(4)]
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
    [Order(5)]
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
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task DescribeOrderableDBInstanceOptions_ShouldNotBeEmpty()
    {
        var engineName = configuration["engineName"];
        var engineVersion = configuration["engineVersion"];
        var clusterOptions =
            await wrapper.DescribeOrderableDBInstanceOptionsPagedAsync(engineName, engineVersion);

        Assert.NotEmpty(clusterOptions);
    }

    /// <summary>
    /// Create the DB cluster. Should return the new cluster.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    [Trait("Category", "Integration")]
    public async Task CreateDBCluster_ShouldReturnCluster()
    {
        var engineName = configuration["engineName"];
        var engineVersion = configuration["engineVersion"];
        var clusterIdentifier = configuration["clusterIdentifier"];
        var adminUserName = configuration["adminUserName"];
        var adminPassword = configuration["adminPassword"];

        bool isClusterReady = false;

        var newCluster = await wrapper.CreateDBClusterWithAdminAsync(
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
            var clusters = await wrapper.DescribeDBClustersPagedAsync(clusterIdentifier);
            isClusterReady = clusters.FirstOrDefault()?.Status == "available";
            newCluster = clusters.First();
            Thread.Sleep(30000);
        }

        Assert.NotNull(newCluster);
    }

    /// <summary>
    /// Describe the DB instances. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    [Trait("Category", "Integration")]
    public async Task DescribeDBInstanceOptionsDBInstancesPaged_ShouldNotBeEmpty()
    {
        var instances =
            await wrapper.DescribeDBInstancesPagedAsync();

        Assert.NotEmpty(instances);
    }

    /// <summary>
    /// Create the DB instance in the cluster. Should return the new instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact(Skip = "Long running test.")]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task CreateDBInstanceInCluster_ShouldReturnCluster()
    {
        var engineName = configuration["engineName"];
        var engineVersion = configuration["engineVersion"];
        var clusterIdentifier = configuration["clusterIdentifier"];
        var instanceIdentifier = configuration["instanceIdentifier"];
        var instanceClass = configuration["instanceClass"];

        bool isInstanceReady = false;

        var newInstance = await wrapper.CreateDBInstanceInClusterAsync(
            clusterIdentifier,
            instanceIdentifier,
            engineName,
            engineVersion,
            instanceClass
        );
        while (!isInstanceReady)
        {
            Thread.Sleep(5000);
            var instances = await wrapper.DescribeDBInstancesPagedAsync(instanceIdentifier);
            isInstanceReady = instances.FirstOrDefault()?.DBInstanceStatus == "available";
            newInstance = instances.FirstOrDefault();
        }

        Assert.NotNull(newInstance);
    }

    /// <summary>
    /// Create a DB snapshot. Should return a snapshot cluster.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    [Trait("Category", "Integration")]
    public async Task CreateClusterSnapshot_ShouldNotBeEmpty()
    {
        var clusterIdentifier = configuration["clusterIdentifier"];

        var snapshot = await wrapper.CreateClusterSnapshotByIdentifierAsync(
            clusterIdentifier, "ExampleSnapshot-" + DateTime.Now.Ticks);

        // Wait for the snapshot to be available.
        bool isSnapshotReady = false;

        while (!isSnapshotReady)
        {
            Thread.Sleep(5000);
            var snapshots = await wrapper.DescribeDBClusterSnapshotsByIdentifierAsync(clusterIdentifier);
            isSnapshotReady = snapshots.FirstOrDefault()?.Status == "available";
            snapshot = snapshots.FirstOrDefault();
        }

        Assert.NotNull(snapshot);
    }

    /// <summary>
    /// Describe the DB engine version options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
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
    /// Delete the DB instance. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact(Skip = "Long running test.")]
    [Order(13)]
    [Trait("Category", "Integration")]
    public async Task DeleteInstance_ShouldNotFail()
    {
        var instanceIdentifier = configuration["instanceIdentifier"];

        await wrapper.DeleteDBInstanceByIdentifierAsync(instanceIdentifier);

        // Wait for the DB instance to delete.
        bool isInstanceDeleted = false;

        while (!isInstanceDeleted)
        {
            Thread.Sleep(30000);
            var instances = await wrapper.DescribeDBInstancesPagedAsync();
            isInstanceDeleted = instances.All(i => i.DBInstanceIdentifier != instanceIdentifier);
        }

        Assert.True(isInstanceDeleted);
    }

    /// <summary>
    /// Delete the DB cluster. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(14)]
    [Trait("Category", "Integration")]
    public async Task DeleteCluster_ShouldNotFail()
    {
        var clusterIdentifier = configuration["clusterIdentifier"];

        await wrapper.DeleteDBClusterByIdentifierAsync(clusterIdentifier);

        // Wait for the DB cluster to delete.
        bool isClusterDeleted = false;

        while (!isClusterDeleted)
        {
            Thread.Sleep(5000);
            var cluster = await wrapper.DescribeDBClustersPagedAsync();
            isClusterDeleted = cluster.All(i => i.DBClusterIdentifier != clusterIdentifier);
        }

        Assert.True(isClusterDeleted);
    }

    /// <summary>
    /// Delete the DB parameter group. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(15)]
    [Trait("Category", "Integration")]
    public async Task DeleteParameterGroup_ShouldNotFail()
    {
        var result = await wrapper.DeleteClusterParameterGroupByNameAsync(parameterGroupName);

        Assert.True(result);
    }
}