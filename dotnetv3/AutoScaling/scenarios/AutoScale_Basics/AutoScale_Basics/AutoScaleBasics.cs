// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
  Before running this AWS SDK for .NET (v3) code example, set up your
  development environment, including your credentials.

  For more information, see the following documentation:

  https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html

  This example creates an Amazon Elastic Compute Cloud (Amazon EC2) launch
  template. You can also use the Amazon EC2 console. For more information, see
  the following:

  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template

 This code example performs the following operations:
  1. Creates an Amazon EC2 launch template
  2. Creates an Amazon EC2 Auto Scaling group.
  3. Shows the details of the new Auto Scaling group to show that only
     one instance was created.
  4. Enables metrics collection.
  5. Updates the Auto Scaling group to increase the max size to three.
  6. Describes Auto Scaling groups again to show the current state of the
     group.
  7. Changes the desired capacity of the Auto Scaling group to two.
  9. Retrieves the details of the group and shows the number of instances.
 10. Lists the scaling activities that have occurred for the group.
 11. Displays the Amazon CloudWatch metrics that have been collected.
 12. Terminates an instance in the Auto Scaling group.
 13. Disables metrics collection.
 13. Deletes the Auto Scaling group.
 14. Deletes the Amazon EC2 launch template.
*/

// snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.main]
var imageId = "ami-05803413c51f242b7";
var instanceType = "t2.micro";
var launchTemplateName = "AutoScaleLaunchTemplate";

// The name of the Auto Scaling group.
var groupName = "AutoScaleExampleGroup";

// The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) service-linked role.
var serviceLinkedRoleARN = "<Enter Value>";

var client = new AmazonAutoScalingClient(RegionEndpoint.USEast2);

Console.WriteLine("Auto Scaling Basics");
DisplayDescription();

// Create the launch template and save the template Id to use when deleting the
// launch template at the end of the application.
var launchTemplateId = await EC2Methods.CreateLaunchTemplateAsync(imageId, instanceType, launchTemplateName);

// Confirm that the template was created by asking for a description of it.
await EC2Methods.DescribeLaunchTemplateAsync(launchTemplateName);

PressEnter();

Console.WriteLine($"--- Creating an Auto Scaling group named {groupName}. ---");
var success = await AutoScaleMethods.CreateAutoScalingGroup(
    client,
    groupName,
    launchTemplateName,
    serviceLinkedRoleARN);

// Keep checking the details of the new group until its lifecycle state
// is "InService".
Console.WriteLine($"Waiting for the Auto Scaling group to be active.");

List<AutoScalingInstanceDetails> instanceDetails;

do
{
    instanceDetails = await AutoScaleMethods.DescribeAutoScalingInstancesAsync(client, groupName);
}
while (instanceDetails.Count <= 0);

Console.WriteLine($"Auto scaling group {groupName} successfully created.");
Console.WriteLine($"{instanceDetails.Count} instances were created for the group.");

// Display the details of the Auto Scaling group.
instanceDetails.ForEach(detail =>
{
    Console.WriteLine($"Group name: {detail.AutoScalingGroupName}");
});

PressEnter();

Console.WriteLine($"\n--- Enable metrics collection for {groupName}");
await AutoScaleMethods.EnableMetricsCollectionAsync(client, groupName);

// Show the metrics that are collected for the group.

// Update the maximum size of the group to three instances.
Console.WriteLine("--- Update the Auto Scaling group to increase max size to 3 ---");
int maxSize = 3;
await AutoScaleMethods.UpdateAutoScalingGroupAsync(client, groupName, launchTemplateName, serviceLinkedRoleARN, maxSize);

Console.WriteLine("--- Describe all Auto Scaling groups to show the current state of the group ---");
var groups = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(client, groupName);

DisplayGroupDetails(groups);

PressEnter();

Console.WriteLine("--- Describe account limits ---");
await AutoScaleMethods.DescribeAccountLimitsAsync(client);

