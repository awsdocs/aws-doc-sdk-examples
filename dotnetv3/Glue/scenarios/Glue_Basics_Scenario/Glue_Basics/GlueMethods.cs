// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace Glue_Basics
{
    /// <summary>
    /// Methods for working the AWS Glue using the AWS SDK for .NET v3.7.
    /// </summary>
    public static class GlueMethods
    {
        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.CreateDatabaseAsync]

        /// <summary>
        /// Creates a database for use by an AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="dbName">The name of the new database.</param>
        /// <param name="locationUri">The location of scripts that will be
        /// used by the AWS Glue crawler.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue database
        /// was created successfully.</returns>
        public static async Task<bool> CreateDatabaseAsync(AmazonGlueClient glueClient, string dbName, string locationUri)
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
                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    Console.WriteLine("The database was successfully created");
                    return true;
                }
                else
                {
                    Console.WriteLine("Could not create the database.");
                    return false;
                }
            }
            catch (AmazonGlueException ex)
            {
                Console.WriteLine($"Error occurred: '{ex.Message}'");
                return false;
            }
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.CreateDatabaseAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.CreateGlueCrawlerAsync]

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
        /// <returns>A Boolean value indicating whether the AWS Glue crawler was
        /// created successfully.</returns>
        public static async Task<bool> CreateGlueCrawlerAsync(
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

            var targetList = new List<S3Target>
            {
                s3Target,
            };

            var targets = new CrawlerTargets
            {
                S3Targets = targetList,
            };

            var crawlerRequest = new CreateCrawlerRequest
            {
                DatabaseName = dbName,
                Name = crawlerName,
                Description = "Created by the AWS Glue .NET API",
                Targets = targets,
                Role = iam,
                Schedule = cron,
            };

            var response = await glueClient.CreateCrawlerAsync(crawlerRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{crawlerName} was successfully created");
                return true;
            }
            else
            {
                Console.WriteLine($"Could not create {crawlerName}.");
                return false;
            }
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.CreateGlueCrawlerAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.CreateJobAsync]

        /// <summary>
        /// Creates an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The name of the job to create.</param>
        /// <param name="iam">The Amazon Resource Name (ARN) of the IAM role
        /// that will be used by the job.</param>
        /// <param name="scriptLocation">The location where the script is stored.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue job was
        /// created successfully.</returns>
        public static async Task<bool> CreateJobAsync(AmazonGlueClient glueClient, string jobName, string iam, string scriptLocation)
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

            var response = await glueClient.CreateJobAsync(jobRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{jobName} was successfully created.");
                return true;
            }

            Console.WriteLine($"{jobName} could not be created.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.CreateJobAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.DeleteSpecificCrawlerAsync]

        /// <summary>
        /// Deletes the named AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawler to delete.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue crawler was
        /// deleted successfully.</returns>
        public static async Task<bool> DeleteSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var deleteCrawlerRequest = new DeleteCrawlerRequest
            {
                Name = crawlerName,
            };

            var response = await glueClient.DeleteCrawlerAsync(deleteCrawlerRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{crawlerName} was deleted");
                return true;
            }

            Console.WriteLine($"Could not create {crawlerName}.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.DeleteSpecificCrawlerAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.DeleteDatabaseAsync]

        /// <summary>
        /// Deletes an AWS Glue database.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue cllient.</param>
        /// <param name="databaseName">The name of the database to delte.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue database was
        /// deleted successfully.</returns>
        public static async Task<bool> DeleteDatabaseAsync(AmazonGlueClient glueClient, string databaseName)
        {
            var request = new DeleteDatabaseRequest
            {
                Name = databaseName,
            };

            var response = await glueClient.DeleteDatabaseAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{databaseName} was successfully deleted");
                return true;
            }

            Console.WriteLine($"{databaseName} could not be deleted.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.DeleteDatabaseAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.DeleteJobAsync]

        /// <summary>
        /// Deletes the named job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The name of the job to delete.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue job was
        /// deleted successfully.</returns>
        public static async Task<bool> DeleteJobAsync(AmazonGlueClient glueClient, string jobName)
        {
            var jobRequest = new DeleteJobRequest
            {
                JobName = jobName,
            };

            var response = await glueClient.DeleteJobAsync(jobRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{jobName} was successfully deleted");
                return true;
            }

            Console.WriteLine($"{jobName} could not be deleted.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.DeleteJobAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.GetAllJobsAsync]

        /// <summary>
        /// Gets a list of AWS Glue jobs.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <returns>A Boolean value indicating whether information about the
        /// AWS Glue jobs was retrieved successfully.</returns>
        /// <returns>A Boolean value that indicates whether information about
        /// all AWS Glue jobs was retrieved.</returns>
        public static async Task<bool> GetAllJobsAsync(AmazonGlueClient glueClient)
        {
            var jobsRequest = new GetJobsRequest
            {
                MaxResults = 10,
            };

            var response = await glueClient.GetJobsAsync(jobsRequest);
            var jobs = response.Jobs;
            if (jobs.Count > 0)
            {
                jobs.ForEach(job => { Console.WriteLine($"The job name is: {job.Name}"); });
                return true;
            }
            else
            {
                Console.WriteLine("Didn't find any jobs.");
                return false;
            }
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.GetAllJobsAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.GetGlueTablesAsync]

        /// <summary>
        /// Gets the tables used by the database for an AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="dbName">The name of the database.</param>
        /// <returns>A Boolean value indicating whether information about
        /// the AWS Glue tables was retrieved successfully.</returns>
        public static async Task<bool> GetGlueTablesAsync(
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

            if (tables.Count > 0)
            {
                // Display the list of table names.
                tables.ForEach(table => { Console.WriteLine($"Table name is: {table.Name}"); });
                return true;
            }
            else
            {
                Console.WriteLine("No tables found.");
                return false;
            }
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.GetGlueTablesAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.GetJobRunsAsync]

        /// <summary>
        /// Retrieves information about an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="jobName">The AWS Glue object for which to retrieve run
        /// information.</param>
        /// <returns>A Boolean value indicating whether information about
        /// the AWS Glue job runs was retrieved successfully.</returns>
        public static async Task<bool> GetJobRunsAsync(AmazonGlueClient glueClient, string jobName)
        {
            var runsRequest = new GetJobRunsRequest
            {
                JobName = jobName,
                MaxResults = 20,
            };

            var response = await glueClient.GetJobRunsAsync(runsRequest);
            var jobRuns = response.JobRuns;

            if (jobRuns.Count > 0)
            {
                foreach (JobRun jobRun in jobRuns)
                {
                    Console.WriteLine($"Job run state is {jobRun.JobRunState}");
                    Console.WriteLine($"Job run Id is {jobRun.Id}");
                    Console.WriteLine($"The Glue version is {jobRun.GlueVersion}");
                }

                return true;
            }
            else
            {
                Console.WriteLine("No jobs found.");
                return false;
            }
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.GetJobRunsAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.GetSpecificCrawlerAsync]

        /// <summary>
        /// Retrieves information about a specific AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawer.</param>
        /// <returns>A Boolean value indicating whether information about
        /// the AWS Glue crawler was retrieved successfully.</returns>
        public static async Task<bool> GetSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var crawlerRequest = new GetCrawlerRequest
            {
                Name = crawlerName,
            };

            var response = await glueClient.GetCrawlerAsync(crawlerRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                var databaseName = response.Crawler.DatabaseName;
                Console.WriteLine($"{crawlerName} has the database {databaseName}");
                return true;
            }

            Console.WriteLine($"No information regarding {crawlerName} could be found.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.GetSpecificCrawlerAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.GetSpecificDatabaseAsync]

        /// <summary>
        /// Gets information about the database created for this Glue
        /// example.
        /// </summary>
        /// <param name="glueClient">The initialized Glue client.</param>
        /// <param name="databaseName">The  name of the AWS Glue database.</param>
        /// <returns>A Boolean value indicating whether information about
        /// the AWS Glue database was retrieved successfully.</returns>
        public static async Task<bool> GetSpecificDatabaseAsync(
            AmazonGlueClient glueClient,
            string databaseName)
        {
            var databasesRequest = new GetDatabaseRequest
            {
                Name = databaseName,
            };

            var response = await glueClient.GetDatabaseAsync(databasesRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"The Create Time is {response.Database.CreateTime}");
                return true;
            }

            Console.WriteLine($"No informaton about {databaseName}.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.GetSpecificDatabaseAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.StartJobAsync]

        /// <summary>
        /// Starts an AWS Glue job.
        /// </summary>
        /// <param name="glueClient">The initialized Glue client.</param>
        /// <param name="jobName">The name of the AWS Glue job to start.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue job
        /// was started successfully.</returns>
        public static async Task<bool> StartJobAsync(AmazonGlueClient glueClient, string jobName)
        {
            var runRequest = new StartJobRunRequest
            {
                WorkerType = WorkerType.G1X,
                NumberOfWorkers = 10,
                JobName = jobName,
            };

            var response = await glueClient.StartJobRunAsync(runRequest);
            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{jobName} successfully started. The job run id is {response.JobRunId}.");
                return true;
            }

            Console.WriteLine($"Could not start {jobName}.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.StartJobAsync]

        // snippet-start:[Glue.dotnetv3.GlueBasicsScenario.StartSpecificCrawlerAsync]

        /// <summary>
        /// Starts the named AWS Glue crawler.
        /// </summary>
        /// <param name="glueClient">The initialized AWS Glue client.</param>
        /// <param name="crawlerName">The name of the crawler to start.</param>
        /// <returns>A Boolean value indicating whether the AWS Glue crawler
        /// was started successfully.</returns>
        public static async Task<bool> StartSpecificCrawlerAsync(AmazonGlueClient glueClient, string crawlerName)
        {
            var crawlerRequest = new StartCrawlerRequest
            {
                Name = crawlerName,
            };

            var response = await glueClient.StartCrawlerAsync(crawlerRequest);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"{crawlerName} was successfully started!");
                return true;
            }

            Console.WriteLine($"Could not start AWS Glue crawler, {crawlerName}.");
            return false;
        }

        // snippet-end:[Glue.dotnetv3.GlueBasicsScenario.StartSpecificCrawlerAsync]
    }
}
