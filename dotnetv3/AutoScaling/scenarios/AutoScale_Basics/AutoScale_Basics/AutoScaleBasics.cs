// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
  Before running this SDK for .NET (v3) code example, set up your development
  environment, including your credentials.

  For more information, see the following documentation:

  https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html

  This example creates an Amazon Elastic Compute Cloud (Amazon EC2) launch
  template. You can also use the Amazon EC2 console. For more information, see
  the following:

  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template

 This code example performs the following operations:
  1. Creates an Amazon EC2 Launch Template
  2. Creates an AWS Auto Scaling group.
  3. Shows the details of the new AWS Auto Scaling group to show that only
     one instance was created.
  4. Enables metrics collection.
  5. Updates the AWS Auto Scaling group to increase the max size to three.
  6. Describes AWS Auto Scaling groups again to show the current state of the
     group.
  7. Changes the desired capacity of the AWS Auto Scaling group to two.
  9. Retrieves the details of the group group and shows the number of instances.
 10. Lists the scaling activities that have occurred for the group.
 11. Displays the Amazon CloudWatch metrics that have been collected.
 12. Terminates an instance in the Auto Scaling group.
 13. Disables metrics collection.
 13. Deletes the Auto Scaling group.
 14. Deletes the EC2 launch template.
*/

// snippet-start:[AutoScale.dotnetv3.AutoScale_Basics.main]
var imageId = "ami-0ca285d4c2cda3300";
var instanceType = "t1.micro";
var launchTemplateName = "AutoScaleLaunchTemplate";

// The name of the Auto Scaling group.
var groupName = "AutoScaleExampleGroup";

// the Amazon Resource Name (ARN) of the service linked IAM role.
var serviceLinkedRoleARN = "<Enter Value>";

// The subnet Id for a virtual service cloud (VPC) where instances in the
// autoscaling group can be created.
string vpcZoneId = "autoscale-basics";

var client = new AmazonAutoScalingClient(RegionEndpoint.USEast1);

// Start by creating the and save launch template Id to use when deleting the launch template at
// the end of the application.
var launchTemplateId = await EC2Methods.CreateLaunchTemplateAsync(imageId, instanceType, launchTemplateName);

Console.WriteLine($"--- Create an Auto Scaling group named {groupName}. ---");
var success = await AutoScaleMethods.CreateAutoScalingGroup(
    client,
    groupName,
    launchTemplateName,
    serviceLinkedRoleARN,
    vpcZoneId);

// Keep checking the details of the new group until its lifecycle state
// is "InService"
Console.WriteLine($"Waiting for the Auto Scaling group to be active.");

List<AutoScalingInstanceDetails> details;

do
{
    details = await AutoScaleMethods.DescribeAutoScalingInstancesAsync(client, groupName);
}
while (details[0].LifecycleState != "InService");

Console.WriteLine($"Auto scaling group {groupName} successfully created.");
Console.WriteLine($"{details.Count} instances were created for the group.");

// Display the details of the AWS Auto Scaling group.
details.ForEach(detail =>
{
    Console.WriteLine($"Group name: {detail.AutoScalingGroupName}");
});

Console.WriteLine($"--- Enable metrics collection for {groupName}");
await AutoScaleMethods.EnableMetricsCollectionAsync(client, groupName);

// Show the metrics that are collected for the group.

// Now update the group to allow up to 3 instances to be created for the
// auto scaling group.
Console.WriteLine("--- Update the Auto Scaling group to increase max size to 3 ---");
int maxSize = 3;
await AutoScaleMethods.UpdateAutoScalingGroupAsync(client, groupName, launchTemplateName, serviceLinkedRoleARN, maxSize);

Console.WriteLine("--- Describe all Auto Scaling groups to show the current state of the group ---");
var groups = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(client, groupName);

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
var activities = await AutoScaleMethods.DescribeAuotoScalingActivitiesAsync(client, groupName);
if (activities is not null)
{
    activities.ForEach(activity =>
    {
        Console.WriteLine($"The activity Id is {activity.ActivityId}");
        Console.WriteLine($"The activity details are {activity.Details}");
    });
}

// Display the Amazon CloudWatch metrics that have been collected.

// Disable metrics collection.

// Terminate all instances in the group.
Console.WriteLine("--- Terminating all instances in the AWS Auto Scaling group ---");
groups.ForEach(group =>
{
    // Only delete groups in the AWS AutoScaling group we created.
    if (group.AutoScalingGroupName == groupName)
    {
        group.Instances.ForEach(async instance =>
        {
            await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(client, instance.InstanceId);
        });
    }
});

// Once all instances have been terminated, delete the group.
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
    Console.WriteLine(" 1. Create an Amazon EC2 launch template.");
    Console.WriteLine(" 2. Create an AWS Auto Scaling group.");
    Console.WriteLine(" 3. Shows the details of the new AWS Auto Scaling group");
    Console.WriteLine("    to show that only one instance was created.");
    Console.WriteLine(" 4. Enables  metrics collection.");
    Console.WriteLine(" 5. Updatings the AWS Auto Scaling group to increase the");
    Console.WriteLine("    capacity to three.");
    Console.WriteLine(" 6. Describes AWS Auto Scaling groups again to show the");
    Console.WriteLine("    current state of the group.");
    Console.WriteLine(" 7. Changes the desired capacity of the AWS Auto Scaling");
    Console.WriteLine("    group to use an additional instance.");
    Console.WriteLine(" 8. Shows that there are now instances in the group.");
    Console.WriteLine(" 9. Lists the scaling activities that have occurred for the group.");
    Console.WriteLine("10. Displays the Amazon CloudWatch metrics that have");
    Console.WriteLine("    been collected.");
    Console.WriteLine("11. Disables metrics collection.");
    Console.WriteLine("12. Terminates all instances in the AWS Auto Scaling group.");
    Console.WriteLine("13. Deletes the Auto Scaling group.}");
    Console.WriteLine("14. Delete the Amazon EC2 launch template.");
}

void DisplayGroupDetails(List<AutoScalingGroup> groups)
{
    groups.ForEach(group =>
    {
        Console.WriteLine($"Group name:\t{group.AutoScalingGroupName}");
        Console.WriteLine($"Group created:\t{group.CreatedTime}");
        Console.WriteLine($"Maximum number of instances:\t{group.MaxSize}");
        Console.WriteLine($"Desired number of instances:\t{group.DesiredCapacity}");
    });
}

// snippet-end:[AutoScale.dotnetv3.AutoScale_Basics.main]
