//snippet-sourcedescription:[Ec2SpotCRUD demonstrates how to create, cancel, and terminate a spot instance request using Amazon EC2.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Compute Cloud]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[Sep 20, 2019]
//snippet-sourceauthor:[Doug-AWS]
/*******************************************************************************
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
/* Build 
   Don't forget to use the Visual Studio command prompt
   Tested with csc /version == 2.10.0.0
     (C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\MSBuild\15.0\bin\Roslyn)
   AWSSDK.Core.dll
     (C:\Users\USERNAME\.nuget\packages\awssdk.core\3.3.100\lib\net45)
   AWSSDK.EC2.dll
     (C:\Users\USERNAME\.nuget\packages\awssdk.ec2\3.3.130.2\lib\net45)

   csc Ec2SpotCRUD.cs -reference:AWSSDK.Core.dll -reference:AWSSDK.EC2.dll

   Get the list of AWSSDK Nuget packages at:
       https://www.nuget.org/packages?q=AWSSDK&prerel=false
 */
// snippet-start:[ec2.dotnet.spot_instance_using]
using System;
using System.Collections.Generic;
using System.Threading;

using Amazon.EC2;
using Amazon.EC2.Model;
// snippet-end:[ec2.dotnet.spot_instance_using]

namespace Ec2SpotCrud
{
    class Program
    {
        // snippet-start:[ec2.dotnet.spot_instance_request_spot_instance]    
        public static SpotInstanceRequest RequestSpotInstance(
            AmazonEC2Client ec2Client,
            string amiId,
            string securityGroupName,
            InstanceType instanceType,
            string spotPrice,
            int instanceCount)
        {
            RequestSpotInstancesRequest request = new RequestSpotInstancesRequest
            {
                SpotPrice = spotPrice,
                InstanceCount = instanceCount
            };

            LaunchSpecification launchSpecification = new LaunchSpecification
            {
                ImageId = amiId,
                InstanceType = instanceType
            };

            launchSpecification.SecurityGroups.Add(securityGroupName);

            request.LaunchSpecification = launchSpecification;

            RequestSpotInstancesResponse result = ec2Client.RequestSpotInstances(request);

            return result.SpotInstanceRequests[0];
        }
        // snippet-end:[ec2.dotnet.spot_instance_request_spot_instance]

        // snippet-start:[ec2.dotnet.spot_instance_get_spot_request_state]
        public static SpotInstanceState GetSpotRequestState(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            // Create the describeRequest object with all of the request ids
            // to monitor (e.g. that we started).
            var request = new DescribeSpotInstanceRequestsRequest();
            request.SpotInstanceRequestIds.Add(spotRequestId);

            // Retrieve the request we want to monitor.
            var describeResponse = ec2Client.DescribeSpotInstanceRequests(request);

            SpotInstanceRequest req = describeResponse.SpotInstanceRequests[0];

            return req.State;
        }
        // snippet-end:[ec2.dotnet.spot_instance_get_spot_request_state]

        // snippet-start:[ec2.dotnet.spot_instance_cancel_spot_request]
        public static void CancelSpotRequest(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            var cancelRequest = new CancelSpotInstanceRequestsRequest();

            cancelRequest.SpotInstanceRequestIds.Add(spotRequestId);

            ec2Client.CancelSpotInstanceRequests(cancelRequest);
        }
        // snippet-end:[ec2.dotnet.spot_instance_cancel_spot_request]

        // snippet-start:[ec2.dotnet.spot_instance_terminate_spot_request]
        public static void TerminateSpotInstance(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            var describeRequest = new DescribeSpotInstanceRequestsRequest();
            describeRequest.SpotInstanceRequestIds.Add(spotRequestId);

            // Retrieve the request we want to monitor.
            var describeResponse = ec2Client.DescribeSpotInstanceRequests(describeRequest);

            if (SpotInstanceState.Active == describeResponse.SpotInstanceRequests[0].State)
            {
                string instanceId = describeResponse.SpotInstanceRequests[0].InstanceId;

                var terminateRequest = new TerminateInstancesRequest();
                terminateRequest.InstanceIds = new List<string>() { instanceId };

                try
                {
                    var terminateResponse = ec2Client.TerminateInstances(terminateRequest);
                }
                catch (AmazonEC2Exception ex)
                {
                    // Check the ErrorCode to see if the instance does not exist.
                    if ("InvalidInstanceID.NotFound" == ex.ErrorCode)
                    {
                        Console.WriteLine("Instance {0} does not exist.", instanceId);
                    }
                    else
                    {
                        // The exception was thrown for another reason, so re-throw the exception.
                        throw;
                    }
                }
            }
        }
        // snippet-end:[ec2.dotnet.spot_instance_terminate_spot_request]

        // snippet-start:[ec2.dotnet.spot_instance_main]
        // This program takes one argument, the AMI
        // Something like: ami-xxxxxxxx
        static void Main(string[] args)
        {
            string amiId = args[0];
            string securityGroupName = "default";
            InstanceType instanceType = InstanceType.T1Micro;
            string spotPrice = "0.003";
            int instanceCount = 1;

            AmazonEC2Client ec2Client = new AmazonEC2Client(region: Amazon.RegionEndpoint.USWest2);

            Console.WriteLine("Creating spot instance request");

            SpotInstanceRequest req = RequestSpotInstance(ec2Client, amiId, securityGroupName, instanceType, spotPrice, instanceCount);

            string id = req.SpotInstanceRequestId;

            // Wait for it to become active
            Console.WriteLine("Waiting for spot instance request with ID " + id + " to become active");

            int wait = 1;
            int totalTime = 0;

            while (true)
            {
                totalTime += wait;
                Console.Write(".");

                SpotInstanceState state = GetSpotRequestState(ec2Client, id);

                if (state == SpotInstanceState.Active)
                {
                    Console.WriteLine("");

                    break;
                }

                // wait a bit and try again
                Thread.Sleep(wait);

                // wait longer next time
                wait = wait * 2;
            }

            // Should be around 1000 (one second)
            Console.WriteLine("That took " + totalTime + " milliseconds");

            // Cancel the request
            Console.WriteLine("Canceling spot instance request");

            CancelSpotRequest(ec2Client, id);

            // Clean everything up
            Console.WriteLine("Terminating spot instance request");

            TerminateSpotInstance(ec2Client, id);

            Console.WriteLine("Done. Press enter to quit");

            string resp = Console.ReadLine();
        }
        // snippet-end:[ec2.dotnet.spot_instance_main]        
    }
}
