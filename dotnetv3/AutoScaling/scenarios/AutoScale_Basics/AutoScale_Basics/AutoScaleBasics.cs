// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
  Before running this SDK for .NET (v3) code example, set up your development
  environment, including your credentials.

  For more information, see the following documentation:

  https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html

  In addition, create a launch template. For more information, see the following topic:

  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template

 This code example performs the following operations:
  1. Create an EC2 Launch Template
  2. Create an Auto Scaling group.
  3. Get a specific Auto Scaling group and returns an instance Id value.
  4. Describes Auto Scaling with the Id value.
  5. Enables metrics collection.
  6. Describes Auto Scaling groups again.
  7. Describes Account details.
  8. Updates an Auto Scaling group to use an additional instance.
  9. Gets the specific Auto Scaling group and gets the number of instances.
 10. List the scaling activities that have occurred for the group.
 11. Terminates an instance in the Auto Scaling group.
 12. Stops the metrics collection.
 13. Deletes the Auto Scaling group.
 14. Deletes the EC2 launch template.
*/

// snippet-start:[autoscale.dotnetv3.create_scaling_scenario.main]

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

Console.WriteLine("--- Describe all Auto Scaling groups to show the current state of the groups ---");
var groups = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(client, groupName);
groups.ForEach(group =>
{
    Console.WriteLine($"Group name:\t{group.AutoScalingGroupName}");
    Console.WriteLine($"Group created:\t{group.CreatedTime}");
    Console.WriteLine($"Maximum number of instances:\t{group.MaxSize}");
    Console.WriteLine($"Desired number of instances:\t{group.DesiredCapacity}");
});

Console.WriteLine("--- Describe account limits ---");
await AutoScaleMethods.DescribeAccountLimitsAsync(client);

Console.WriteLine("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
System.Threading.Thread.Sleep(60000);

Console.WriteLine("--- Set desired capacity to 2 ---");
int desiredCapacity = 2;
await AutoScaleMethods.SetDesiredCapacityAsync(client, groupName, desiredCapacity);

Console.WriteLine("--- Get the two instance Id values and state ---");
var groups = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(client, groupName);
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
    foreach (Activity activity in activities)
    {
        Console.WriteLine($"The activity Id is {activity.ActivityId}");
        Console.WriteLine($"The activity details are {activity.Details}");
    }
}

Console.WriteLine("--- Terminate an instance in the Auto Scaling group ---");
await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(client, instanceId);

Console.WriteLine("**** Delete the Auto Scaling group");
await AutoScaleMethods.DeleteAutoScalingGroupAsync(client, groupName);

void DisplayDescription()
{
    Console.WriteLine("This code example performs the following operations:");
    Console.WriteLine(" 1. Create an Amazon EC2 launch template.");
    Console.WriteLine(" 2. Create an Auto Scaling group.");
    Console.WriteLine(" 3. Gets a specific Auto Scaling group and returns an instance Id value.");
    Console.WriteLine(" 4. Describes Auto Scaling with the Id value.");
    Console.WriteLine(" 5. Enables metrics collection.");
    Console.WriteLine(" 6. Describes Auto Scaling groups.");
    Console.WriteLine(" 7. Describes Account details.");
    Console.WriteLine(" 8. Updates an Auto Scaling group to use an additional instance.");
    Console.WriteLine(" 9. Gets the specific Auto Scaling group and gets the number of instances.");
    Console.WriteLine("10. List the scaling activities that have occurred for the group.");
    Console.WriteLine("11. Terminates an instance in the Auto Scaling group.");
    Console.WriteLine("12. Stops the metrics collection.");
    Console.WriteLine("13. Deletes the Auto Scaling group.}");
    Console.WriteLine("14. Delete the Amazon EC2 launch template.");
}

// snippet-end:[autoscale.dotnetv3.create_scaling_scenario.main]
