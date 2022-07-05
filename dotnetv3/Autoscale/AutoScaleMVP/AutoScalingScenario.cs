/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

using System;
using Amazon;
using Amazon.AutoScaling;
using Amazon.AutoScaling.Model;

/**
 *  Before running this SDK for .NET (v3) code example, set up your development environment, including your credentials.
 *
 *  For more information, see the following documentation:
 *
 *  https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html
 *
 *  In addition, create a launch template. For more information, see the following topic:
 *
 *  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template
 *
 * This code example performs the following operations:
 * 1. Creates an Auto Scaling group.
 * 2. Gets a specific Auto Scaling group and returns an instance Id value.
 * 3. Describes Auto Scaling with the Id value.
 * 4. Enables metrics collection.
 * 5. Describes Auto Scaling groups.
 * 6. Describes Account details.
 * 7. Updates an Auto Scaling group to use an additional instance.
 * 8. Gets the specific Auto Scaling group and gets the number of instances.
 * 9. List the scaling activities that have occurred for the group.
 * 10. Terminates an instance in the Auto Scaling group.
 * 11. Stops the metrics collection.
 * 12. Deletes the Auto Scaling group.
 */

namespace AutoScaleMVP
{
    public class AutoScalingScenario
    {
        public async Task PerformAutoScalingTasks()
        {
            /*
            *   Set the following variables: 
            *  
            *   groupName - The name of the Auto Scaling group.\n" +
            *   launchTemplateName - The name of the launch template. \n" +
            *   serviceLinkedRoleARN - The Amazon Resource Name (ARN) of the service-linked role that the Auto Scaling group uses.\n" +
            *   vpcZoneId - A subnet Id for a virtual private cloud (VPC) where instances in the Auto Scaling group can be created.\n" ;
            */

            string groupName = "<Enter Value>";
            string launchTemplateName = "<Enter Value>";
            string serviceLinkedRoleARN = "<Enter Value>";
            string vpcZoneId = "<Enter Value>";
            var autoScalingClient = new AmazonAutoScalingClient(RegionEndpoint.USEast1);

            Console.WriteLine("**** Create an Auto Scaling group named " + groupName);
            await CreateAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);

            Console.WriteLine("Wait 2 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
            System.Threading.Thread.Sleep(120000);

            Console.WriteLine("**** Get Auto Scale group Id value");
            var instanceId = await GetSpecificAutoScalingGroups(autoScalingClient, groupName);
                                    
            if (string.Equals(instanceId, ""))
            {
                Console.WriteLine("Error - no instance Id value");
                System.Environment.Exit(1);
            }
            else
            {
                Console.WriteLine("The instance Id value is " + instanceId);
            }

            Console.WriteLine("**** Describe Auto Scaling with the Id value " + instanceId);
            await DescribeAutoScalingInstance(autoScalingClient, instanceId);

            Console.WriteLine("**** Enable metrics collection " + instanceId);
            await EnableMetricsCollection(autoScalingClient, groupName);

            Console.WriteLine("**** Update an Auto Scaling group to update max size to 3");
            await UpdateAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN);

            Console.WriteLine("**** Describe all Auto Scaling groups to show the current state of the groups");
            await DescribeAutoScalingGroups(autoScalingClient, groupName);

            Console.WriteLine("**** Describe account details");
            await DescribeAccountLimits(autoScalingClient);

            Console.WriteLine("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
            System.Threading.Thread.Sleep(60000);

            Console.WriteLine("**** Set desired capacity to 2");
            await SetDesiredCapacity(autoScalingClient, groupName);

            Console.WriteLine("**** Get the two instance Id values and state");
            await GetAutoScalingGroups(autoScalingClient, groupName);

            Console.WriteLine("**** List the scaling activities that have occurred for the group");
            await DescribeScalingActivities(autoScalingClient, groupName);

            Console.WriteLine("**** Terminate an instance in the Auto Scaling group");
            await TerminateInstanceInAutoScalingGroup(autoScalingClient, instanceId);

            Console.WriteLine("**** Delete the Auto Scaling group");
            await DeleteAutoScalingGroup(autoScalingClient, groupName);
        }

