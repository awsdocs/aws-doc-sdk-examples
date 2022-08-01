// Copyright Amazon.com, Inc.or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache - 2.0

// snippet-start:[Glue.dotnetv3.GlueBasicsScenario.Main]
// This example uses .NET Core 6 and the AWS SDK for .NET (v3.7)
// Before running the code, set up your development environment,
// including your credentials. For more information, see the
// following topic:
//    https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-config.html
//
// To set up the resources you need, see the following topic:
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

// Initialize the values that we need for the scenario.
// The Amazon Resource Name (ARN) of the service role used by the crawler.
var iam = "arn:aws:iam::012345678901:role/AWSGlueServiceRole-CrawlerTutorial";

// The path to the Amazon S3 bucket where the comma-delimited file is stored.
var s3Path = "s3://crawler-public-us-east-1/flight/2016/csv";

var cron = "cron(15 12 * * ? *)";

// The name of the database used by the crawler.
var dbName = "example-flights-db";

var crawlerName = "Flight Data Crawler";
var jobName = "glue-job34";
var scriptLocation = "s3://aws-glue-scripts-012345678901-us-west-1/GlueDemoUser";
var locationUri = "s3://crawler-public-us-east-1/flight/2016/csv/";

var glueClient = new AmazonGlueClient();
await GlueMethods.DeleteDatabaseAsync(glueClient, dbName);

Console.WriteLine("Creating the database and crawler for the AWS Glue example.");
var success = await GlueMethods.CreateDatabaseAsync(glueClient, dbName, locationUri);
success = await GlueMethods.CreateGlueCrawlerAsync(glueClient, iam, s3Path, cron, dbName, crawlerName);

// Get information about the AWS Glue crawler.
Console.WriteLine("Showing information about the newly created AWS Glue crawler.");
success = await GlueMethods.GetSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Starting the new AWS Glue crawler.");
success = await GlueMethods.StartSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Displaying information about the database used by the crawler.");
success = await GlueMethods.GetSpecificDatabaseAsync(glueClient, dbName);
success = await GlueMethods.GetGlueTablesAsync(glueClient, dbName);

Console.WriteLine("Creating a new AWS Glue job.");
success = await GlueMethods.CreateJobAsync(glueClient, jobName, iam, scriptLocation);

Console.WriteLine("Starting the new AWS Glue job.");
success = await GlueMethods.StartJobAsync(glueClient, jobName);

Console.WriteLine("Getting information about the AWS Glue job.");
success = await GlueMethods.GetAllJobsAsync(glueClient);
success = await GlueMethods.GetJobRunsAsync(glueClient, jobName);

Console.WriteLine("Deleting the AWS Glue job used by the exmple.");
success = await GlueMethods.DeleteJobAsync(glueClient, jobName);

Console.WriteLine("\n*** Waiting 5 MIN for the " + crawlerName + " to stop. ***");
System.Threading.Thread.Sleep(300000);

Console.WriteLine("Clean up the resources created for the example.");
success = await GlueMethods.DeleteDatabaseAsync(glueClient, dbName);
success = await GlueMethods.DeleteSpecificCrawlerAsync(glueClient, crawlerName);

Console.WriteLine("Successfully completed the AWS Glue Scenario ");

// snippet-end:[Glue.dotnetv3.GlueBasicsScenario.Main]
