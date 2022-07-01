// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Glue_Basics
{
    using Amazon.Glue;
    using Amazon.Glue.Model;

    public static class GlueMethods
    {
        public static async Task DeleteSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var deleteCrawlerRequest = new DeleteCrawlerRequest
            {
                Name = crawlerName,
            };

            await glueClient.DeleteCrawlerAsync(deleteCrawlerRequest);

            Console.WriteLine($"{crawlerName} was deleted");
        }

        public static async Task DeleteDatabaseAsync(AmazonGlueClient glueClient, string databaseName)
        {
            var request = new DeleteDatabaseRequest
            {
                Name = databaseName,
            };

            await glueClient.DeleteDatabaseAsync(request);

            Console.WriteLine($"{databaseName} was successfully deleted");
        }

        public static async Task DeleteJobAsync(AmazonGlueClient glueClient, string jobName)
        {
            var jobRequest = new DeleteJobRequest
            {
                JobName = jobName,
            };

            await glueClient.DeleteJobAsync(jobRequest);

            Console.WriteLine($"{jobName} was successfully deleted");
        }

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

        public static async Task GetSpecificDatabaseAsync(AmazonGlueClient glueClient, string databaseName)
        {
            GetDatabaseRequest databasesRequest = new GetDatabaseRequest
            {
                Name = databaseName,
            };

            var response = await glueClient.GetDatabaseAsync(databasesRequest);

            Console.WriteLine($"The Create Time is {response.Database.CreateTime}");
        }

        public static async Task StartSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var crawlerRequest = new StartCrawlerRequest
            {
                Name = crawlerName,
            };

            await glueClient.StartCrawlerAsync(crawlerRequest);
            Console.WriteLine($"{crawlerName} was successfully started!");
        }

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

        public static async Task GetGlueTablesAsync(AmazonGlueClient glueClient, string dbName)
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
