// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Glue;
using Microsoft.Extensions.Configuration;

namespace GlueTests
{
    public class GlueTests
    {
        private readonly IConfiguration _configuration;
        private readonly GlueWrapper _wrapper;

        private readonly IAmazonGlue _glueClient;
        private string _bucketName;
        private string _crawlerName;
        private string _roleName;
        private string _sourceData;
        private string _dbName;
        private string _cron;
        private string _scriptUrl;
        private string _jobName;
        private static string _jobRunId;

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

            string _bucketName = _configuration["BucketName"];
            string _crawlerName = _configuration["CrawlerName"];
            string _roleName = _configuration["RoleName"];
            string _sourceData = _configuration["SourceData"];
            string _dbName = _configuration["DbName"];
            string _cron = _configuration["Cron"];
            string _scriptUrl = _configuration["ScriptURL"];
            string _jobName = _configuration["JobName"];
        }

        [Fact]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateCrawlerAsyncTest()
        {
            var success = await 
        }

        [Fact]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task StartCrawlerAsyncTest()
        {

        }

        [Fact]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task GetDatabaseAsyncTest()
        {

        }

        [Fact]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task GetTablesAsyncTest()
        {

        }
    }
}