Console.WriteLine("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
System.Threading.Thread.Sleep(60000);

Console.WriteLine("--- Set desired capacity to 2 ---");
int desiredCapacity = 2;
await AutoScaleMethods.SetDesiredCapacityAsync(client, groupName, desiredCapacity);

Console.WriteLine("--- Get the two instance Id values and state ---");

// Empty the group before getting the details again.
groups.Clear();
groups = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(client, groupName);
if (groups is not null)
{
    foreach (AutoScalingGroup group in groups)
    {
        Console.WriteLine($"The group name is {group.AutoScalingGroupName}");
        Console.WriteLine($"The group ARN is {group.AutoScalingGroupARN}");
        var instances = group.Instances;
        foreach (Instance instance in instances)
        {
            Console.WriteLine($"The instance id is {instance.InstanceId}");
            Console.WriteLine($"The lifecycle state is {instance.LifecycleState}");
        }
    }
}

Console.WriteLine("**** List the scaling activities that have occurred for the group");
var activities = await AutoScaleMethods.DescribeAutoScalingActivitiesAsync(client, groupName);
if (activities is not null)
{
    activities.ForEach(activity =>
    {
        Console.WriteLine($"The activity Id is {activity.ActivityId}");
        Console.WriteLine($"The activity details are {activity.Details}");
    });
}

// Display the Amazon CloudWatch metrics that have been collected.
var metrics = await CloudWatchMethods.GetCloudWatchMetricsAsync(groupName);
Console.WriteLine($"Metrics collected for {groupName}:");
metrics.ForEach(metric =>
{
    Console.Write($"Metric name: {metric.MetricName}\t");
    Console.WriteLine($"Namespace: {metric.Namespace}");
});

var dataPoints = await CloudWatchMethods.GetMetricStatisticsAsync(groupName);
Console.WriteLine("Details for the metrics collected:");
dataPoints.ForEach(detail =>
{
    Console.WriteLine(detail);
});

// Disable metrics collection.
Console.WriteLine("Disabling the collection of metrics for {groupName}.");
success = await AutoScaleMethods.DisableMetricsCollectionAsync(client, groupName);

if (success)
{
    Console.WriteLine($"Successfully stopped metrics collection for {groupName}.");
}
else
{
    Console.WriteLine($"Could not stop metrics collection for {groupName}.");
}

// Terminate all instances in the group.
Console.WriteLine("--- Now terminating all instances in the AWS Auto Scaling group ---");

if (groups is not null)
{
    groups.ForEach(group =>
    {
        // Only delete instances in the AutoScaling group we created.
        if (group.AutoScalingGroupName == groupName)
        {
            group.Instances.ForEach(async instance =>
            {
                await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(client, instance.InstanceId);
            });
        }
    });
}

// After all instances are terminated, delete the group.
Console.WriteLine("--- Deleting the Auto Scaling group ---");
await AutoScaleMethods.DeleteAutoScalingGroupAsync(client, groupName);

// Delete the launch template.
var deletedLaunchTemplateName = await EC2Methods.DeleteLaunchTemplateAsync(launchTemplateId);

if (deletedLaunchTemplateName == launchTemplateName)
{
    Console.WriteLine("Successfully deleted the launch template.");
}

Console.WriteLine("The demo is now concluded.");

void DisplayDescription()
{
    Console.WriteLine("This code example performs the following operations:");
    Console.WriteLine(" 1. Creates an Amazon EC2 launch template.");
    Console.WriteLine(" 2. Creates an Auto Scaling group.");
    Console.WriteLine(" 3. Shows the details of the new Auto Scaling group");
    Console.WriteLine("    to show that only one instance was created.");
    Console.WriteLine(" 4. Enables metrics collection.");
    Console.WriteLine(" 5. Updates the AWS Auto Scaling group to increase the");
    Console.WriteLine("    capacity to three.");
    Console.WriteLine(" 6. Describes Auto Scaling groups again to show the");
    Console.WriteLine("    current state of the group.");
    Console.WriteLine(" 7. Changes the desired capacity of the Auto Scaling");
    Console.WriteLine("    group to use an additional instance.");
    Console.WriteLine(" 8. Shows that there are now instances in the group.");
    Console.WriteLine(" 9. Lists the scaling activities that have occurred for the group.");
    Console.WriteLine("10. Displays the Amazon CloudWatch metrics that have");
    Console.WriteLine("    been collected.");
    Console.WriteLine("11. Disables metrics collection.");
    Console.WriteLine("12. Terminates all instances in the Auto Scaling group.");
    Console.WriteLine("13. Deletes the Auto Scaling group.");
    Console.WriteLine("14. Deletes the Amazon EC2 launch template.");
}

void DisplayGroupDetails(List<AutoScalingGroup> groups)
{
    if (groups is null)
        return;

    groups.ForEach(group =>
    {
        Console.WriteLine($"Group name:\t{group.AutoScalingGroupName}");
        Console.WriteLine($"Group created:\t{group.CreatedTime}");
        Console.WriteLine($"Maximum number of instances:\t{group.MaxSize}");
        Console.WriteLine($"Desired number of instances:\t{group.DesiredCapacity}");
    });
}

void PressEnter()
{
    Console.WriteLine("Press <Enter> to continue.");
    _ = Console.ReadLine();
    Console.WriteLine("\n\n");
}

// snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.main]
