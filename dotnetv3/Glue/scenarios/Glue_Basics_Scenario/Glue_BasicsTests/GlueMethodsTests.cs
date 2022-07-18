using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Glue_Basics.Tests
{
    using Amazon.Glue;

    [TestClass()]
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

        private readonly AmazonGlueClient _Client;

        public GlueMethodsTests()
        {
            _Client = new AmazonGlueClient();
        }

        [TestMethod()]
        public async Task CreateDatabaseAsyncTest()
        {
            var success = await GlueMethods.CreateDatabaseAsync(_Client, dbName, locationUri);
            Assert.IsTrue(success, "Could not create the database.");
        }

        [TestMethod()]
        public async Task CreateGlueCrawlerAsyncTest()
        {
            var success = await GlueMethods.CreateGlueCrawlerAsync(
                _Client, iam, s3Path, cron, dbName, crawlerName);
            Assert.IsTrue(success, "Could not create the crawler.");
        }

        [TestMethod()]
        public async Task CreateJobAsyncTest()
        {
            var success = await GlueMethods.CreateJobAsync(_Client, jobName, iam, scriptLocation);
            Assert.IsTrue(success, "Could not create the AWS Glue job.");
        }

        [TestMethod()]
        public async Task DeleteSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.DeleteSpecificCrawlerAsync(_Client, crawlerName);
            Assert.IsTrue(success, $"Could not delete the AWS Glue crawler {crawlerName}.");
        }

        [TestMethod()]
        public async Task DeleteDatabaseAsyncTest()
        {
            var success = await GlueMethods.DeleteDatabaseAsync(_Client, dbName);
            Assert.IsTrue(success, $"Could not delete {dbName}.");
        }

        [TestMethod()]
        public async Task DeleteJobAsyncTest()
        {
            var success = await GlueMethods.DeleteJobAsync(_Client, jobName);
            Assert.IsTrue(success, $"Could not delete the job, {jobName}.");
        }

        [TestMethod()]
        public async Task GetAllJobsAsyncTest()
        {
            var success = await GlueMethods.GetAllJobsAsync(_Client);
            Assert.IsTrue(success, "Could not get any AWS Glue job information.");
        }

        [TestMethod()]
        public async Task GetGlueTablesAsyncTest()
        {
            var success = await GlueMethods.GetGlueTablesAsync(_Client, dbName);
            Assert.IsTrue(success, $"Couldn't get information for any tables in {dbName}.");
        }

        [TestMethod()]
        public async Task GetJobRunsAsyncTest()
        {
            var success = await GlueMethods.GetJobRunsAsync(_Client, jobName);
            Assert.IsTrue(success, $"No information for job runs for {jobName}.");
        }

        [TestMethod()]
        public async Task GetSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.GetSpecificCrawlerAsync(_Client, crawlerName);
            Assert.IsTrue(success, $"Couldn't fimd any information about {crawlerName}");
        }

        [TestMethod()]
        public async Task GetSpecificDatabaseAsyncTest()
        {
            var success = await GlueMethods.GetSpecificDatabaseAsync(_Client, dbName);
            Assert.IsTrue(success, $"Couldn't find any information about {dbName}.");
        }

        [TestMethod()]
        public async Task StartJobAsyncTest()
        {
            var success = await GlueMethods.StartJobAsync(_Client, jobName);
            Assert.IsTrue(success, $"Couldn't start job: {jobName}.");
        }

        [TestMethod()]
        public async Task StartSpecificCrawlerAsyncTest()
        {
            var success = await GlueMethods.StartSpecificCrawlerAsync(_Client, crawlerName);
            Assert.IsTrue(success, $"Couldn't start crawler, {crawlerName}");
        }
    }
}