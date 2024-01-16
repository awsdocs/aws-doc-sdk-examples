// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon;
using Amazon.IdentityManagement;
using Amazon.Lambda;
using Amazon.S3;
using Amazon.SageMaker;
using Amazon.SQS;
using Microsoft.Extensions.Configuration;
using SageMakerActions;
using SageMakerScenario;

namespace SageMakerTests;

/// <summary>
/// Tests for the SageMakerWrapper class.
/// </summary>
public class SageMakerTests
{
    private readonly IConfiguration _configuration;
    private readonly SageMakerWrapper _sageMakerWrapper;
    private static string pipelineName = null!;
    private static string bucketName = null!;
    private static string queueName = null!;
    private static string lambdaRoleArn = null!;
    private static string sageMakerRoleArn = null!;
    private static string functionArn = null!;
    private static string queueUrl = null!;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public SageMakerTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        queueName = _configuration["queueName"];
        bucketName = _configuration["bucketName"];
        pipelineName = _configuration["pipelineName"];

        _sageMakerWrapper = new SageMakerWrapper(
            new AmazonSageMakerClient(RegionEndpoint.USWest2));

        PipelineWorkflow._sageMakerWrapper = _sageMakerWrapper;
        PipelineWorkflow._iamClient =
            new AmazonIdentityManagementServiceClient(RegionEndpoint.USWest2);
        PipelineWorkflow._sqsClient =
            new AmazonSQSClient(RegionEndpoint.USWest2);
        PipelineWorkflow._s3Client =
            new AmazonS3Client(RegionEndpoint.USWest2);
        PipelineWorkflow._lambdaClient =
            new AmazonLambdaClient(RegionEndpoint.USWest2);

        PipelineWorkflow.lambdaFunctionName = "SageMakerExampleFunctionTest";
        PipelineWorkflow.sageMakerRoleName = "SageMakerExampleRoleTest";
        PipelineWorkflow.lambdaRoleName = "SageMakerExampleLambdaRoleTest";
    }

    /// <summary>
    /// Set up a new pipeline. The returned Amazon Resource Name (ARN) should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task AddPipeline_ShouldReturnNonEmptyArn()
    {
        // Arrange.
        lambdaRoleArn = await PipelineWorkflow.CreateLambdaRole();
        sageMakerRoleArn = await PipelineWorkflow.CreateSageMakerRole();
        functionArn = await PipelineWorkflow.SetupLambda(lambdaRoleArn, false);
        queueUrl = await PipelineWorkflow.SetupQueue(queueName);
        await PipelineWorkflow.SetupBucket(bucketName);

        // Act.
        var arn = await PipelineWorkflow.SetupPipeline(sageMakerRoleArn, functionArn, pipelineName);

        // Assert.
        Assert.False(string.IsNullOrEmpty(arn));
    }

    /// <summary>
    /// Run the pipeline. The returned ARN should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task ExecutePipeline_ShouldReturnNonEmptyArn()
    {
        // Act.
        var arn = await PipelineWorkflow.ExecutePipeline(queueUrl, sageMakerRoleArn,
            pipelineName, bucketName);
        await PipelineWorkflow.WaitForPipelineExecution(arn);
        // Assert.
        Assert.False(string.IsNullOrEmpty(arn));
    }

    /// <summary>
    /// Get some pipeline output. The returned object key should not be empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    [Trait("Category", "Integration")]
    public async Task GetPipelineOutput_ShouldReturnKey()
    {
        // Act.
        var keyName = await PipelineWorkflow.GetOutputResults(bucketName);

        // Assert.
        Assert.False(string.IsNullOrEmpty(keyName));
    }

    /// <summary>
    /// Clean up resources. Should return true if successful.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    [Trait("Category", "Integration")]
    public async Task CleanupResources_ShouldReturnTrue()
    {
        // Act.
        var success = await PipelineWorkflow.CleanupResources(false, queueUrl, pipelineName, bucketName);

        // Assert.
        Assert.True(success);
    }
}