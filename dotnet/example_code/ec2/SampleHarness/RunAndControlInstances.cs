 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon EC2]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


﻿/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

using Amazon.EC2;
using Amazon.EC2.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AWS.Samples
{
    public partial class Program
    {
        /**********************************************************
         * IAM Role: arn:aws:iam::123456789012:role/ec2-sample-role
         * Based on  AmazonEC2FullAccess Policy shown below.
         *
         * {
         *   "Version": "2012-10-17",
         *   "Statement": [
         *     {
         *       "Action": "ec2:*",
         *       "Effect": "Allow",
         *       "Resource": "*"
         *     },
         *     {
         *       "Effect": "Allow",
         *       "Action": "elasticloadbalancing:*",
         *       "Resource": "*"
         *     },
         *     {
         *       "Effect": "Allow",
         *       "Action": "cloudwatch:*",
         *       "Resource": "*"
         *     },
         *     {
         *       "Effect": "Allow",
         *       "Action": "autoscaling:*",
         *       "Resource": "*"
         *     }
         *   ]
         * }
         ********************************************************/

        public static string LaunchInstance(string keyPairName, string secGroupName, AmazonEC2Client client)
        {
            string amiID = "ami-1712d877";

            SecurityGroup mySG = GetSecurityGroup(secGroupName, client);
            List<string> groups = new List<string>() { mySG.GroupId };

            var launchRequest = new RunInstancesRequest()
            {
                ImageId = amiID,
                InstanceType = InstanceType.T1Micro,
                MinCount = 1,
                MaxCount = 1,
                KeyName = keyPairName,
                SecurityGroupIds = groups
            };

            var launchResponse = client.RunInstances(launchRequest);
            var instances = launchResponse.Reservation.Instances;
            var instanceIds = new List<string>();
            foreach (Instance item in instances)
            {
                instanceIds.Add(item.InstanceId);
                Console.WriteLine();
                Console.WriteLine("New instance: " + item.InstanceId);
                Console.WriteLine("Instance state: " + item.State.Name);
            }

            return instanceIds[0];
        }

        public static void StopInstance(string id, AmazonEC2Client client)
        {
            var instanceIds = new List<String>();
            instanceIds.Add(id);

            var request = new StopInstancesRequest(instanceIds);

            StopInstancesResponse response = client.StopInstances(request);


        }

        public static void TerminateInstance( AmazonEC2Client ec2Client, string instanceId)
        {
            var request = new TerminateInstancesRequest();
            request.InstanceIds = new List<string>() { instanceId };

            try
            {
                var response = ec2Client.TerminateInstances(request);
                foreach (InstanceStateChange item in response.TerminatingInstances)
                {
                    Console.WriteLine("Terminated instance: " + item.InstanceId);
                    Console.WriteLine("Instance state: " + item.CurrentState.Name);
                }
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

        public static SecurityGroup GetSecurityGroup(string sgName, AmazonEC2Client client)
        {

            var request = new DescribeSecurityGroupsRequest();
            var response = client.DescribeSecurityGroups(request);
            List<SecurityGroup> mySGs = response.SecurityGroups;

            var sg = mySGs.Find(x => x.GroupName.Equals(sgName));

            //TODO: handle case where groupID not found. Find returns a default type (SecurityGroup) if not found.
            Console.WriteLine("Found security group name equal to {0}. (ID: {1})", sg.GroupName, sg.GroupId);

            return sg;
        }

        public static void DescribeImages(AmazonEC2Client client)
        {
            var owners = new List<String>();
            owners.Add("amazon");
            var filterOwner = new Filter("owner-alias", owners);

            var platforms = new List<string>();
            platforms.Add("windows");
            var filterPlatform = new Filter("platform", platforms);

            var architectures = new List<string>();
            architectures.Add("x86_64");
            var filterArchitecture = new Filter("architecture", architectures);


            var request = new DescribeImagesRequest();
            request.Filters.Add(filterOwner);
            request.Filters.Add(filterPlatform);
            // request.ExecutableUsers.Add("self");
            request.ImageIds.Add("ami-1712d877");

            List<Image> result = client.DescribeImages(request).Images;

            foreach (Image img in result)
            {
                Console.WriteLine("{0}: \t{1})", img.ImageId, img.Name);

            }
        }

    }
}
