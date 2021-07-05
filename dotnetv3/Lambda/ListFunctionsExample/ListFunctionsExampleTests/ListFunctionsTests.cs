using Xunit;
using ListFunctionsExample;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.Lambda;
using Moq;

namespace ListFunctionsExample.Tests
{
    public class ListFunctionsTests
    {
        public Mock<AmazonLambdaClient> MockClient = new Mock<AmazonLambdaClient>();

        [Fact()]
        public void MainTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void ListFunctionsAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void ListFunctionsPaginatorAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void DisplayFunctionListTest()
        {
            Assert.True(false, "This test needs an implementation");
        }
    }
}