        public async Task DeleteAutoScalingGroup(AmazonAutoScalingClient autoScalingClient, String groupName)
        {
           DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = new DeleteAutoScalingGroupRequest
           {
               AutoScalingGroupName = groupName,
               ForceDelete = true
           };

           await autoScalingClient.DeleteAutoScalingGroupAsync(deleteAutoScalingGroupRequest);
           Console.WriteLine("You successfully deleted " + groupName);
        }

        public async Task TerminateInstanceInAutoScalingGroup(AmazonAutoScalingClient autoScalingClient, string instanceId)
        {
            var request = new TerminateInstanceInAutoScalingGroupRequest
            {
                InstanceId = instanceId,
                ShouldDecrementDesiredCapacity = false
            };

            await autoScalingClient.TerminateInstanceInAutoScalingGroupAsync(request);
            Console.WriteLine("You have terminated instance " + instanceId);
        }

        public async Task DescribeScalingActivities(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            DescribeScalingActivitiesRequest scalingActivitiesRequest = new DescribeScalingActivitiesRequest
            {
                AutoScalingGroupName = groupName,
                MaxRecords = 10
            }; 

            var response = await autoScalingClient.DescribeScalingActivitiesAsync(scalingActivitiesRequest);
            var activities = response.Activities;
            foreach (Activity activity in activities)
            {
                Console.WriteLine("The activity Id is " + activity.ActivityId);
                Console.WriteLine("The activity details are " + activity.Details);
            }
        }

        public async Task<string> GetAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
           var instanceId = "";
           var groupList = new List<string>();
           groupList.Add(groupName); 
           var scalingGroupsRequest = new DescribeAutoScalingGroupsRequest
           { 
                AutoScalingGroupNames = groupList
           };

           var response = await autoScalingClient.DescribeAutoScalingGroupsAsync(scalingGroupsRequest);
           var groups = response.AutoScalingGroups;
           foreach (AutoScalingGroup group in groups)
           {
                Console.WriteLine("The group name is " + group.AutoScalingGroupName);
                Console.WriteLine("The group ARN is " + group.AutoScalingGroupARN);
                var instances = group.Instances;
                foreach (Instance instance in instances)
                {
                     instanceId = instance.InstanceId;
                     Console.WriteLine("The instance id is " + instanceId);
                     Console.WriteLine("The lifecycle state is " + instance.LifecycleState);
                }
           }
         
            return instanceId;
        }

        public async Task SetDesiredCapacity(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
           var capacityRequest = new SetDesiredCapacityRequest
           {
               AutoScalingGroupName = groupName,
               DesiredCapacity = 2
           };

            await autoScalingClient.SetDesiredCapacityAsync(capacityRequest);
            Console.WriteLine("You have set the DesiredCapacity to 2");
        }

        public async Task DescribeAccountLimits(AmazonAutoScalingClient autoScalingClient)
        {
            DescribeAccountLimitsResponse response = await autoScalingClient.DescribeAccountLimitsAsync();
            Console.WriteLine("The max number of auto scaling groups is " + response.MaxNumberOfAutoScalingGroups);
            Console.WriteLine("The current number of auto scaling groups is " + response.NumberOfAutoScalingGroups);
        }

        public async Task DescribeAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var groupList = new List<string>();
            groupList.Add(groupName); 
            var groupsRequest = new DescribeAutoScalingGroupsRequest
                { 
                   AutoScalingGroupNames = groupList,
                   MaxRecords = 10
                };

