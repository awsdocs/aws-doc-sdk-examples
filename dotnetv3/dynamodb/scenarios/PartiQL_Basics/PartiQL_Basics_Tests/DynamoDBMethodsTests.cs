using Xunit;
using DynamoDB_PartiQL_Basics_Scenario;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;

namespace DynamoDB_PartiQL_Basics_Scenario.Tests
{
    public class DynamoDBMethodsTests
    {
        private static readonly AmazonDynamoDBClient Client = new AmazonDynamoDBClient();
        const string tableName = "test_movie_table";

        [Fact()]
        public async Task CreateMovieTableAsyncTest()
        {
            var success = await DynamoDBMethods.CreateMovieTableAsync(tableName);
            Assert.True(success, $"Could not create table {tableName}.");
        }

        [Fact()]
        public async Task DeleteTableAsyncTest()
        {
            var success = await DynamoDBMethods.DeleteTableAsync(tableName);
            Assert.True(success, $"Could not delete table {tableName}.");
        }
    }
}