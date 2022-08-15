// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace AutoScale_Basics
{
    public class AutoScaleMethods
    {
        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.CreateAutoScalingGroup]
        /// <summary>
        /// Creates a new Amazon Auto Scaling group.
        /// </summary>
        /// <param name="client">The  initialized Amazon Auto Scaling client object.</param>
        /// <param name="groupName">The name to use for the new Amazon Auto
        /// Scaling group.</param>
        /// <param name="launchTemplateName">The name of the Amazon EC2 launch template
        /// that will be used to create instances in the group.</param>
        /// <param name="serviceLinkedRoleARN">The Amazon IAM service-linked role that
        /// provides the permissions to use with the Amazon Auso Scaling group.</param>
        /// <returns></returns>
        public static async Task<bool> CreateAutoScalingGroup(
            AmazonAutoScalingClient client,
            string groupName,
            string launchTemplateName,
            string serviceLinkedRoleARN)
        {
            var templateSpecification = new LaunchTemplateSpecification
            {
                LaunchTemplateId = launchTemplateName,
            };

            var zoneList = new List<string>
            {
                "us-east-2a",
            };

            var request = new CreateAutoScalingGroupRequest
            {
                AutoScalingGroupName = groupName,
                AvailabilityZones = zoneList,
                LaunchTemplate = templateSpecification,
                MaxSize = 1,
                MinSize = 1,
                ServiceLinkedRoleARN = serviceLinkedRoleARN,
            };

            var response = await client.CreateAutoScalingGroupAsync(request);
            Console.WriteLine(groupName + " Auto Scaling Group created");
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.CreateAutoScalingGroup]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAccountLimits]
        public static async Task DescribeAccountLimitsAsync(AmazonAutoScalingClient client)
        {
            var response = await client.DescribeAccountLimitsAsync();
            Console.WriteLine("The max number of auto scaling groups is " + response.MaxNumberOfAutoScalingGroups);
            Console.WriteLine("The current number of auto scaling groups is " + response.NumberOfAutoScalingGroups);
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAccountLimits]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.DescribeScalingActivities]
        public static async Task<List<Activity>> DescribeAuotoScalingActivitiesAsync(
            AmazonAutoScalingClient client,
            string groupName)
        {
            var scalingActivitiesRequest = new DescribeScalingActivitiesRequest
            {
                AutoScalingGroupName = groupName,
                MaxRecords = 10,
            };

            var response = await client.DescribeScalingActivitiesAsync(scalingActivitiesRequest);
            return response.Activities;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.DescribeScalingActivities]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAutoScalingInstances]
        public static async Task<List<AutoScalingInstanceDetails>> DescribeAutoScalingInstancesAsync(
            AmazonAutoScalingClient client,
            string groupName)
        {
            var groups = await DescribeAutoScalingGroupsAsync(client, groupName);
            var instanceIds = new List<string>();
            groups.ForEach(group =>
            {
                if (group.AutoScalingGroupName == groupName)
                {
                    group.Instances.ForEach(instance =>
                    {
                        instanceIds.Add(instance.InstanceId);
                    });
                }
            });

            var scalingGroupsRequest = new DescribeAutoScalingInstancesRequest
            {
                MaxRecords = 10,
                InstanceIds = instanceIds,
            };

            var response = await client.DescribeAutoScalingInstancesAsync(scalingGroupsRequest);
            var instanceDetails = response.AutoScalingInstances;

            return instanceDetails;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAutoScalingInstances]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAutoScalingGroups]
        public static async Task<List<AutoScalingGroup>> DescribeAutoScalingGroupsAsync(
            AmazonAutoScalingClient client,
            string groupName)
        {
            var groupList = new List<string>
            {
                groupName,
            };

            var request = new DescribeAutoScalingGroupsRequest
            {
                AutoScalingGroupNames = groupList,
            };

            var response = await client.DescribeAutoScalingGroupsAsync(request);
            var groups = response.AutoScalingGroups;

            return groups;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.DescribeAutoScalingGroups]

        // snippet-start: [AutoScale.dotnetv3.AutoScale_Basics.DisableMetricsCollection]
        public static async Task<bool> DisableMetricsCollectionAsync(AmazonAutoScalingClient client, string groupName)
        {
            var request = new DisableMetricsCollectionRequest
            {
                AutoScalingGroupName = groupName,
            };

            var response = await client.DisableMetricsCollectionAsync(request);
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end: [AutoScale.dotnetv3.AutoScale_Basics.DisableMetricsCollection]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.DeleteAutoScalingGroup]
        public static async Task<bool> DeleteAutoScalingGroupAsync(
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

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.DeleteAutoScalingGroup]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.EnableMetricsCollection]
        public static async Task<bool> EnableMetricsCollectionAsync(AmazonAutoScalingClient client, string groupName)
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

            var response = await client.EnableMetricsCollectionAsync(collectionRequest);
            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.EnableMetricsCollection]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.SetDesiredCapacity]
        public static async Task<bool> SetDesiredCapacityAsync(
            AmazonAutoScalingClient autoScalingClient,
            string groupName,
            int desiredCapacity)
        {
            var capacityRequest = new SetDesiredCapacityRequest
            {
                AutoScalingGroupName = groupName,
                DesiredCapacity = desiredCapacity,
            };

            var response = await autoScalingClient.SetDesiredCapacityAsync(capacityRequest);
            Console.WriteLine($"You have set the DesiredCapacity to {desiredCapacity}.");

            return response.HttpStatusCode == System.Net.HttpStatusCode.OK;
        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.SetDesiredCapacity]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.TerminateInstanceInAutoScalingGroup]
        public static async Task<bool> TerminateInstanceInAutoScalingGroupAsync(
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

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.TerminateInstanceInAutoScalingGroup]

        // snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.UpdateAutoScalingGroup]
        public static async Task<bool> UpdateAutoScalingGroupAsync(
            AmazonAutoScalingClient autoScalingClient,
            string groupName,
            string launchTemplateName,
            string serviceLinkedRoleARN,
            int maxSize)
        {
            var templateSpecification = new LaunchTemplateSpecification
            {
                LaunchTemplateName = launchTemplateName,
            };

            var groupRequest = new UpdateAutoScalingGroupRequest
            {
                MaxSize = maxSize,
                ServiceLinkedRoleARN = serviceLinkedRoleARN,
                AutoScalingGroupName = groupName,
                LaunchTemplate = templateSpecification,
            };

            var response = await autoScalingClient.UpdateAutoScalingGroupAsync(groupRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine("You successfully updated the auto scaling group  " + groupName);
                return true;
            }
            else
            {
                return false;
            }

        }

        // snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.UpdateAutoScalingGroup]
    }
}
