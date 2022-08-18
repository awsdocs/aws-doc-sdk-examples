// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.VisualStudio.TestTools.UnitTesting;
using AutoScale_Basics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.AutoScaling;
using Amazon.AutoScaling.Model;

namespace AutoScale_Basics.Tests
{
    [TestClass()]
    public class AutoScaleMethodsTests
    {
        private string _GroupName = "test-group-name";
        private AmazonAutoScalingClient _Client = new AmazonAutoScalingClient();

        // the Amazon Resource Name (ARN) of the IAM service linked role.
        private readonly string _ServiceLinkedRoleArn = "<Enter Value>";

        private readonly string _LaunchTemplateName = "AutoScaleLaunchTemplateTest";

        [TestMethod()]
        public async Task DeleteAutoScalingGroupTest()
        {
            var success = await AutoScaleMethods.DeleteAutoScalingGroupAsync(_Client, _GroupName);
            Assert.IsTrue(success, "Could not delete the list.");
        }

        [TestMethod()]
        public async Task DeleteAutoScalingGroupGroupNonexistentTest()
        {
            var success = await AutoScaleMethods.DeleteAutoScalingGroupAsync(_Client, "NonExistentSite");
            Assert.IsFalse(success, "Could not delete the list.");
        }

        [TestMethod()]
        public async Task TerminateInstanceInAutoScalingGroupTest()
        {
            var instanceId = string.Empty;
            var success = await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(_Client, instanceId);
            Assert.IsTrue(success, "Could not terminate the instance.");
        }

        [TestMethod()]
        public async Task TerminateInstanceInAutoScalingGroupNonexistentTest()
        {
            var instanceId = string.Empty;
            var success = await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(_Client, instanceId);
            Assert.IsFalse(success, "Terminated the instance.");
        }

        [TestMethod()]
        public async Task DescribeScalingActivitiesTest()
        {
            var activities = await AutoScaleMethods.DescribeAuotoScalingActivitiesAsync(_Client, _GroupName);
            Assert.IsTrue(activities.Count > 0, "Can't find any auto scaling activities for the group.");
        }

        [TestMethod()]
        public async Task SetDesiredCapacityTest()
        {
            var success = await AutoScaleMethods.SetDesiredCapacityAsync(_Client, _GroupName, 3);
            Assert.IsTrue(success, "Couln't set the desired capacity.");
        }

        [TestMethod()]
        public async Task DescribeAccountLimitsTest()
        {
            await AutoScaleMethods.DescribeAccountLimitsAsync(_Client);
        }

        [TestMethod()]
        public async Task DescribeAutoScalingGroupsTest()
        {
            var details = await AutoScaleMethods.DescribeAutoScalingGroupsAsync(_Client, _GroupName);
            Assert.IsTrue(details.Count > 0, "Couldn't find that Auto Scaling group.");
        }

        [TestMethod()]
        public async Task EnableMetricsCollectionTest()
        {
            var success = await AutoScaleMethods.EnableMetricsCollectionAsync(_Client, _GroupName);
            Assert.IsTrue(success, $"Couldn't enable metrics collection for {_GroupName}.");
        }

        [TestMethod()]
        public async Task UpdateAutoScalingGroupTest()
        {
            var success = await AutoScaleMethods.UpdateAutoScalingGroupAsync(_Client, _GroupName, _LaunchTemplateName, _ServiceLinkedRoleArn, 3);
            Assert.IsTrue(success, "Couldn't update the Auto Scaling group: {_GroupName}.");
        }

        [TestMethod()]
        public async Task CreateAutoScalingGroupTest()
        {
            var success = await AutoScaleMethods.CreateAutoScalingGroup(_Client, _GroupName, _LaunchTemplateName, _ServiceLinkedRoleArn);
            Assert.IsTrue(success, "Couldn't create the Auto Scaling group.");
        }
    }
}