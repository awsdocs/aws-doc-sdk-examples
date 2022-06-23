using Xunit;
using DescribeInstancesExample;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.EC2;
using Moq;
using Amazon.EC2.Model;
using System.Threading;

namespace DescribeInstancesExample.Tests
{
    public class DescribeInstancesTests
    {
        public AmazonEC2Client CreateMockEC2Client()
        {
            var mockEC2Client = new Mock<AmazonEC2Client>();
            mockEC2Client.Setup(client => client.Paginators.DescribeInstances(
                It.IsAny<DescribeInstancesRequest>()
            )).Returns((DescribeInstancesRequest r, CancellationToken token) =>
            {
                var instances = new List<Instance>()
                    {
                        new Instance
                        {
                            InstanceId = "blah blah",
                        },
                        new Instance
                        {
                            InstanceId = "blah blah",
                        },
                        new Instance
                        {
                            InstanceId = "blah blah",
                        },
                    };

                var reservations = new List<Reservation>()
                {
                    new Reservation()
                    {
                        Instances = instances,
                    }                    
                };

                var responses = new List<DescribeInstancesResponse>()
                {
                    new DescribeInstancesResponse()
                    {
                        Reservations = reservations,
                    }
                };

                return Task.FromResult(new DescribeInstancesPaginator()
                {
                    Responses = responses,
                });
            });

            return mockEC2Client.Object;
        }

        [Fact()]
        public void GetInstanceDescriptionsTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public async Task GetInstanceDescriptionsFilteredTest()
        {
            var client = CreateMockEC2Client();

            string tagName = "IncludeInList";
            string tagValue = "Yes";

            await DescribeInstances.GetInstanceDescriptionsFiltered(client, tagName, tagValue);
        }
    }
}