// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
  Before running this SDK for .NET (v3) code example, set up your development environment, including your credentials.

  For more information, see the following documentation:

  https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html

  In addition, create a launch template. For more information, see the following topic:

  https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-launch-templates.html#create-launch-template

 This code example performs the following operations:
 1. Creates an Auto Scaling group.
 2. Gets a specific Auto Scaling group and returns an instance Id value.
 3. Describes Auto Scaling with the Id value.
 4. Enables metrics collection.
 5. Describes Auto Scaling groups.
 6. Describes Account details.
 7. Updates an Auto Scaling group to use an additional instance.
 8. Gets the specific Auto Scaling group and gets the number of instances.
 9. List the scaling activities that have occurred for the group.
 10. Terminates an instance in the Auto Scaling group.
 11. Stops the metrics collection.
 12. Deletes the Auto Scaling group.
*/

// snippet-start:[autoscale.dotnetv3.create_scaling_scenario.main]
/*
    Set the following variables:

    groupName - The name of the Auto Scaling group.\n" +
    launchTemplateName - The name of the launch template. \n" +
    serviceLinkedRoleARN - The Amazon Resource Name (ARN) of the service-linked role that the Auto Scaling group uses.\n" +
    vpcZoneId - A subnet Id for a virtual private cloud (VPC) where instances in the Auto Scaling group can be created.\n" ;
*/

using AutoScale_Basics;

string groupName = "<Enter Value>";
string launchTemplateName = "<Enter Value>";
string serviceLinkedRoleARN = "<Enter Value>";
string vpcZoneId = "<Enter Value>";
var autoScalingClient = new AmazonAutoScalingClient(RegionEndpoint.USEast1);

Console.WriteLine("**** Create an Auto Scaling group named " + groupName);
await AutoScaleMethods.CreateAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId);

Console.WriteLine("Wait 2 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
System.Threading.Thread.Sleep(120000);

Console.WriteLine("**** Get Auto Scale group Id value");
var instanceId = await AutoScaleMethods.GetSpecificAutoScalingGroups(autoScalingClient, groupName);

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
await AutoScaleMethods.DescribeAutoScalingInstance(autoScalingClient, instanceId);

Console.WriteLine("**** Enable metrics collection " + instanceId);
await AutoScaleMethods.EnableMetricsCollection(autoScalingClient, groupName);

Console.WriteLine("**** Update an Auto Scaling group to update max size to 3");
await AutoScaleMethods.UpdateAutoScalingGroup(autoScalingClient, groupName, launchTemplateName, serviceLinkedRoleARN);

Console.WriteLine("**** Describe all Auto Scaling groups to show the current state of the groups");
await AutoScaleMethods.DescribeAutoScalingGroups(autoScalingClient, groupName);

Console.WriteLine("**** Describe account details");
await AutoScaleMethods.DescribeAccountLimits(autoScalingClient);

Console.WriteLine("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned");
System.Threading.Thread.Sleep(60000);

Console.WriteLine("**** Set desired capacity to 2");
await AutoScaleMethods.SetDesiredCapacity(autoScalingClient, groupName);

Console.WriteLine("**** Get the two instance Id values and state");
await AutoScaleMethods.GetAutoScalingGroups(autoScalingClient, groupName);

Console.WriteLine("**** List the scaling activities that have occurred for the group");
await AutoScaleMethods.DescribeScalingActivities(autoScalingClient, groupName);

Console.WriteLine("**** Terminate an instance in the Auto Scaling group");
await AutoScaleMethods.TerminateInstanceInAutoScalingGroup(autoScalingClient, instanceId);

Console.WriteLine("**** Delete the Auto Scaling group");
await AutoScaleMethods.DeleteAutoScalingGroup(autoScalingClient, groupName);

// snippet-end:[autoscale.dotnetv3.create_scaling_scenario.main]
