// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace EC2Tests;
/// <summary>
/// Integration tests for the Amazon Elastic Compute Cloud (Amazon EC2)
/// Basics scenario.
/// </summary>
public class EC2WrapperTests
{
    private readonly IConfiguration _configuration;
    private readonly AmazonEC2Client _client;
    private readonly EC2Wrapper _ec2Wrapper;
    private readonly SsmWrapper _ssmWrapper;

    private readonly string _ciderBlock;
    private readonly string _groupName;
    private readonly string _groupDescription;
    private readonly string _keyPairName;

    private static string? _allocationId;
    private static ArchitectureValues? _architecture;
    private static string? _associationId;
    private static string? _ec2InstanceId;
    private static List<Image>? _images;
    private static string? _imageId;
    private static List<InstanceTypeInfo>? _instanceTypes;
    private static string? _instanceType;
    private static KeyPair? _keyPair;
    private static string? _secGroupId;
    private static string? _tempFileName;

    /// <summary>
    /// The test class constructor.
    /// </summary>
    public EC2WrapperTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _ciderBlock = _configuration["CidrBlock"];
        _groupName = _configuration["GroupName"];
        _groupDescription = _configuration["GroupDescription"];
        _instanceType = _configuration["InstanceType"];
        _keyPairName = _configuration["KeyPairName"];

        _client = new AmazonEC2Client();

