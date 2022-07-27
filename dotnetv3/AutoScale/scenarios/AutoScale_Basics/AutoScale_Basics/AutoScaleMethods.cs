// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AutoScale_Basics
{
    public class AutoScaleMethods
    {
        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.DeleteAutoScalingGroup]
        public static async Task<bool> DeleteAutoScalingGroup(
            AmazonAutoScalingClient autoScalingClient,
            string groupName)
        {
            var deleteAutoScalingGroupRequest = new DeleteAutoScalingGroupRequest
            {
                AutoScalingGroupName = groupName,
                ForceDelete = true,
            };

            var response = await autoScalingClient.DeleteAutoScalingGroupAsync(deleteAutoScalingGroupRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"You successfully deleted {groupName}");
                return true;
            }

            Console.WriteLine($"Couldn't delete {groupName}.");
            return false;
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.DeleteAutoScalingGroup]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.TerminateInstanceInAutoScalingGroup]
        public static async Task<bool> TerminateInstanceInAutoScalingGroup(
            AmazonAutoScalingClient autoScalingClient,
            string instanceId)
        {
            var request = new TerminateInstanceInAutoScalingGroupRequest
            {
                InstanceId = instanceId,
                ShouldDecrementDesiredCapacity = false,
            };

            var response = await autoScalingClient.TerminateInstanceInAutoScalingGroupAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"You have terminated the instance {instanceId}");
                return true;
            }

            Console.WriteLine($"Could not terminate {instanceId}");
            return false;
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.TerminateInstanceInAutoScalingGroup]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.DescribeScalingActivities]
        public static async Task<List<Activity>> DescribeScalingActivities(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var scalingActivitiesRequest = new DescribeScalingActivitiesRequest
            {
                AutoScalingGroupName = groupName,
                MaxRecords = 10,
            };

            var response = await autoScalingClient.DescribeScalingActivitiesAsync(scalingActivitiesRequest);
            return response.Activities;
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.DescribeScalingActivities]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.GetAutoScalingGroups]
        public static async Task<string> GetAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var instanceId = string.Empty;
            var groupList = new List<string>
            {
                groupName,
            };

            var scalingGroupsRequest = new DescribeAutoScalingGroupsRequest
            {
                AutoScalingGroupNames = groupList,
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

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.GetAutoScalingGroups]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.SetDesiredCapacity]
        public static async Task SetDesiredCapacity(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var capacityRequest = new SetDesiredCapacityRequest
            {
                AutoScalingGroupName = groupName,
                DesiredCapacity = 2,
            };

            await autoScalingClient.SetDesiredCapacityAsync(capacityRequest);
            Console.WriteLine("You have set the DesiredCapacity to 2");
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.SetDesiredCapacity]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.DescribeAccountLimits]
        public static async Task DescribeAccountLimits(AmazonAutoScalingClient autoScalingClient)
        {
            DescribeAccountLimitsResponse response = await autoScalingClient.DescribeAccountLimitsAsync();
            Console.WriteLine("The max number of auto scaling groups is " + response.MaxNumberOfAutoScalingGroups);
            Console.WriteLine("The current number of auto scaling groups is " + response.NumberOfAutoScalingGroups);
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.DescribeAccountLimits]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.DescribeAutoScalingGroups]
        public static async Task DescribeAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var groupList = new List<string>
            {
                groupName,
            };

            var groupsRequest = new DescribeAutoScalingGroupsRequest
            {
                AutoScalingGroupNames = groupList,
                MaxRecords = 10,
            };

            var response = await autoScalingClient.DescribeAutoScalingGroupsAsync(groupsRequest);
            var groups = response.AutoScalingGroups;
            foreach (AutoScalingGroup group in groups)
            {
                Console.WriteLine("*** The service to use for the health checks: " + group.HealthCheckType);
            }
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.DescribeAutoScalingGroups]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.EnableMetricsCollection]
        public static async Task EnableMetricsCollection(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var listMetrics = new List<string>
            {
                "GroupMaxSize",
            };

            var collectionRequest = new EnableMetricsCollectionRequest
            {
                AutoScalingGroupName = groupName,
                Metrics = listMetrics,
                Granularity = "1Minute",
            };

            await autoScalingClient.EnableMetricsCollectionAsync(collectionRequest);
            Console.WriteLine("The enable metrics collection operation was successful");
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.EnableMetricsCollection]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.DescribeAutoScalingInstance]
        public static async Task DescribeAutoScalingInstance(
            AmazonAutoScalingClient autoScalingClient,
            string id)
        {
            var listIdValues = new List<string>
            {
                id,
            };
            var describeAutoScalingInstancesRequest = new DescribeAutoScalingInstancesRequest
            {
                InstanceIds = listIdValues,
            };

            var response = await autoScalingClient.DescribeAutoScalingInstancesAsync(describeAutoScalingInstancesRequest);
            var instances = response.AutoScalingInstances;
            foreach (AutoScalingInstanceDetails instance in instances)
            {
                Console.WriteLine("The instance lifecycle state is: " + instance.LifecycleState);
            }
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.DescribeAutoScalingInstance]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.GetSpecificAutoScalingGroups]
        public static async Task<string> GetSpecificAutoScalingGroups(AmazonAutoScalingClient autoScalingClient, string groupName)
        {
            var instanceId = string.Empty;
            var groupNameList = new List<string>
            {
                groupName,
            };

            var scalingGroupsRequest = new DescribeAutoScalingGroupsRequest
            {
                AutoScalingGroupNames = groupNameList,
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

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.GetSpecificAutoScalingGroups]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.UpdateAutoScalingGroup]
        public static async Task UpdateAutoScalingGroup(
            AmazonAutoScalingClient autoScalingClient,
            string groupName,
            string launchTemplateName,
            string serviceLinkedRoleARN)
        {
            var templateSpecification = new LaunchTemplateSpecification
            {
                LaunchTemplateName = launchTemplateName,
            };

            var groupRequest = new UpdateAutoScalingGroupRequest
            {
                MaxSize = 3,
                ServiceLinkedRoleARN = serviceLinkedRoleARN,
                AutoScalingGroupName = groupName,
                LaunchTemplate = templateSpecification,
            };

            await autoScalingClient.UpdateAutoScalingGroupAsync(groupRequest);
            Console.WriteLine("You successfully updated the auto scaling group  " + groupName);
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.UpdateAutoScalingGroup]

        // snippet-start:[autoscale.dotnetv3.create_scaling_scenario.CreateAutoScalingGroup]
        public static async Task CreateAutoScalingGroup(
            AmazonAutoScalingClient autoScalingClient,
            string groupName,
            string launchTemplateName,
            string serviceLinkedRoleARN,
            string vpcZoneId)
        {
            var templateSpecification = new LaunchTemplateSpecification
            {
                LaunchTemplateName = launchTemplateName,
            };

            var zoneList = new List<string>
            {
                "us-east-1a",
            };

            var request = new CreateAutoScalingGroupRequest
            {
                AutoScalingGroupName = groupName,
                AvailabilityZones = zoneList,
                LaunchTemplate = templateSpecification,
                MaxSize = 1,
                MinSize = 1,
                VPCZoneIdentifier = vpcZoneId,
                ServiceLinkedRoleARN = serviceLinkedRoleARN,
            };

            await autoScalingClient.CreateAutoScalingGroupAsync(request);
            Console.WriteLine(groupName + " Auto Scaling Group created");
        }

        // snippet-end:[autoscale.dotnetv3.create_scaling_scenario.CreateAutoScalingGroup]
    }
}
