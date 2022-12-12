// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[EC2.dotnetv3.CreateVPCforS3Example]
using Amazon.EC2;
using Amazon.EC2.Model;
using Amazon.S3;
using Amazon.S3.Model;
using Tag = Amazon.EC2.Model.Tag;

namespace CreateVPCforS3Example;

/// <summary>
/// Use a Amazon Elastic Compute Cloud (Amazon EC2) client to create a VPC Endpoint
/// for the Amazon Simple Storage Service (Amazon S3) service and use that endpoint to list the objects in the S3 bucket.
/// </summary>
public class CreateVPCforS3
{
    static async Task Main(string[] args)
    {
        try
        {
            var ec2Client = new AmazonEC2Client();

            var endpointResponse = await ec2Client.CreateVpcEndpointAsync(new CreateVpcEndpointRequest
            {
                VpcId = "vpc-1a2b3c4d",
                VpcEndpointType = VpcEndpointType.Interface,
                ServiceName = "com.amazonaws.us-east-1.s3",
                SubnetIds = new List<string>() { "subnet-012345678912345606" },
                SecurityGroupIds = new List<string>() { "sg-012345678912345606" },
                TagSpecifications = new List<TagSpecification>() { new TagSpecification() { ResourceType = ResourceType.VpcEndpoint, Tags = new List<Tag>() { new Tag("service", "S3") } } }
            });

            var newEndpoint = endpointResponse.VpcEndpoint;

            Console.WriteLine($"VPC Endpoint {newEndpoint.VpcEndpointId} was created, waiting for it to be available. This may take a few minutes.");

            State? endpointState = null;

            while (endpointState == null || !(endpointState == State.Available || endpointState == State.Failed))
            {
                var endpointDescription = await ec2Client.DescribeVpcEndpointsAsync(
                    new DescribeVpcEndpointsRequest()
                    {
                        VpcEndpointIds = new List<string>()
                            { endpointResponse.VpcEndpoint.VpcEndpointId }
                    });
                endpointState = endpointDescription.VpcEndpoints.FirstOrDefault()?.State;
                Thread.Sleep(500);
            }

            if (endpointState == State.Failed)
            {
                Console.WriteLine("VPC Endpoint failed.");
                return;
            }

            // For newly created endpoints we may need to wait a few more minutes before using it for the Amazon S3 client.
            Thread.Sleep(300000);

            // Use the endpoint to create a ServiceURL to use with the S3 client.
            var vpceUrl = @$"https://bucket{endpointResponse.VpcEndpoint.DnsEntries[0].DnsName.Trim('*')}/";

            Console.WriteLine($"VPC Endpoint available, creating S3 client with endpoint {vpceUrl}.");

            var s3Client = new AmazonS3Client(new AmazonS3Config() { ServiceURL = vpceUrl });

            var objectsList = await s3Client.ListObjectsAsync(new ListObjectsRequest()
            {
                BucketName = "ExampleBucket1"
            });
            Console.WriteLine("List of objects in bucket:");
            Console.WriteLine(string.Join(" ", objectsList.S3Objects.Select(o => o.Key)));
        }
        catch (Exception e)
        {
            Console.WriteLine("There was a problem listing objects using the VPCE.");
            Console.WriteLine(e);

        }
        Console.ReadLine();
    }
}
// snippet-end:[EC2.dotnetv3.CreateVPCforS3Example]