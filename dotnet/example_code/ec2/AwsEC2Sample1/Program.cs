//snippet-sourcedescription:[AwsEC2Sample2 example demonstrates how to use various operations, like create instance and create key pairs, in the EC2 client.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
﻿/*******************************************************************************
* Copyright 2009-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

using System;
using System.Collections.Generic;
using System.Threading;

using Amazon;
using Amazon.EC2;
using Amazon.EC2.Model;
using Amazon.EC2.Util;

using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

using Amazon.Auth.AccessControlPolicy;
using Amazon.Auth.AccessControlPolicy.ActionIdentifiers;

using System.Net;
namespace AwsEC2Sample1
{

    class Program
    {
        public static void Main(string[] args)
        {
            // This project contains the following examples:
            //CreateInstance();
            //CreateInstanceProfile();
            //CreateKeyPair();
            //DeleteKeyPair(new AmazonEC2Client(), CreateKeyPair());
            //DescribeAvailabilityZones();
            //DescribeRegions();
            //CreateVPCEndpoint();
            //DescribeVPCEndPoints();
            //ModifyVPCEndPoint();
            //DeleteVPCEndPoint();
            //DescribeElasticIps();
            //AllocateAndAssociate("instanceId");
            //Release("allocationId");
        }
        static readonly string RESOURCDE_POSTFIX = DateTime.Now.Ticks.ToString();
        public static void CreateInstance()
        {

            var bucketName = "ec2-sample-" + RESOURCDE_POSTFIX;

            var ec2Client = new AmazonEC2Client();

            // Get latest 2012 Base AMI
            var imageId = ImageUtilities.FindImage(ec2Client, ImageUtilities.WINDOWS_2012_BASE).ImageId;
            Console.WriteLine("Using Image ID: {0}", imageId);

            var runRequest = new RunInstancesRequest
            {
                ImageId = imageId,
                MinCount = 1,
                MaxCount = 1,
                InstanceType = new InstanceType("t1.micro")

            };
            var instanceId = ec2Client.RunInstances(runRequest).Reservation.Instances[0].InstanceId;

            ec2Client.CreateTags(new CreateTagsRequest
            {
                Resources = new List<string> { instanceId },
                Tags = new List<Amazon.EC2.Model.Tag> { new Amazon.EC2.Model.Tag { Key = "Name", Value = "Processor" } }
            });
            Console.WriteLine("Adding Name Tag to instance");
            // Pause to be sure instance has started
            Thread.Sleep(45000);
            ec2Client.StopInstances(new StopInstancesRequest { InstanceIds = new List<string> { instanceId } });

        }
        static string CreateInstanceProfile()
        {
            var roleName = "ec2-sample-" + RESOURCDE_POSTFIX;
            var client = new AmazonIdentityManagementServiceClient();
            client.CreateRole(new CreateRoleRequest
            {
                RoleName = roleName,
                AssumeRolePolicyDocument = @"{""Statement"":[{""Principal"":{""Service"":[""ec2.amazonaws.com""]},""Effect"":""Allow"",""Action"":[""sts:AssumeRole""]}]}"
            });

            var statement = new Amazon.Auth.AccessControlPolicy.Statement(Amazon.Auth.AccessControlPolicy.Statement.StatementEffect.Allow);
            statement.Actions.Add(S3ActionIdentifiers.AllS3Actions);
            statement.Resources.Add(new Resource("*"));

            var policy = new Policy();
            policy.Statements.Add(statement);

            client.PutRolePolicy(new PutRolePolicyRequest
            {
                RoleName = roleName,
                PolicyName = "S3Access",
                PolicyDocument = policy.ToJson()
            });

            var response = client.CreateInstanceProfile(new CreateInstanceProfileRequest
            {
                InstanceProfileName = roleName
            });

            client.AddRoleToInstanceProfile(new AddRoleToInstanceProfileRequest
            {
                InstanceProfileName = roleName,
                RoleName = roleName
            });

            return response.InstanceProfile.Arn;
        }
        public static KeyPair CreateKeyPair()
        {
            var ec2Client = new AmazonEC2Client();
            CreateKeyPairRequest request = new CreateKeyPairRequest("MyNewKeyPair");
            return ec2Client.CreateKeyPair(request).KeyPair;
        }
        public static void DeleteKeyPair(AmazonEC2Client ec2Client, KeyPair keyPair)
        {
            try
            {
                // Delete key pair created for sample.
                ec2Client.DeleteKeyPair(new DeleteKeyPairRequest { KeyName = keyPair.KeyName });
            }
            catch (AmazonEC2Exception ex)
            {
                // Check the ErrorCode to see if the key already exists.
                if ("InvalidKeyPair.NotFound" == ex.ErrorCode)
                {
                    Console.WriteLine("The key pair \"{0}\" was not found.", keyPair.KeyName);
                }
                else
                {
                    // The exception was thrown for another reason, so re-throw the exception.
                    throw;
                }
            }
        }
        public static void DescribeAvailabilityZones()
        {
            Console.WriteLine("Describe Availability Zones");
            AmazonEC2Client client = new AmazonEC2Client();
            DescribeAvailabilityZonesResponse response = client.DescribeAvailabilityZones();
            var availZones = new List<AvailabilityZone>();
            availZones = response.AvailabilityZones;
            foreach (AvailabilityZone az in availZones)
            {
                Console.WriteLine(az.ZoneName);
            }
        }
        public static void DescribeRegions()
        {
            Console.WriteLine("Describe Regions");
            AmazonEC2Client client = new AmazonEC2Client();
            DescribeRegionsResponse response = client.DescribeRegions();
            var regions = new List<Region>();
            regions = response.Regions;
            foreach (Region region in regions)
            {
                Console.WriteLine(region.RegionName);
            }
        }
        public static void CreateVPCEndpoint()
        {
            AmazonEC2Client client = new AmazonEC2Client();
            CreateVpcRequest vpcRequest = new CreateVpcRequest("10.32.0.0/16");
            CreateVpcResponse vpcResponse = client.CreateVpc(vpcRequest);
            Vpc vpc = vpcResponse.Vpc;
            CreateVpcEndpointRequest endpointRequest = new CreateVpcEndpointRequest();
            endpointRequest.VpcId = vpc.VpcId;
            endpointRequest.ServiceName = "com.amazonaws.us-west-2.s3";
            CreateVpcEndpointResponse cVpcErsp = client.CreateVpcEndpoint(endpointRequest);
            VpcEndpoint vpcEndPoint = cVpcErsp.VpcEndpoint;
        }
        public static void DescribeVPCEndPoints()
        {
            AmazonEC2Client client = new AmazonEC2Client();
            DescribeVpcEndpointsRequest endpointRequest = new DescribeVpcEndpointsRequest();
            endpointRequest.MaxResults = 5;
            DescribeVpcEndpointsResponse endpointResponse = client.DescribeVpcEndpoints(endpointRequest);
            List<VpcEndpoint> endpointList = endpointResponse.VpcEndpoints;
            foreach (VpcEndpoint vpc in endpointList)
            {
                Console.WriteLine("VpcEndpoint ID = " + vpc.VpcEndpointId);
                List<string> routeTableIds = vpc.RouteTableIds;
                foreach (string id in routeTableIds)
                {
                    Console.WriteLine("\tRoute Table ID = " + id);
                }

            }
        }
        public static void ModifyVPCEndPoint()
        {
            AmazonEC2Client client = new AmazonEC2Client();
            ModifyVpcEndpointRequest modifyRequest = new ModifyVpcEndpointRequest();
            modifyRequest.VpcEndpointId = "vpce-17b05a7e";
            modifyRequest.AddRouteTableIds = new List<string> { "rtb-c46f15a3" };
            ModifyVpcEndpointResponse modifyResponse = client.ModifyVpcEndpoint(modifyRequest);
            HttpStatusCode status = modifyResponse.HttpStatusCode;
            if (status.ToString() == "OK")
                Console.WriteLine("ModifyHostsRequest succeeded");
            else
                Console.WriteLine("ModifyHostsRequest failed");
        }
        private static void DeleteVPCEndPoint()
        {
            AmazonEC2Client client = new AmazonEC2Client();
            DescribeVpcEndpointsRequest endpointRequest = new DescribeVpcEndpointsRequest();
            endpointRequest.MaxResults = 5;
            DescribeVpcEndpointsResponse endpointResponse = client.DescribeVpcEndpoints(endpointRequest);
            List<VpcEndpoint> endpointList = endpointResponse.VpcEndpoints;
            var vpcEndPointListIds = new List<string>();
            foreach (VpcEndpoint vpc in endpointList)
            {
                Console.WriteLine("VpcEndpoint ID = " + vpc.VpcEndpointId);
                vpcEndPointListIds.Add(vpc.VpcEndpointId);
            }
            DeleteVpcEndpointsRequest deleteRequest = new DeleteVpcEndpointsRequest();
            deleteRequest.VpcEndpointIds = vpcEndPointListIds;
            client.DeleteVpcEndpoints(deleteRequest);
        }
        public static void DescribeElasticIps()
        {
            using (var client = new AmazonEC2Client(RegionEndpoint.USWest2))
            {
                var addresses = client.DescribeAddresses(new DescribeAddressesRequest
                {
                    Filters = new List<Filter>
                    {
                        new Amazon.EC2.Model.Filter
                        {
                            Name = "domain",
                            Values = new List<string> { "vpc" }
                        }
                    }
                }).Addresses;

                foreach (var address in addresses)
                {
                    Console.WriteLine(address.PublicIp);
                    Console.WriteLine("\tAllocation Id: " + address.AllocationId);
                    Console.WriteLine("\tPrivate IP Address: " + address.PrivateIpAddress);
                    Console.WriteLine("\tAssociation Id: " + address.AssociationId);
                    Console.WriteLine("\tInstance Id: " + address.InstanceId);
                    Console.WriteLine("\tNetwork Interface Owner Id: " + address.NetworkInterfaceOwnerId);
                }
            }
        }
        public static void AllocateAndAssociate(string instanceId)
        {
            using (var client = new AmazonEC2Client(RegionEndpoint.USWest2))
            {
                var allocationId = client.AllocateAddress(new AllocateAddressRequest
                {
                    Domain = DomainType.Vpc
                }).AllocationId;

                Console.WriteLine("Allocation Id: " + allocationId);

                var associationId = client.AssociateAddress(new AssociateAddressRequest
                {
                    AllocationId = allocationId,
                    InstanceId = instanceId
                }).AssociationId;

                Console.WriteLine("Association Id: " + associationId);
            }
        }
        public static void Release(string allocationId)
        {
            using (var client = new AmazonEC2Client(RegionEndpoint.USWest2))
            {
                client.ReleaseAddress(new ReleaseAddressRequest
                {
                    AllocationId = allocationId
                });
            }
        }
    }

}
