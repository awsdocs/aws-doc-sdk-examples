// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.AutoScaling;
using Amazon.AutoScaling.Model;

namespace AutoScalingTests
{
    public class AutoScalingWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly AmazonAutoScalingClient _autoScalingClient;
        private readonly AutoScalingWrapper _autoScalingWrapper;
        private readonly AmazonEC2Client _ec2Client;
        private readonly EC2Wrapper _ec2Wrapper;
        private readonly AmazonCloudWatchClient _cloudWatchClient;
        private readonly CloudWatchWrapper _cloudWatchWrapper;

        private static string? _groupName;
        private static string? _imageId;
        private static string? _instanceType;
        private static string? _launchTemplateName;

        private static string? _nameGuid;

        private static string? _launchTemplateId;
        private static List<AutoScalingGroup>? _groups;
        public static string? InstanceId;
        public static bool setup = false;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public AutoScalingWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            // Values needed for testing purposes.
            _autoScalingClient = new AmazonAutoScalingClient();
            _autoScalingWrapper = new AutoScalingWrapper(_autoScalingClient);
            _ec2Client = new AmazonEC2Client();
            _ec2Wrapper = new EC2Wrapper(_ec2Client);
            _cloudWatchClient = new AmazonCloudWatchClient();
            _cloudWatchWrapper = new CloudWatchWrapper(_cloudWatchClient);

