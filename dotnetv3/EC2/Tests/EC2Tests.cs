// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace EC2Tests;
public class EC2Tests
{
    private readonly IConfiguration _configuration;
    private readonly AmazonEC2Client _client;
    private readonly EC2Wrapper _ec2Wrapper;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public EC2Tests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _client = new AmazonEC2Client();

        _ec2Wrapper = new EC2Wrapper(_client);
    }

    [Fact()]
    [Order(1)]
    public async Task DescribeSecurityGroupsAsyncTest_ShouldPass()
    {

        var groups = await HelloEc2.DescribeSecurityGroupsAsync(_client);

        // There should be at least one security group in existence but
        // not more than 10 since we've limited returned values to 10.
        var groupCount = groups.Count();
        Assert.True(groupCount is > 0 and <= 10, "Wrong number of security groups returned.");
    }
}