        _ec2Wrapper = new EC2Wrapper(_client);
        _ssmWrapper = new SsmWrapper(new AmazonSimpleSystemsManagementClient());

    }

    /// <summary>
    /// Test the allocation of an Elastic IP address.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(11)]
    public async Task AllocateAddressTest()
    {
        _allocationId = await _ec2Wrapper.AllocateAddress();
        Assert.NotNull(_allocationId);
    }

    /// <summary>
    /// Test the association of an elastic IP address to an instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(12)]
    public async Task AssociateAddressTest()
    {
        _associationId = await _ec2Wrapper.AssociateAddress(_allocationId, _ec2InstanceId);
        Assert.NotNull(_associationId);
    }

    /// <summary>
    /// Test the authorization of the local computer for ingress to an
    /// EC2 instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(5)]
    public async Task AuthorizeSecurityGroupIngressTest()
    {
        var success = await _ec2Wrapper.AuthorizeSecurityGroupIngress(_groupName);
        Assert.True(success, "Could not authorize the group for ingress.");
    }

    /// <summary>
    /// Test the creation of an Amazon EC2 key pair.
    /// </summary>
    /// <returns></returns>
    [Fact()]
    [Order(2)]
    public async Task CreateKeyPairTest()
    {
        _keyPair = await _ec2Wrapper.CreateKeyPair(_keyPairName);
        Assert.NotNull(_keyPair);
    }

    /// <summary>
    /// Test the creation of an Amazon EC2 security group.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(4)]
    public async Task CreateSecurityGroupTest()
    {
        _secGroupId = await _ec2Wrapper.CreateSecurityGroup(_groupName, _groupDescription);
        Assert.NotNull(_secGroupId);
    }

    /// <summary>
    /// Test the deletion of an Amazon EC2 key pair.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(19)]
    public async Task DeleteKeyPairTest()
    {
        var success = await _ec2Wrapper.DeleteKeyPair(_keyPair.KeyName);
        Assert.True(success, "Could not delete the key pair.");
    }

    /// <summary>
    /// Test the deletion of an Amazon EC2 security group.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(18)]
    public async Task DeleteSecurityGroupTest()
    {
        var success = await _ec2Wrapper.DeleteSecurityGroup(_secGroupId);
        Assert.True(success, "Couldn't delete the security group.");
    }

    /// <summary>
    /// Test the deletion of the temporary PEM file.
    /// </summary>
    [Fact()]
    [Order(20)]
    public void DeleteTempFileTest()
    {
        try
        {
            _ec2Wrapper.DeleteTempFile(_tempFileName);
        }
        catch (Exception ex)
        {
            Assert.True(false, $"Could not delete the temporary file. Error: {ex.Message}");
        }
    }

    /// <summary>
    /// Test the method which retrieves a list of images.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(7)]
    public async Task DescribeImagesTest()
    {
        // Get list of available Amazon Linux 2 Amazon Machine Images (AMIs).
        var parameters = await _ssmWrapper.GetParametersByPath("/aws/service/ami-amazon-linux-latest");

        List<string> imageIds = parameters.Select(param => param.Value).ToList();

        _images = await _ec2Wrapper.DescribeImages(imageIds);
        _imageId = _images[0].ImageId;
        _architecture = _images[0].Architecture;
        Assert.NotEmpty(_images);
    }

    /// <summary>
    /// Test the retrieval of information about an EC2 instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(5)]
    public async Task DescribeInstanceTest()
    {
        var instance = await _ec2Wrapper.DescribeInstance(_ec2InstanceId);
        Assert.NotNull(instance);

    }

    /// <summary>
    /// Test the retrieval of information about multiple EC2 instances.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(9)]
    public async Task DescribeInstancesTest()
    {
        try
        {
            await _ec2Wrapper.DescribeInstances();
        }
        catch (Exception ex)
        {
            Assert.True(false, $"Describe instances failed. Error: {ex.Message}");
        }
    }

    /// <summary>
    /// Test the ability to retrieve information about available instance
    /// types.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(8)]
    public async Task DescribeInstanceTypesTest()
    {
        _instanceTypes = await _ec2Wrapper.DescribeInstanceTypes(_architecture);
        Assert.NotEmpty(_instanceTypes);
    }

    /// <summary>
    /// Test the ability to retrieve information about existing Amazon
    /// EC2 key pairs.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(3)]
    public async Task DescribeKeyPairsTest()
    {
        var keyPairs = await _ec2Wrapper.DescribeKeyPairs(_keyPairName);
        Assert.NotEmpty(keyPairs);
    }

    /// <summary>
    /// Test the ability to retrieve a list of existing Amazon EC2 security
    /// groups.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(6)]
    public async Task DescribeSecurityGroupsTest()
    {
        var secGroups = await _ec2Wrapper.DescribeSecurityGroups(_secGroupId);
        Assert.NotEmpty(secGroups);
    }

    /// <summary>
    /// Test the ability to disassociate an IP address from an Amazon EC2
    /// Elastic IP address.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(15)]
    public async Task DisassociateIpTest()
    {
        var success = await _ec2Wrapper.DisassociateIp(_associationId);
        Assert.True(success, "Could not disassociate IP address.");
    }

    /// <summary>
    /// Test the ability to retrieve a list of Amazon EC2 AMIs.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(24)]
    public async Task GetEc2AmiListTest()
    {
        _images = await _ec2Wrapper.GetEC2AmiList();
        Assert.NotEmpty(_images);
    }

    /// <summary>
    /// Test the ability to release an Amazon EC2 Elastic IP address.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(16)]
    public async Task ReleaseAddressTest()
    {
        var success = await _ec2Wrapper.ReleaseAddress(_allocationId);
        Assert.True(success, "Could not releases the address.");
    }

    /// <summary>
    /// Test the ability to run EC2 instances.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(10)]
    public async Task RunInstancesTest()
    {
        _ec2InstanceId = await _ec2Wrapper.RunInstances(_imageId, _instanceType, _keyPairName, _secGroupId);

        // Wait for the instance state to be running.
        var isRunning = false;
        do
        {
            isRunning = await _ec2Wrapper.WaitForInstanceState(_ec2InstanceId, InstanceStateName.Running);
        } while (!isRunning);

        Assert.NotNull(_ec2InstanceId);
    }

    /// <summary>
    /// Test the ability to save the values of an Amazon EC2 key pair to a
    /// temporary file.
    /// </summary>
    [Fact()]
    [Order(2)]
    public void SaveKeyPairTest()
    {
        _tempFileName = _ec2Wrapper.SaveKeyPair(_keyPair);
        Assert.NotNull(_tempFileName);

    }

    /// <summary>
    /// Test the ability to start an existing EC2 instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(14)]
    public async Task StartInstancesTest()
    {
        try
        {
            await _ec2Wrapper.StartInstances(_ec2InstanceId);

            // Wait for the instance state to be running.
            var isRunning = false;
            do
            {
                isRunning = await _ec2Wrapper.WaitForInstanceState(_ec2InstanceId, InstanceStateName.Running);
            } while (!isRunning);

            Assert.True(isRunning);
        }
        catch (Exception ex)
        {
            Assert.True(false, $"Could not start {_ec2InstanceId}. Error: {ex.Message}");
        }
    }

    /// <summary>
    /// Test the ability to stop a running EC2 instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(13)]
    public async Task StopInstancesTest()
    {
        try
        {
            await _ec2Wrapper.StopInstances(_ec2InstanceId);
            var hasStopped = false;
            do
            {
                hasStopped = await _ec2Wrapper.WaitForInstanceState(_ec2InstanceId, InstanceStateName.Stopped);
            } while (!hasStopped);

            Assert.True(hasStopped);
        }
        catch (Exception ex)
        {
            Assert.True(false, $"Could not stop the instance. Error: {ex.Message}");
        }
    }

    /// <summary>
    /// Test the ability to terminate an existing EC2 instance.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact()]
    [Order(17)]
    public async Task TerminateInstanceTest()
    {
        var stateChange = await _ec2Wrapper.TerminateInstances(_ec2InstanceId);

        // Wait for the instance state to be terminated.
        var hasTerminated = false;
        do
        {
            hasTerminated = await _ec2Wrapper.WaitForInstanceState(_ec2InstanceId, InstanceStateName.Terminated);
        } while (!hasTerminated);

        Assert.True(hasTerminated);
    }
}
