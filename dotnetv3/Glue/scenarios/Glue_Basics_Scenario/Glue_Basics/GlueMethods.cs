// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Glue_Basics
{
    using Amazon.Glue;
    using Amazon.Glue.Model;

    /// <summary>
    /// Methods for working the AWS Glue using the AWS SDK for .NET v3.7.
    /// </summary>
    public static class GlueMethods
    {
        /// <summary>
        /// Deletes the named AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawler to delete.</param>
        public static async Task DeleteSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var deleteCrawlerRequest = new DeleteCrawlerRequest
            {
                Name = crawlerName,
            };

            await glueClient.DeleteCrawlerAsync(deleteCrawlerRequest);

            Console.WriteLine($"{crawlerName} was deleted");
        }

        /// <summary>
        /// Deletes an AWS Glue database.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue cllient.</param>
        /// <param name="databaseName">The name of the database to delte.</param>
        public static async Task DeleteDatabaseAsync(AmazonGlueClient glueClient, string databaseName)
        {
            var request = new DeleteDatabaseRequest
            {
                Name = databaseName,
            };

            await glueClient.DeleteDatabaseAsync(request);

            Console.WriteLine($"{databaseName} was successfully deleted");
        }

        /// <summary>
        /// Deletes the named job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The name of the job to delete.</param>
        public static async Task DeleteJobAsync(AmazonGlueClient glueClient, string jobName)
        {
            var jobRequest = new DeleteJobRequest
            {
                JobName = jobName,
            };

            await glueClient.DeleteJobAsync(jobRequest);

            Console.WriteLine($"{jobName} was successfully deleted");
        }

        /// <summary>
        /// Retrieves information about an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The AWS Glue object for which to retrieve run
        /// information.</param>
        public static async Task GetJobRunsAsync(AmazonGlueClient glueClient, string jobName)
        {
            var runsRequest = new GetJobRunsRequest
            {
                JobName = jobName,
                MaxResults = 20,
            };

            var response = await glueClient.GetJobRunsAsync(runsRequest);
            var jobRuns = response.JobRuns;

            foreach (JobRun jobRun in jobRuns)
            {
                Console.WriteLine($"Job run state is {jobRun.JobRunState}");
                Console.WriteLine($"Job run Id is {jobRun.Id}");
                Console.WriteLine($"The Glue version is {jobRun.GlueVersion}");
            }
        }

        /// <summary>
        /// Gets a list of AWS Glue jobs.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        public static async Task GetAllJobsAsync(AmazonGlueClient glueClient)
        {
            var jobsRequest = new GetJobsRequest
            {
                MaxResults = 10,
            };

            var jobsResponse = await glueClient.GetJobsAsync(jobsRequest);
            var jobs = jobsResponse.Jobs;

            jobs.ForEach(job => { Console.WriteLine($"The job name is: {job.Name}"); });
        }

        /// <summary>
        /// Starts an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized Glue client.</param>
        /// <param name="jobName">The name of the AWS Glue job to start.</param>
        public static async Task StartJobAsync(AmazonGlueClient glueClient, string jobName)
        {
            var runRequest = new StartJobRunRequest
            {
                WorkerType = WorkerType.G1X,
                NumberOfWorkers = 10,
                JobName = jobName,
            };

            var response = await glueClient.StartJobRunAsync(runRequest);

            Console.WriteLine("The job run id is " + response.JobRunId);
        }

        /// <summary>
        /// Creates an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The name of the job to create.</param>
        /// <param name="iam">The Amazon Resource Name (ARN) of the IAM role
        /// that will be used by the job.</param>
        /// <param name="scriptLocation">The location where the script is stored.</param>
        public static async Task CreateJobAsync(AmazonGlueClient glueClient, string jobName, string iam, string scriptLocation)
        {
            var command = new JobCommand
            {
                PythonVersion = "3",
                Name = "MyJob1",
                ScriptLocation = scriptLocation,
            };

            var jobRequest = new CreateJobRequest
            {
                Description = "A Job created by using the AWS SDK for .NET",
                GlueVersion = "2.0",
                WorkerType = WorkerType.G1X,
                NumberOfWorkers = 10,
                Name = jobName,
                Role = iam,
                Command = command,
            };

            await glueClient.CreateJobAsync(jobRequest);
            Console.WriteLine($"{jobName} was successfully created.");
        }

        /// <summary>
        /// Gets information about the database created for this Glue
        /// example.
        /// </summary>
        /// <param name="glueClient">The initialized Glue client.</param>
        /// <param name="databaseName"></param>
        public static async Task GetSpecificDatabaseAsync(
            AmazonGlueClient glueClient,
            string databaseName)
        {
            GetDatabaseRequest databasesRequest = new GetDatabaseRequest
            {
                Name = databaseName,
            };

            var response = await glueClient.GetDatabaseAsync(databasesRequest);

            Console.WriteLine($"The Create Time is {response.Database.CreateTime}");
        }

        /// <summary>
        /// Starts the named AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawler to start.</param>
        public static async Task StartSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var crawlerRequest = new StartCrawlerRequest
            {
                Name = crawlerName,
            };

            await glueClient.StartCrawlerAsync(crawlerRequest);
            Console.WriteLine($"{crawlerName} was successfully started!");
        }
        /// <summary>
        /// Retrieves information about a specific AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawer.</param>
        public static async Task GetSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            GetCrawlerRequest crawlerRequest = new GetCrawlerRequest
            {
                Name = crawlerName,
            };

            var response = await glueClient.GetCrawlerAsync(crawlerRequest);
            var databaseName = response.Crawler.DatabaseName;
            Console.WriteLine($"{crawlerName} has the database {databaseName}");
        }

        /// <summary>
        /// Creates an AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="iam">The Amazon Resource Name (ARN) of the IAM role
        /// used by the crawler.</param>
        /// <param name="s3Path">The path to the Amazon S3 bucket where
        /// data is stored.</param>
        /// <param name="cron">The name of the CRON job that runs the crawler.</param>
        /// <param name="dbName">The name of the database.</param>
        /// <param name="crawlerName">The name of the AWS Glue crawler.</param>
        public static async Task CreateGlueCrawlerAsync(
            AmazonGlueClient glueClient,
            string iam,
            string s3Path,
            string cron,
            string dbName,
            string crawlerName)
        {
            var s3Target = new S3Target
            {
                Path = s3Path,
            };

            List<S3Target> targetList = new List<S3Target>();
            targetList.Add(s3Target);

            CrawlerTargets targets = new CrawlerTargets
            {
                S3Targets = targetList,
            };

            CreateCrawlerRequest crawlerRequest = new CreateCrawlerRequest
            {
                DatabaseName = dbName,
                Name = crawlerName,
                Description = "Created by the AWS Glue .NET API",
                Targets = targets,
                Role = iam,
                Schedule = cron,
            };

            await glueClient.CreateCrawlerAsync(crawlerRequest);
            Console.WriteLine($"{crawlerName} was successfully created");
        }

        /// <summary>
        /// Creates a database for use by an AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="dbName">The name of the new database.</param>
        /// <param name="locationUri">The location of scripts that will be
        /// used by the AWS Glue crawler.</param>
        public static async Task CreateDatabaseAsync(AmazonGlueClient glueClient, string dbName, string locationUri)
        {
            try
            {
                var dataBaseInput = new DatabaseInput
                {
                    Description = "Built with the AWS SDK for .NET v3",
                    Name = dbName,
                    LocationUri = locationUri,
                };

                var request = new CreateDatabaseRequest
                {
                    DatabaseInput = dataBaseInput,
                };

                var response = await glueClient.CreateDatabaseAsync(request);
                Console.WriteLine("The database was successfully created");
            }
            catch (AmazonGlueException ex)
            {
                Console.WriteLine($"Error occurred: '{ex.Message}'");
            }
        }

        /// <summary>
        /// Gets the tables used by the database for an AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="dbName">The name of the database.</param>
        public static async Task GetGlueTablesAsync(
            AmazonGlueClient glueClient,
            string dbName)
        {
            var tableRequest = new GetTablesRequest
            {
                DatabaseName = dbName,
            };

            // Get the list of Glue databases.
            var response = await glueClient.GetTablesAsync(tableRequest);
            var tables = response.TableList;

            // Display the list of table names.
            tables.ForEach(table => { Console.WriteLine($"Table name is: {table.Name}"); });
        }
    }
}
