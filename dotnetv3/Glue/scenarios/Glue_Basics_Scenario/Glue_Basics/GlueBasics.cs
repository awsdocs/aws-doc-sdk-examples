// Copyright Amazon.com, Inc.or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache - 2.0

// This example uses .NET Core 6 and the AWS SDK for .NET v3.7
// Before running the code, et up your development environment,
// including your credentials. For more information, see the
// following topic:
//    https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
//
// To set up the resources you need, see this topic:
//    https://docs.aws.amazon.com/glue/latest/ug/tutorial-add-crawler.html
//
// This example performs the following tasks:
//    1. CreateDatabase
//    2. CreateCrawler
//    3. GetCrawler
//    4. StartCrawler
//    5. GetDatabase
//    6. GetTables
//    7. CreateJob
//    8. StartJobRun
//    9. ListJobs
//   10. GetJobRuns
//   11. DeleteJob
//   12. DeleteDatabase
//   13. DeleteCrawler
using Amazon.Glue;
using Glue_Basics;

// Initialize the values we need for the scenario.
// The ARN of the service role used by the crawler.
// var iam = "arn:aws:iam::012345678901:role/AWSGlueServiceRole-CrawlerTutorial";
var iam = "arn:aws:iam::704825161248:role/service-role/AWSGlueServiceRole-CrawlerTutorial";

// The path to the Amazon S3 bucket where the comma-delimited file is stored.
var s3Path = "s3://crawler-public-us-east-1/flight/2016/csv";

var cron = "cron(15 12 * * ? *)";

// The name of the database used by the crawler.
var dbName = "test-flights-db";

var crawlerName = "Flight Data Crawler";
var jobName = "glue-job34";
var scriptLocation = "s3://aws-glue-scripts-704825161248-us-west-1/GlueDemoUser";
var locationUri = "s3://crawler-public-us-east-1/flight/2016/csv/";

var glueClient = new AmazonGlueClient();
await GlueMethods.DeleteDatabaseAsync(glueClient, dbName);

Console.WriteLine("Creating the database and crawler for the AWS Glue example.");
await GlueMethods.CreateDatabaseAsync(glueClient, dbName, locationUri);
await GlueMethods.CreateGlueCrawlerAsync(glueClient, iam, s3Path, cron, dbName, crawlerName);

// Get information about the AWS Glue crawler.
Console.WriteLine("Showing information about the newly created AWS Glue crawler.");
await GlueMethods.GetSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Starting the new AWS Glue crawler.");
await GlueMethods.StartSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Displaying information about the database used by the crawler.");
await GlueMethods.GetSpecificDatabaseAsync(glueClient, dbName);
await GlueMethods.GetGlueTablesAsync(glueClient, dbName);

Console.WriteLine("Creating a new AWS Glue job.");
await GlueMethods.CreateJobAsync(glueClient, jobName, iam, scriptLocation);

Console.WriteLine("Starting the new AWS Glue job.");
await GlueMethods.StartJobAsync(glueClient, jobName);

Console.WriteLine("Getting information about the AWS Glue job.");
await GlueMethods.GetAllJobsAsync(glueClient);
await GlueMethods.GetJobRunsAsync(glueClient, jobName);

Console.WriteLine("Deleting the AWS Glue job used by the exmple.");
await GlueMethods.DeleteJobAsync(glueClient, jobName);

Console.WriteLine("\n*** Waiting 5 MIN for the " + crawlerName + " to stop");
System.Threading.Thread.Sleep(300000);

Console.WriteLine("Clean up the resources created for the example.");
await GlueMethods.DeleteDatabaseAsync(glueClient, dbName);
await GlueMethods.DeleteSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Successfully completed the AWS Glue Scenario ");