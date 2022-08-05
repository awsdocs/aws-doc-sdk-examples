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
        // the Amazon Resource Name (ARN) of the service linked IAM role.
        // var serviceLinkedRoleARN = "<Enter Value>";
        private string serviceLinkedRoleARN = "arn:aws:iam::704825161248:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling_Basics";

        // The subnet Id for a virtual service cloud (VPC) where instances in the
        // autoscaling group can be created.
        private string vpcZoneId = "autoscale-basics";

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
        public async void TerminateInstanceInAutoScalingGroupTest()
        {
            var instanceId = string.Empty;
            var success = await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(_Client, instanceId);
            Assert.IsTrue(success, "Could not terminate the instance.");
        }

        [TestMethod()]
        public async void TerminateInstanceInAutoScalingGroupNonexistentTest()
        {
            var instanceId = string.Empty;
            var success = await AutoScaleMethods.TerminateInstanceInAutoScalingGroupAsync(_Client, instanceId);
            Assert.IsFalse(success, "Terminated the instance.");
        }

        [TestMethod()]
        public void DescribeScalingActivitiesTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void GetAutoScalingGroupsTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void SetDesiredCapacityTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void DescribeAccountLimitsTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void DescribeAutoScalingGroupsTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void EnableMetricsCollectionTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void DescribeAutoScalingInstanceTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void GetSpecificAutoScalingGroupsTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void UpdateAutoScalingGroupTest()
        {
            Assert.Fail();
        }

        [TestMethod()]
        public void CreateAutoScalingGroupTest()
        {
            
        }
    }
}