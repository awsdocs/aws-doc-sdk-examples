// Copyright Amazon.com, Inc.or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache - 2.0

// Before running this .NET v3 code example, set up your development environment, including your credentials.
// For more information, see the following topic:
//    https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
//
// To set up the resources, see this topic:
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
var iam = "arn:aws:iam::814548047983:role/AWSGlueServiceRoleDefault"; // args[0];
var s3Path = "s3://glue-demo-scott/read"; // args[1];
var cron = "cron(15 12 * * ? *)"; // args[2];
var dbName = "glue-db305";  // args[3];
var crawlerName = "crawl2019"; // args[4];
var jobName = "glue-job34"; // args[5];
var scriptLocation = "s3://aws-glue-scripts-814548047983-us-east-1/PowerUserScott"; // args[6];
var locationUri = "s3://crawler-public-us-east-1/flight/2016/csv/"; // args[7];
var glueClient = new AmazonGlueClient();

await GlueMethods.CreateDatabaseAsync(glueClient, dbName, locationUri);
await GlueMethods.CreateGlueCrawlerAsync(glueClient, iam, s3Path, cron, dbName, crawlerName);
await GlueMethods.GetSpecificCrawlerAsync(glueClient, crawlerName);
await GlueMethods.StartSpecificCrawlerAsync(glueClient, crawlerName);
await GlueMethods.GetSpecificDatabaseAsync(glueClient, dbName);
await GlueMethods.GetGlueTablesAsync(glueClient, dbName);
await GlueMethods.CreateJobAsync(glueClient, jobName, iam, scriptLocation);
await GlueMethods.StartJobAsync(glueClient, jobName);
await GlueMethods.GetAllJobsAsync(glueClient);
await GlueMethods.GetJobRunsAsync(glueClient, jobName);
await GlueMethods.DeleteJobAsync(glueClient, jobName);

Console.WriteLine("*** Wait 5 MIN for the " + crawlerName + " to stop");
System.Threading.Thread.Sleep(300000);

await GlueMethods.DeleteDatabaseAsync(glueClient, dbName);
await GlueMethods.DeleteSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Successfully completed the AWS Glue Scenario ");