            var response = await autoScalingClient.DescribeAutoScalingGroupsAsync(groupsRequest);
            var groups = response.AutoScalingGroups;
            foreach (AutoScalingGroup group in groups)
            { 
                Console.WriteLine("*** The service to use for the health checks: " + group.HealthCheckType);
            }
        }

        public async Task EnableMetricsCollection(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var listMetrics = new List<string>();
            listMetrics.Add("GroupMaxSize");

            var collectionRequest = new EnableMetricsCollectionRequest
            {         
                AutoScalingGroupName = groupName,
                Metrics = listMetrics,
                Granularity = "1Minute"
            };

            await autoScalingClient.EnableMetricsCollectionAsync(collectionRequest);
            Console.WriteLine("The enable metrics collection operation was successful");
        }

        public async Task DescribeAutoScalingInstance(AmazonAutoScalingClient autoScalingClient, string id)
        {
            var listIdValues = new List<string>();
            listIdValues.Add(id);
            var describeAutoScalingInstancesRequest = new DescribeAutoScalingInstancesRequest
            { 
                   InstanceIds = listIdValues
            };

            var response = await autoScalingClient.DescribeAutoScalingInstancesAsync(describeAutoScalingInstancesRequest);
            var instances = response.AutoScalingInstances;
            foreach (AutoScalingInstanceDetails instance in instances)
            {
                Console.WriteLine("The instance lifecycle state is: " + instance.LifecycleState);
            }
        }

        public async Task<string> GetSpecificAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var instanceId = "";
            var groupNameList = new List<string>();
            groupNameList.Add(groupName);
            var scalingGroupsRequest = new DescribeAutoScalingGroupsRequest
            { 
                   AutoScalingGroupNames = groupNameList
            };

            var response = await autoScalingClient.DescribeAutoScalingGroupsAsync(scalingGroupsRequest);
            var groups = response.AutoScalingGroups;
            foreach (var group in groups)
            {
                Console.WriteLine("The group name is " + group.AutoScalingGroupName);
                Console.WriteLine("The group ARN is " + group.AutoScalingGroupARN);
                var instances = group.Instances;
                foreach (var instance in instances)
                {
                    instanceId = instance.InstanceId;
                    Console.WriteLine("The instance id is " + instanceId);
                    Console.WriteLine("The lifecycle state is " + instance.LifecycleState);
                }
            }

            return instanceId;
        }
        public async Task UpdateAutoScalingGroup(AmazonAutoScalingClient autoScalingClient, string groupName, string launchTemplateName, String serviceLinkedRoleARN)
        {
            var templateSpecification = new LaunchTemplateSpecification
            { 
                  LaunchTemplateName = launchTemplateName
            };

            var groupRequest = new UpdateAutoScalingGroupRequest
            { 
                    MaxSize = 3,
                    ServiceLinkedRoleARN = serviceLinkedRoleARN,
                    AutoScalingGroupName = groupName,
                    LaunchTemplate = templateSpecification
            };

            await autoScalingClient.UpdateAutoScalingGroupAsync(groupRequest);
            Console.WriteLine("You successfully updated the auto scaling group  " + groupName);
        }

        public async Task CreateAutoScalingGroup(AmazonAutoScalingClient autoScalingClient,
                                              string groupName,
                                              string launchTemplateName,
                                              string serviceLinkedRoleARN,
                                              string vpcZoneId)
        {
            var templateSpecification = new LaunchTemplateSpecification
            {
                LaunchTemplateName = launchTemplateName
            };

            var zoneList = new List<string>();
            zoneList.Add("us-east-1a");

            var request = new CreateAutoScalingGroupRequest
            {
                AutoScalingGroupName = groupName,
                AvailabilityZones = zoneList,
                LaunchTemplate = templateSpecification,
                MaxSize = 1,
                MinSize = 1,
                VPCZoneIdentifier = vpcZoneId,
                ServiceLinkedRoleARN = serviceLinkedRoleARN
            };

            await autoScalingClient.CreateAutoScalingGroupAsync(request);
            Console.WriteLine(groupName +" Auto Scaling Group created");
        }
    }
}
