// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Glue;
using Amazon.Glue.Model;
using GlueActions;
using Microsoft.Extensions.Configuration;

namespace GlueTests
{
    public class GlueTests
    {
        private readonly IConfiguration _configuration;
        private readonly GlueWrapper _wrapper;
        private readonly IAmazonGlue _glueClient;
        private readonly string _bucketName;
        private readonly string _bucketUrl;
        private readonly string _crawlerName;
        private readonly string _roleName;
        private readonly string _sourceData;
        private readonly string _dbName;
        private readonly string _cron;
        private readonly string _scriptUrl;
        private readonly string _jobName;
        private readonly string _description;
        private static string _jobRunId;
        private static List<Table> _tables;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public GlueTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _glueClient = new AmazonGlueClient();
            _wrapper = new GlueWrapper(_glueClient);

            _bucketName = _configuration["BucketName"];
            _bucketUrl = _configuration["BucketUrl"];
            _crawlerName = _configuration["CrawlerName"];
            _roleName = _configuration["RoleName"];
            _sourceData = _configuration["SourceData"];
            _dbName = _configuration["DbName"];
            _cron = _configuration["Cron"];
            _scriptUrl = _configuration["ScriptURL"];
            _jobName = _configuration["JobName"];
            _description = "AWS Glue job created for testing.";
        }

        /// <summary>
        /// Test the creation of an AWS Glue crawler.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateCrawlerAsyncTest()
        {
            var success = await _wrapper.CreateCrawlerAsync(
                _crawlerName,
                "Glue crawler created for testing.",
                _roleName,
                _cron,
                _sourceData,
                _dbName);

            // Wait for crawler to be ready for use.
            CrawlerState crawlerState;
            do
            {
                crawlerState = await _wrapper.GetCrawlerStateAsync(_crawlerName);
            }
            while (crawlerState != "READY");

            Assert.True(success, "Could not create crawler.");
        }

        /// <summary>
        /// Test starting an AWS Glue crawler.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task StartCrawlerAsyncTest()
        {
            var success = await _wrapper.StartCrawlerAsync(_crawlerName);

            // Wait for the crawler to be started.
            CrawlerState crawlerState;
            do
            {
                crawlerState = await _wrapper.GetCrawlerStateAsync(_crawlerName);
            }
            while (crawlerState != "READY");

            Assert.True(success, "Could not start crawler.");
        }

        /// <summary>
        /// Test getting AWS Glue database information.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task GetDatabaseAsyncTest()
        {
            var database = await _wrapper.GetDatabaseAsync(_dbName);
            Assert.NotNull(database);
        }

        /// <summary>
        /// Test getting information about AWS Glue database tables.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task GetTablesAsyncTest()
        {
            _tables = await _wrapper.GetTablesAsync(_dbName);
            Assert.NotEmpty(_tables);
        }

        /// <summary>
        /// Test creating an AWS Glue job.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task CreateJobAsyncTest()
        {
            var success = await _wrapper.CreateJobAsync(_dbName, _tables[0].Name, _bucketUrl, _jobName, _roleName, _description, _scriptUrl);
            Assert.True(success, "Couldn't create job.");
        }

        /// <summary>
        /// Test starting an AWS Glue job run.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task StartJobRunAsyncTest()
        {
            _jobRunId = await _wrapper.StartJobRunAsync(_jobName, _dbName, _tables[0].Name, _bucketName);

            // Wait for the job run to complete or error out.
            var jobRunComplete = false;
            do
            {
                var jobRun = await _wrapper.GetJobRunAsync(_jobName, _jobRunId);
                if (jobRun.JobRunState == "SUCCEEDED" || jobRun.JobRunState == "STOPPED" ||
                    jobRun.JobRunState == "FAILED" || jobRun.JobRunState == "TIMEOUT")
                {
                    jobRunComplete = true;
                }
            } while (!jobRunComplete);
            Assert.True(jobRunComplete, "Could not complete the job run.");
        }

        /// <summary>
        /// Test getting a list of job run information.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task GetJobRunsAsyncTest()
        {
            var jobRuns = await _wrapper.GetJobRunsAsync(_jobName);
            Assert.NotNull(jobRuns);
        }

        [Fact]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task ListJobsAsyncTest()
        {
            var jobs = await _wrapper.ListJobsAsync();
            Assert.NotNull(jobs);
        }

        /// <summary>
        /// Test deleting an AWS Glue database.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task DeleteJobAsyncTest()
        {
            var success = await _wrapper.DeleteJobAsync(_jobName);
            Assert.True(success, "Could not delete the job.");
        }

        /// <summary>
        /// Test deleting an AWS Glue database table.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task DeleteTableAsyncTest()
        {
            var success = false;

            foreach (var table in _tables)
            {
                success = await _wrapper.DeleteTableAsync(_dbName, table.Name);
                Assert.True(success, $"Tried to delete table {table.Name} but couldn't.");
            }

            Assert.True(success, "Could not delete the tables.");
        }

        /// <summary>
        /// Test deleting an AWS Glue database.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task DeleteDatabaseAsyncTest()
        {
            var success = await _wrapper.DeleteDatabaseAsync(_dbName);
            Assert.True(success, "Could not delete database.");
        }

        /// <summary>
        /// Test deleting an AWS Glue crawler.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task DeleteCrawlerAsyncTest()
        {
            var success = await _wrapper.DeleteCrawlerAsync(_crawlerName);
            Assert.True(success, "Could not delete crawler.");
        }
    }
}