// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.AutoScaling;
using Amazon.DynamoDBv2;
using Amazon.EC2;
using Amazon.ElasticLoadBalancingV2;
using Amazon.IdentityManagement;
using Amazon.SimpleSystemsManagement;
using AutoScalerActions;
using ElasticLoadBalancerActions;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using ParameterActions;
using RecommendationService;
using ResilientService;

namespace ResilientServiceTests;

/// <summary>
/// Tests for the Resilient Service example.
/// </summary>
public class ResilientServiceTests
{
    private readonly IConfiguration _configuration;

    private readonly ElasticLoadBalancerWrapper _elasticLoadBalancerWrapper = null!;
    private readonly AutoScalerWrapper _autoScalerWrapper = null!;
    private readonly Recommendations _recommendations = null!;
    private readonly SmParameterWrapper _smParameterWrapper = null!;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public ResilientServiceTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();


        var loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        });
        var _logger = new Logger<AutoScalerWrapper>(loggerFactory);

        _elasticLoadBalancerWrapper = new ElasticLoadBalancerWrapper(
            new AmazonElasticLoadBalancingV2Client(),
            _configuration);
        _autoScalerWrapper = new AutoScalerWrapper(
            new AmazonAutoScalingClient(),
            new AmazonEC2Client(),
            new AmazonSimpleSystemsManagementClient(),
            new AmazonIdentityManagementServiceClient(),
            _configuration, _logger);
        _recommendations = new Recommendations(new AmazonDynamoDBClient(),
            _configuration);
        _smParameterWrapper =
            new SmParameterWrapper(new AmazonSimpleSystemsManagementClient(),
                _configuration);

        ResilientServiceWorkflow._autoScalerWrapper = _autoScalerWrapper;
        ResilientServiceWorkflow._elasticLoadBalancerWrapper =
            _elasticLoadBalancerWrapper;
        ResilientServiceWorkflow._recommendations = _recommendations;
        ResilientServiceWorkflow._smParameterWrapper = _smParameterWrapper;
        ResilientServiceWorkflow._configuration = _configuration;
    }

    /// <summary>
    /// Run the deploy step of the scenario. Should return successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestDeploy()
    {
        // Arrange.
        ResilientServiceWorkflow.ResourcesSetup();

        // Act.
        var success = await ResilientServiceWorkflow.Deploy(false);

        // Assert.
        Assert.True(success);
    }

    /// <summary>
    /// Run the demo step of the scenario. Should return successful.
    /// </summary>
    /// <returns></returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task TestDemo()
    {
        // Arrange.
        ResilientServiceWorkflow.ResourcesSetup();

        // Act.
        var success = await ResilientServiceWorkflow.Demo(false);

        // Assert.
        Assert.True(success);
    }

    /// <summary>
    /// Run the destroy step of the scenario. Should return successful.
    /// </summary>
    /// <returns></returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task TestDestroy()
    {
        // Arrange.
        ResilientServiceWorkflow.ResourcesSetup();

        // Act.
        var success = await ResilientServiceWorkflow.DestroyResources(false);

        // Assert.
        Assert.True(success);
    }
}