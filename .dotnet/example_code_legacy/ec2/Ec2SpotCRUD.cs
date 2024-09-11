// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
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
using Amazon;
using Amazon.EC2;
using Amazon.EC2.Model;
// snippet-end:[ec2.dotnet.spot_instance_using]

namespace Ec2SpotCrud
{
    class Program
    {
        // snippet-start:[ec2.dotnet.spot_instance_request_spot_instance]
        /* Creates a spot instance
         *
         * Takes six args:
         *   AmazonEC2Client ec2Client is the EC2 client through which the spot instance request is made
         *   string amiId is the AMI of the instance to request
         *   string securityGroupName is the name of the security group of the instance to request
         *   InstanceType instanceType is the type of the instance to request
         *   string spotPrice is the price of the instance to request
         *   int instanceCount is the number of instances to request
         *
         * See https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/MEC2RequestSpotInstancesRequestSpotInstancesRequest.html
         */
        private static SpotInstanceRequest RequestSpotInstance(
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

            var result = ec2Client.RequestSpotInstancesAsync(request);

            return result.Result.SpotInstanceRequests[0];
        }
        // snippet-end:[ec2.dotnet.spot_instance_request_spot_instance]

        // snippet-start:[ec2.dotnet.spot_instance_get_spot_request_state]
        /* Gets the state of a spot instance request.
         * Takes two args:
         *   AmazonEC2Client ec2Client is the EC2 client through which information about the state of the spot instance is made
         *   string spotRequestId is the ID of the spot instance
         *
         * See https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/MEC2DescribeSpotInstanceRequests.html
         */
        private static SpotInstanceState GetSpotRequestState(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            // Create the describeRequest object with all of the request ids
            // to monitor (e.g. that we started).
            var request = new DescribeSpotInstanceRequestsRequest();
            request.SpotInstanceRequestIds.Add(spotRequestId);

            // Retrieve the request we want to monitor.
            var describeResponse = ec2Client.DescribeSpotInstanceRequestsAsync(request);

            SpotInstanceRequest req = describeResponse.Result.SpotInstanceRequests[0];

            return req.State;
        }
        // snippet-end:[ec2.dotnet.spot_instance_get_spot_request_state]

        // snippet-start:[ec2.dotnet.spot_instance_cancel_spot_request]
        /* Cancels a spot instance request
         * Takes two args:
         *   AmazonEC2Client ec2Client is the EC2 client through which the spot instance is cancelled
         *   string spotRequestId is the ID of the spot instance
         *
         * See https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/MEC2CancelSpotInstanceRequestsCancelSpotInstanceRequestsRequest.html
         */
        private static void CancelSpotRequest(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            var cancelRequest = new CancelSpotInstanceRequestsRequest();

            cancelRequest.SpotInstanceRequestIds.Add(spotRequestId);

            ec2Client.CancelSpotInstanceRequestsAsync(cancelRequest);
        }
        // snippet-end:[ec2.dotnet.spot_instance_cancel_spot_request]

        // snippet-start:[ec2.dotnet.spot_instance_terminate_spot_request]
        /* Terminates a spot instance request
         * Takes two args:
         *   AmazonEC2Client ec2Client is the EC2 client through which the spot instance is terminated
         *   string spotRequestId is the ID of the spot instance
         *
         * See https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/MEC2TerminateInstancesTerminateInstancesRequest.html
         */
        private static void TerminateSpotInstance(
            AmazonEC2Client ec2Client,
            string spotRequestId)
        {
            var describeRequest = new DescribeSpotInstanceRequestsRequest();
            describeRequest.SpotInstanceRequestIds.Add(spotRequestId);

            // Retrieve the request we want to monitor.
            var describeResponse = ec2Client.DescribeSpotInstanceRequestsAsync(describeRequest);

            if (SpotInstanceState.Active == describeResponse.Result.SpotInstanceRequests[0].State)
            {
                string instanceId = describeResponse.Result.SpotInstanceRequests[0].InstanceId;

                var terminateRequest = new TerminateInstancesRequest();
                terminateRequest.InstanceIds = new List<string>() { instanceId };

                try
                {
                    ec2Client.TerminateInstancesAsync(terminateRequest);
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
        /* Creates, cancels, and terminates a spot instance request
         *
         *   AmazonEC2Client ec2Client is the EC2 client through which the spot instance is manipulated
         *   string spotRequestId is the ID of the spot instance
         *
         * See https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/EC2/MEC2TerminateInstancesTerminateInstancesRequest.html
         */

        // Displays information about the command-line args
        private static void Usage()
        {
            Console.WriteLine("");
            Console.WriteLine("Usage:");
            Console.WriteLine("");
            Console.WriteLine("Ec2SpotCrud.exe AMI [-s SECURITY_GROUP] [-p SPOT_PRICE] [-c INSTANCE_COUNT] [-h]");
            Console.WriteLine("  where:");
            Console.WriteLine("  AMI is the AMI to use. No default value. Cannot be an empty string.");
            Console.WriteLine("  SECURITY_GROUP is the name of a security group. Default is default. Cannot be an empty string.");
            Console.WriteLine("  SPOT_PRICE is the spot price. Default is 0.003. Must be > 0.001.");
            Console.WriteLine("  INSTANCE_COUNT is the number of instances. Default is 1. Must be > 0.");
            Console.WriteLine("  -h displays this message and quits");
            Console.WriteLine();
        }

        /* Creates, cancels, and terminates a spot instance request
         * See Usage() for information about the command-line args
         */
        static void Main(string[] args)
        {
            // Values that aren't easy to pass on the command line
            RegionEndpoint region = RegionEndpoint.USWest2;
            InstanceType instanceType = InstanceType.T1Micro;

            // Default values for optional command-line args
            string securityGroupName = "default";
            string spotPrice = "0.003";
            int instanceCount = 1;

            // Placeholder for the only required command-line arg
            string amiId = "";

            // Parse command-line args
            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                    case "-s":
                        i++;
                        securityGroupName = args[i];
                        if (securityGroupName == "")
                        {
                            Console.WriteLine("The security group name cannot be blank");
                            Usage();
                            return;
                        }
                        break;
                    case "-p":
                        i++;
                        spotPrice = args[i];
                        double price;
                        double.TryParse(spotPrice, out price);
                        if (price < 0.001)
                        {
                            Console.WriteLine("The spot price must be > 0.001");
                            Usage();
                            return;
                        }
                        break;
                    case "-c":
                        i++;
                        int.TryParse(args[i], out instanceCount);
                        if (instanceCount < 1)
                        {
                            Console.WriteLine("The instance count must be > 0");
                            Usage();
                            return;
                        }
                        break;
                    case "-h":
                        Usage();
                        return;
                    default:
                        amiId = args[i];
                        break;
                }

                i++;
            }

            // Make sure we have an AMI
            if (amiId == "")
            {
                Console.WriteLine("You must supply an AMI");
                Usage();
                return;
            }

            AmazonEC2Client ec2Client = new AmazonEC2Client(region: region);

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
                // 1, 2, 4, ...
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

            Console.ReadLine();
        }
        // snippet-end:[ec2.dotnet.spot_instance_main]
    }
}
