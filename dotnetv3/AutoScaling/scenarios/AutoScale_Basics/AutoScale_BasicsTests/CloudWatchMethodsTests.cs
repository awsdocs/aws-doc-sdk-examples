// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0 

using Microsoft.VisualStudio.TestTools.UnitTesting;
using AutoScale_Basics;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AutoScale_Basics.Tests
{
    [TestClass()]
    public class CloudWatchMethodsTests
    {
        private string _GroupName = "test-group-name";

        [TestMethod()]
        [TestProperty("Category", "Integration")]
        public void GetCloudWatchMetricsAsyncTest()
        {
            var metrics = CloudWatchMethods.GetCloudWatchMetricsAsync(_GroupName);
            Assert.IsNotNull(metrics, "No metrics were returned.");
        }

        [TestMethod()]
        [TestProperty("Category", "Integration")]
        public void GetMetricStatisticsAsyncTest()
        {
            var metricStatistics = CloudWatchMethods.GetMetricStatisticsAsync(_GroupName);
            Assert.IsNotNull(metricStatistics, "No statistics returned.");
        }
    }
}