            if (!setup)
            {
                _nameGuid = Guid.NewGuid().ToString();
                _imageId = _configuration["ImageId"];
                _instanceType = _configuration["InstanceType"];
                _launchTemplateName =
                    $"{_configuration["LaunchTemplateName"]}-{_nameGuid}";

                // The name of the Auto Scaling group.
                _groupName = $"{_configuration["GroupName"]}-{_nameGuid}";
                setup = true;
            }
        }

        /// <summary>
        /// Test the CreateLaunchTemplateAsync method. The call should return
        /// a LaunchTemplate Id.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateLaunchTemplateAsyncTest()
        {
            _launchTemplateId = await _ec2Wrapper.CreateLaunchTemplateAsync(_imageId!, _instanceType!, _launchTemplateName!);
            Assert.NotNull(_launchTemplateId);
        }

        /// <summary>
        /// Test the DescribeLaunchTemplateAsync method. The method should succeed.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task DescribeLaunchTemplateAsyncTest()
        {
            var success = await _ec2Wrapper.DescribeLaunchTemplateAsync(_launchTemplateName!);
            Assert.True(success);
        }

        /// <summary>
        /// Test the CreateAutoScalingGroupAsync method. The method call should
        /// create an Auto Scaling group and return true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task CreateAutoScalingGroupTest()
        {
            var availabilityZone =
                (await _ec2Wrapper.ListAvailabilityZonesAsync()).First().ZoneName;
            var success = await _autoScalingWrapper.CreateAutoScalingGroupAsync(_groupName!, _launchTemplateName!, availabilityZone);
            Assert.True(success, $"Couldn't create the Auto Scaling group {_groupName}.");
        }

        /// <summary>
        /// Test the DescribeAutoScalingInstancesAsync method. The call should
        /// return information about instances in the logging group and the
        /// value should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task DescribeAutoScalingInstancesAsyncTest()
        {
            var instanceDetails = await _autoScalingWrapper.DescribeAutoScalingInstancesAsync(_groupName!);
            Assert.NotNull(instanceDetails);
        }

        /// <summary>
        /// Test the EnableMetricsCollectionAsync method, which should succeed.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task EnableMetricsCollectionAsyncTest()
        {
            var success = await _autoScalingWrapper.EnableMetricsCollectionAsync(_groupName!);
            Assert.True(success, $"Couldn't enable metrics collection for {_groupName}.");
        }

        /// <summary>
        /// Test the SetDesiredCapacity method by sending a different
        /// value for maximum capacity. It confirms success by retrieving
        /// the list of Auto Scaling groups and checking the value of
        /// DesiredCapacity.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task SetDesiredCapacityAsyncTest()
        {
            var newMax = 3;
            var success = await _autoScalingWrapper.SetDesiredCapacityAsync(_groupName!, newMax);
            Assert.True(success, "Couldn't set the desired capacity.");
            _groups = await _autoScalingWrapper.DescribeAutoScalingGroupsAsync(_groupName!);
            _groups!.ForEach(group =>
            {
                if (group.AutoScalingGroupName == _groupName)
                {
                    Assert.Equal(newMax, group.DesiredCapacity);
                }
            });
        }

        /// <summary>
        /// Test the UpdateAutoScalingGroup method by changing the maximum
        /// capacity. After the change is made, the DescribeAutoScalingGroupsAsync
        /// method checks the capacity for the group.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task UpdateAutoScalingGroupTest()
        {
            var newMax = 3;
            var success = await _autoScalingWrapper.UpdateAutoScalingGroupAsync(_groupName!, _launchTemplateName!, newMax);
            Assert.True(success, $"Couldn't update the Auto Scaling group: {_groupName}.");
            _groups = await _autoScalingWrapper.DescribeAutoScalingGroupsAsync(_groupName!);
            _groups!.ForEach(group =>
            {
                if (group.AutoScalingGroupName == _groupName)
                {
                    Assert.Equal(newMax, group.DesiredCapacity);
                }
            });
        }

        /// <summary>
        /// Test the DescribeAutoScalingGroupsAsync method.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task DescribeAutoScalingGroupsAsyncTest()
        {
            var details = await _autoScalingWrapper.DescribeAutoScalingGroupsAsync(_groupName!);
            Assert.True(details!.Count > 0, $"Couldn't find that Auto Scaling group {_groupName}.");
        }

        /// <summary>
        /// Test the DescribeAutoScalingActivitiesAsync method. The method
        /// should return a non-null collection of activities.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task DescribeScalingActivitiesAsyncTest()
        {
            var activities = await _autoScalingWrapper.DescribeScalingActivitiesAsync(_groupName!);
            Assert.NotNull(activities);
        }

        /// <summary>
        /// Test the GetCloudWatchMetricsAsync method. The metrics collection
        /// should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task GetCloudWatchMetricsAsyncTest()
        {
            var metrics = await _cloudWatchWrapper.GetCloudWatchMetricsAsync(_groupName!);
            Assert.NotNull(metrics);
        }

        /// <summary>
        /// Test the GetMetricStatisticsAsync method. The data points
        /// collection should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task GetMetricStatisticsAsyncTest()
        {
            var dataPoints = await _cloudWatchWrapper.GetMetricStatisticsAsync(_groupName!);
            Assert.NotNull(dataPoints);
        }

        /// <summary>
        /// Test the DisableMetricsCollectionAsync method. The method should
        /// succeed.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task DisableMetricsCollectionAsyncTest()
        {
            var success = await _autoScalingWrapper.DisableMetricsCollectionAsync(_groupName!);
            Assert.True(success);
        }

        /// <summary>
        /// Test the TerminateInstanceAutoScalingGroupsAsync method to
        /// terminate all instances created during the test. It should
        /// be successful.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(13)]
        [Trait("Category", "Integration")]
        public async Task TerminateInstanceInAutoScalingGroupAsyncTest()
        {
            foreach (var group in _groups!)
            {
                if (group.AutoScalingGroupName == _groupName)
                {
                    foreach (Amazon.AutoScaling.Model.Instance instance in group.Instances)
                    {
                        var success = await _autoScalingWrapper.TerminateInstanceInAutoScalingGroupAsync(instance.InstanceId);
                        Assert.True(success, "Could not terminate the instance.");
                    }
                }
            }
        }

        /// <summary>
        /// Test the DeleteAutoScalingGroupAsync method to delete the group
        /// created for testing. The call should be successful.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(14)]
        [Trait("Category", "Integration")]
        public async Task DeleteAutoScalingGroupTest()
        {
            var success = await _autoScalingWrapper.DeleteAutoScalingGroupAsync(_groupName!);
            Assert.True(success, "Could not delete the group.");
        }

        /// <summary>
        /// Test the DeleteLaunchTemplateAsync method by deleting the template
        /// created for testing purposes. If successful, the value returned
        /// should be equal to the name of the launch template created by the
        /// test class.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(15)]
        [Trait("Category", "Integration")]
        public async Task DeleteLaunchTemplateAsyncTest()
        {
            var templateName = await _ec2Wrapper.DeleteLaunchTemplateAsync(_launchTemplateId!);
            Assert.Equal(_launchTemplateName, templateName);
        }

        /// <summary>
        /// Test the DescribeAccountLimitsAsync method. It doesn't matter when
        /// this runs, since it is retrieving information that should be
        /// available at any time.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Trait("Category", "Integration")]
        public async Task DescribeAccountLimitsTest()
        {
            var success = await _autoScalingWrapper.DescribeAccountLimitsAsync();
            Assert.True(success, "Couldn't retrieve account limits.");
        }
    }
}