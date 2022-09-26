// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.RDS;
using Amazon.RDS.Model;
using Microsoft.Extensions.Configuration;
using RDSActions;

namespace RDSTests;

/// <summary>
/// Integration tests for the RDS instance examples.
/// </summary>
public class RDSInstanceScenarioTests
{
    private readonly IConfiguration _configuration;
    private readonly RDSWrapper _wrapper;
    private string _parameterGroupName = "";


    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public RDSInstanceScenarioTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();
        _parameterGroupName = _configuration["parameterGroupName"];
        _wrapper = new RDSWrapper(new AmazonRDSClient());
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
        var versions = await _wrapper.DescribeDBEngineVersions(engineName);

        Assert.NotEmpty(versions);
    }

    /// <summary>
    /// Create a DB parameter group. Should return a new parameter group.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    public async Task CreateDbParameterGroup_ShouldSucceed()
    {
        var groupFamilyName = _configuration["parameterGroupFamily"];
        var parameterGroup = await _wrapper.CreateDBParameterGroup(_parameterGroupName,
            groupFamilyName, "New test parameter group");

        bool isParameterGroupReady = false;
        while (!isParameterGroupReady)
        {
            var parameterGroups = await _wrapper.DescribeDBParameterGroups();
            isParameterGroupReady = parameterGroups.Any(g => g.DBParameterGroupName == _parameterGroupName);
        }

        Assert.NotNull(parameterGroup);
    }

    /// <summary>
    /// Describe DB parameters within a parameter group. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    public async Task DescribeDBParameters_ShouldNotBeEmpty()
    {
        var parameters =
            await _wrapper.DescribeDBParameters(_parameterGroupName);

        Assert.NotEmpty(parameters);
    }

    /// <summary>
    /// Modify DB parameters within a parameter group. Should return the group name.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    public async Task ModifyParameters_ShouldReturnGroupName()
    {
        var modifyParameters = new List<Parameter>();
        var parameters =
            await _wrapper.DescribeDBParameters(_parameterGroupName);
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
            await _wrapper.ModifyDBParameterGroup(_parameterGroupName, modifyParameters);

        Assert.Equal(_parameterGroupName, groupName);
    }

    /// <summary>
    /// Describe user parameters within a group. Should return the modified parameter.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    public async Task DescribeDBParameters_ShouldReturnUserParameters()
    {
        var parameters =
            await _wrapper.DescribeDBParameters(_parameterGroupName, "user");

        var parameterNames = parameters.Select(p => p.ParameterName);

        Assert.Contains(_configuration["modifyParameterName"], parameterNames);
    }

    /// <summary>
    /// Describe orderable instance options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    public async Task DescribeOrderableDBInstanceOptions_ShouldNotBeEmpty()
    {
        var engineName = _configuration["engineName"];
        var engineVersion = _configuration["engineVersion"];
        var instanceOptions =
            await _wrapper.DescribeOrderableDBInstanceOptions(engineName, engineVersion);

        Assert.NotEmpty(instanceOptions);
    }

    /// <summary>
    /// Describe orderable instance options. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    public async Task CreateDBInstance_ShouldReturnInstance()
    {
        var parameterGroupName = _configuration["parameterGroupName"];
        var engineName = _configuration["engineName"];
        var engineVersion = _configuration["engineVersion"];
        var instanceIdentifier = _configuration["instanceIdentifier"];
        var instanceClass = _configuration["instanceClass"];
        var adminUserName = _configuration["adminUserName"];
        var adminPassword = _configuration["adminPassword"];

        bool isInstanceReady = false;

        var newInstance = await _wrapper.CreateDBInstance(
            "ExampleTestInstance",
            instanceIdentifier,
            parameterGroupName,
            engineName,
            engineVersion,
            instanceClass,
            20,
            adminUserName,
            adminPassword
        );
        while (!isInstanceReady)
        {
            var instances = await _wrapper.DescribeDBInstances(instanceIdentifier);
            isInstanceReady = instances.FirstOrDefault()?.DBInstanceStatus == "available";
            newInstance = instances.First();
        }

        Assert.NotNull(newInstance);
    }

    /// <summary>
    /// Create DB snapshot. Should return a snapshot instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    public async Task CreateSnapshot_ShouldNotBeEmpty()
    {
        var instanceIdentifier = _configuration["instanceIdentifier"];

        var snapshot = await _wrapper.CreateDBSnapshot(
            instanceIdentifier, "ExampleSnapshot-" + DateTime.Now.Ticks);

        // Wait for the snapshot to be available
        bool isSnapshotReady = false;

        while (!isSnapshotReady)
        {
            var snapshots = await _wrapper.DescribeDBSnapshots(instanceIdentifier);
            isSnapshotReady = snapshots.FirstOrDefault()?.Status == "available";
            snapshot = snapshots.First();
        }

        Assert.NotNull(snapshot);
    }

    /// <summary>
    /// Delete the DB instance. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    public async Task DeleteInstance_ShouldNotFail()
    {
        var instanceIdentifier = _configuration["instanceIdentifier"];

        await _wrapper.DeleteDBInstance(instanceIdentifier);

        // Wait for the instance to delete.
        bool isInstanceDeleted = false;

        while (!isInstanceDeleted)
        {
            var instance = await _wrapper.DescribeDBInstances();
            isInstanceDeleted = instance.All(i => i.DBInstanceIdentifier != instanceIdentifier);
        }

        Assert.True(isInstanceDeleted);
    }

    /// <summary>
    /// Delete the DB parameter group. Should not fail.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    public async Task DeleteParameterGroup_ShouldNotFail()
    {
        var result = await _wrapper.DeleteDBParameterGroup(_parameterGroupName);

        Assert.True(result);
    }
}