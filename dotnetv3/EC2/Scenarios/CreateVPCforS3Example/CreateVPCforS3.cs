﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


using Amazon.EC2;
using Amazon.EC2.Model;
using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.Extensions.Configuration;
using Tag = Amazon.EC2.Model.Tag;

namespace CreateVPCforS3Example;

/// <summary>
/// Use an Amazon Elastic Compute Cloud (Amazon EC2) client to create a VPC endpoint
/// for the Amazon Simple Storage Service (Amazon S3) service. Then use
/// that endpoint to list the objects in the S3 bucket.
/// </summary>
public class CreateVPCforS3
{
    static async Task Main(string[] args)
    {
        var ec2Client = new AmazonEC2Client();

        var configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("appsettings.json") // Load app settings from .json file.
            .AddJsonFile("appsettings.local.json",
                true) // Optionally, load local settings.
            .Build();

        var s3EndpointUrl = await CreateVPCforS3Client(configuration, ec2Client);

        if (s3EndpointUrl != null)
        {
            var objectsList = await CreateS3ClientWithEndpoint(configuration, s3EndpointUrl);
            Console.WriteLine("List of objects in bucket:");
            Console.WriteLine(string.Join(" ", objectsList.Select(o => o.Key)));
        }
    }


    // snippet-start:[EC2.dotnetv3.CreateVPCforS3]

    /// <summary>
    /// Create a VPC Endpoint and use it for an Amazon S3 client.
    /// </summary>
    /// <param name="configuration">Configuration to specify resource IDs.</param>
    /// <param name="ec2Client">Initialized Amazon EC2 client.</param>
    /// <returns>The Amazon S3 URL using the endpoint.</returns>
    public static async Task<string?> CreateVPCforS3Client(IConfiguration configuration,
        AmazonEC2Client ec2Client)
    {
        var endpointResponse = await ec2Client.CreateVpcEndpointAsync(
            new CreateVpcEndpointRequest
            {
                VpcId = configuration["VpcId"],
                VpcEndpointType = VpcEndpointType.Interface,
                ServiceName = "com.amazonaws.us-east-1.s3",
                SubnetIds = new List<string> { configuration["SubnetId"]! },
                SecurityGroupIds = new List<string> { configuration["SecurityGroupId"]! },
                TagSpecifications = new List<TagSpecification>
                {
                    new TagSpecification
                    {
                        ResourceType = ResourceType.VpcEndpoint,
                        Tags = new List<Tag>
                        {
                            new Tag("service", "S3")
                        }
                    }
                }
            });

        var newEndpoint = endpointResponse.VpcEndpoint;

        Console.WriteLine(
            $"VPC endpoint {newEndpoint.VpcEndpointId} was created, waiting for it to be available. " +
            $"This might take a few minutes.");

        State? endpointState = null;

        while (endpointState == null ||
               !(endpointState == State.Available || endpointState == State.Failed))
        {
            var endpointDescription = await ec2Client.DescribeVpcEndpointsAsync(
                new DescribeVpcEndpointsRequest
                {
                    VpcEndpointIds = new List<string>
                        { endpointResponse.VpcEndpoint.VpcEndpointId }
                });
            endpointState = endpointDescription.VpcEndpoints.FirstOrDefault()?.State;
            Thread.Sleep(500);
        }

        if (endpointState == State.Failed)
        {
            Console.WriteLine("VPC endpoint failed.");
            return null;
        }

        // For newly created endpoints, we may need to wait a few
        // more minutes before using it for the Amazon S3 client.
        Thread.Sleep(int.Parse(configuration["WaitTimeMilliseconds"]!));

        // Return the endpoint to create a ServiceURL to use with the Amazon S3 client.
        var vpceUrl =
            @$"https://bucket{endpointResponse.VpcEndpoint.DnsEntries[0].DnsName.Trim('*')}/";

        return vpceUrl;
    }

    /// <summary>
    /// Create an Amazon S3 client with a VPC URL.
    /// To successfully list the objects in the S3 bucket when you're using a private VPC endpoint,
    /// you must run this code in an environment that has access to the VPC.
    /// </summary>
    /// <param name="configuration">Configuration to specify resource IDs.</param>
    /// <param name="vpceUrl">The endpoint URL.</param>
    /// <returns>Async task.</returns>
    public static async Task<List<S3Object>> CreateS3ClientWithEndpoint(IConfiguration configuration, string vpceUrl)
    {
        Console.WriteLine(
            $"Creating Amazon S3 client with endpoint {vpceUrl}.");

        var s3Client = new AmazonS3Client(new AmazonS3Config { ServiceURL = vpceUrl });

        var objectsList = await s3Client.ListObjectsAsync(new ListObjectsRequest
        {
            BucketName = configuration["BucketName"]
        });

        return objectsList.S3Objects;
    }
    // snippet-end:[EC2.dotnetv3.CreateVPCforS3]
}