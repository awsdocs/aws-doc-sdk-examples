using System;
using System.Threading.Tasks;
using Xunit;

namespace AssumeRoleExampleTest
{
    public class AssumeRoleTests
    {
        [Fact]
        public async Task GetCallerIdentityAsyncTest()
        {
            // Define variable

            // Create the moq

            // Call the method
            var response = GetCallerIdentityResponseAsync(client: client, request: getCallerIdReq)

            // Examine the results
        }

        [Fact]
        public async Task AssumeRoleAsyncTest()
        {
            // Define variables

            // Create the moq client

            // Call the method
            var response = await AssumeRoleAsync();

            // Examine the results
        }
    }
}
