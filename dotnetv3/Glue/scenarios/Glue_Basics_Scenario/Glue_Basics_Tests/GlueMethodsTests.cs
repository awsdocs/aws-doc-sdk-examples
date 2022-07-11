using Amazon.Glue;
using Glue_Basics;
using Xunit;

namespace Glue_Basics.Tests
{
    public class GlueMethodsTests
    {
        // Initialize the values we need for the scenario.
        // The ARN of the service role used by the crawler.
        readonly string iam = "arn:aws:iam::012345678901:role/AWSGlueServiceRole-CrawlerTutorial";

        // The path to the Amazon S3 bucket where the comma-delimited file is stored.
        readonly string s3Path = "s3://crawler-public-us-east-1/flight/2016/csv";
        readonly string cron = "cron(15 12 * * ? *)";

        // The name of the database used by the crawler.
        readonly string dbName = "test-flights-db";
        readonly string crawlerName = "Test Scenario Crawler";
        readonly string jobName = "glue-job-test";
        readonly string scriptLocation = "s3://aws-glue-scripts-012345678901-us-west-1/GlueDemoUser";
        readonly string locationUri = "s3://crawler-public-us-east-1/flight/2016/csv/";

        readonly AmazonGlueClient _Client;

        public GlueMethodsTests()
        {
            _Client = new AmazonGlueClient();
        }

        [Fact()]
        public async Task CreateDatabaseAsyncTest()
        {
            var success = await GlueMethods.CreateDatabaseAsync(_Client, dbName, locationUri);
            Assert.True(success, "Could not create the database.");
        }

        [Fact()]
        public async Task CreateGlueCrawlerAsyncTest()
        {
            var success = await GlueMethods.CreateGlueCrawlerAsync(
                _Client, iam, s3Path, cron, dbName, crawlerName);
            Assert.True(success, "Could not create the crawler.");
        }

        [Fact()]
        public async Task CreateJobAsyncTest()
        {
            var success = await GlueMethods.CreateJobAsync(_Client, jobName, iam, scriptLocation);
            Assert.True(success, "Could not create the AWS Glue job.");
        }

        [Fact()]
        public async Task DeleteSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.DeleteSpecificCrawlerAsync(_Client, crawlerName);
            Assert.True(success, $"Could not delete the AWS Glue crawler {crawlerName}.");
        }

        [Fact()]
        public async Task DeleteDatabaseAsyncTest()
        {
            var success = await GlueMethods.DeleteDatabaseAsync(_Client, dbName);
            Assert.True(success, $"Could not delete {dbName}.");
        }

        [Fact()]
        public async Task DeleteJobAsyncTest()
        {
            var success = await GlueMethods.DeleteJobAsync(_Client, jobName);
            Assert.True(success, $"Could not delete the job, {jobName}.");
        }

        [Fact()]
        public async Task GetAllJobsAsyncTest()
        {
            var success = await GlueMethods.GetAllJobsAsync(_Client);
            Assert.True(success, "Could not get any AWS Glue job information.");
        }

        [Fact()]
        public async Task GetGlueTablesAsyncTest()
        {
            var success = await GlueMethods.GetGlueTablesAsync(_Client, dbName);
            Assert.True(success, $"Couldn't get information for any tables in {dbName}.");
        }

        [Fact()]
        public async Task GetJobRunsAsyncTest()
        {
            var success = await GlueMethods.GetJobRunsAsync(_Client, jobName);
            Assert.True(success, $"No information for job runs for {jobName}.");
        }

        [Fact()]
        public async Task GetSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.GetSpecificCrawlerAsync(_Client, crawlerName);
            Assert.True(success, $"Couldn't fimd any information about {crawlerName}");
        }

        [Fact()]
        public async Task GetSpecificDatabaseAsyncTest()
        {
            var success = await GlueMethods.GetSpecificDatabaseAsync(_Client, dbName);
            Assert.True(success, $"Couldn't find any information about {dbName}.");
        }

        [Fact()]
        public async Task StartJobAsyncTest()
        {
            var success = await GlueMethods.StartJobAsync(_Client, jobName);
            Assert.True(success, $"Couldn't start job: {jobName}.");
        }

        [Fact()]
        public async Task StartSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.StartSpecificCrawlerAsync(_Client, crawlerName);
            Assert.True(success, $"Couldn't start crawler, {crawlerName}");
        }
    }
}