// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace EC2Actions;

// snippet-start:[EC2.dotnetv3.HelloEc2]
public class HelloEc2
{
    /// <summary>
    /// HelloEc2 lists the existing security groups for the default users.
    /// </summary>
    /// <param name="args">Command line arguments</param>
    /// <returns>A Task object.</returns>
    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Microsoft.Extensions.Hosting.Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonEC2>()
                .AddTransient<EC2Wrapper>()
            )
            .Build();

        // Now the client is available for injection.
        var ec2Client = host.Services.GetRequiredService<IAmazonEC2>();

        var groups = await DescribeSecurityGroupsAsync(ec2Client);

        // Now print the security groups returned by the call to
        // DescribeSecurityGroupsAsync.
        Console.WriteLine("Security Groups:");
        groups.ForEach(group =>
        {
            Console.WriteLine($"Security group: {group.GroupName} ID: {group.GroupId}");
        });

    }

    /// <summary>
    /// Retrieve the list of existing EC2 security groups.
    /// </summary>
    /// <param name="client">The initalized EC2 client object.</param>
    /// <returns>A list of SecurityGroup objects.</returns>
    public static async Task<List<SecurityGroup>> DescribeSecurityGroupsAsync(IAmazonEC2 client)
    {
        var request = new DescribeSecurityGroupsRequest
        {
            MaxResults = 10,
        };


        // Retrieve information about up to 10 Amazon EC2 Security Groups.
        var response = await client.DescribeSecurityGroupsAsync(request);
        return response.SecurityGroups;
    }
}
// snippet-end:[EC2.dotnetv3.HelloEc2]