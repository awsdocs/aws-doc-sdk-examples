using AutoScale_Basics;
using Amazon.EC2;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace AutoScale_Basics.Tests
{
    [TestClass()]
    public class EC2MethodsTests
    {
        private static readonly string imageId = "ami-0ca285d4c2cda3300";
        private static readonly string instanceType = "t1.micro";
        private static readonly string launchTemplateName = "AutoScaleLaunchTemplateTest";
        private static string launchTemplateId = string.Empty;

        [TestMethod()]
        public async Task CreateLaunchTemplateAsyncTest()
        {
            launchTemplateId = await EC2Methods.CreateLaunchTemplateAsync(imageId, instanceType, launchTemplateName);
            Assert.IsTrue(launchTemplateId != String.Empty, "Couldn't create launch template.");
        }

        [TestMethod()]
        public async Task DeleteLaunchTemplateAsyncTest()
        {
            var deletedLaunchTemplateName = await EC2Methods.DeleteLaunchTemplateAsync(launchTemplateId);
            Assert.IsTrue(deletedLaunchTemplateName == launchTemplateName, "Could not delete the launch template.");
        }

        [TestMethod()]
        public void DescribeLaunchTemplateAsyncTest()
        {
            Assert.Fail();
        }
    }
}