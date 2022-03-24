//snippet-sourcedescription:[GlueScenario.kt demonstrates how to perform multiple AWS Glue operations.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/17/2022]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.scenario.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.DatabaseInput
import aws.sdk.kotlin.services.glue.model.CreateDatabaseRequest
import aws.sdk.kotlin.services.glue.model.StartJobRunRequest
import aws.sdk.kotlin.services.glue.model.CreateJobRequest
import aws.sdk.kotlin.services.glue.model.JobCommand
import aws.sdk.kotlin.services.glue.model.CreateCrawlerRequest
import aws.sdk.kotlin.services.glue.model.WorkerType
import aws.sdk.kotlin.services.glue.model.GetDatabaseRequest
import aws.sdk.kotlin.services.glue.model.GetTablesRequest
import aws.sdk.kotlin.services.glue.model.S3Target
import aws.sdk.kotlin.services.glue.model.GetCrawlerRequest
import aws.sdk.kotlin.services.glue.model.GetJobsRequest
import aws.sdk.kotlin.services.glue.model.GetJobRunsRequest
import aws.sdk.kotlin.services.glue.model.DeleteJobRequest
import aws.sdk.kotlin.services.glue.model.StartCrawlerRequest
import aws.sdk.kotlin.services.glue.model.DeleteDatabaseRequest
import aws.sdk.kotlin.services.glue.model.DeleteCrawlerRequest
import aws.sdk.kotlin.services.glue.model.CrawlerTargets
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.scenario.import]

/**
 Before running this Kotlin code example, set up your development environment,
 including your credentials.

 For more information, see the following documentation topic:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

 To set up the resources, see this documentation topic:

  https://docs.aws.amazon.com/glue/latest/ug/tutorial-add-crawler.html
*/

//snippet-start:[glue.kotlin.scenario.main]
suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <iam> <s3Path> <cron> <dbName> <crawlerName> <jobName> <scriptLocation> <locationUri>

        Where:
            iam - The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role that has AWS Glue and Amazon Simple Storage Service (Amazon S3) permissions.
            s3Path - The Amazon Simple Storage Service (Amazon S3) target that contains data (for example, CSV data).
            cron - A cron expression used to specify the schedule (for example, cron(15 12 * * ? *).
            dbName - The database name. 
            crawlerName - The name of the crawler. 
            jobName - The name you assign to this job definition.
            scriptLocation - Specifies the Amazon S3 path to a script that runs a job.
            locationUri - Specifies the location of the database. 
        """

    if (args.size != 8) {
        println(usage)
        exitProcess(1)
     }

    val iam = args[0]
    val s3Path = args[1]
    val cron = args[2]
    val dbName = args[3]
    val crawlerName = args[4]
    val jobName = args[5]
    val scriptLocation = args[6]
    val locationUri = args[7]

    println("About to start the AWS Glue Scenario")
    createDatabase(dbName, locationUri)
    createCrawler(iam, s3Path, cron, dbName, crawlerName)
    getCrawler(crawlerName)
    startCrawler(crawlerName)
    getDatabase(dbName)
    getGlueTables( dbName)
    createJob(jobName, iam, scriptLocation)
    startJob(jobName)
    getJobs()
    getJobRuns(jobName)
    deleteJob(jobName)
    println("*** Wait for 5 MIN so the $crawlerName is ready to be deleted")
    TimeUnit.MINUTES.sleep(5)
    deleteMyDatabase(dbName)
    deleteCrawler(crawlerName)
}

suspend fun createDatabase( dbName: String?, locationUriVal: String?) {

        val input = DatabaseInput {
            description = "Built with the AWS SDK for Kotlin"
            name = dbName
            locationUri = locationUriVal
        }

        val request = CreateDatabaseRequest {
            databaseInput = input
        }

        GlueClient { region = "us-east-1" }.use { glueClient ->
            glueClient.createDatabase(request)
            println("The database was successfully created")
        }
 }

suspend fun createCrawler(iam: String?, s3Path: String?, cron: String?, dbName: String?, crawlerName: String) {

        val s3Target = S3Target {
            path=  s3Path
        }

        val targetList = ArrayList<S3Target>()
        targetList.add(s3Target)

        val targetOb = CrawlerTargets {
            s3Targets = targetList
        }

        val crawlerRequest = CreateCrawlerRequest {
            databaseName= dbName
            name = crawlerName
            description = "Created by the AWS Glue Java API"
            targets = targetOb
            role = iam
            schedule = cron
        }

        GlueClient { region = "us-east-1" }.use { glueClient ->
          glueClient.createCrawler(crawlerRequest)
          println("$crawlerName was successfully created")
        }
}

suspend fun getCrawler(crawlerName: String?) {

    val request = GetCrawlerRequest {
        name = crawlerName
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getCrawler(request)
        val role = response.crawler?.role
        println("The role associated with this crawler is $role")
    }
}

suspend fun startCrawler(crawlerName: String) {

        val crawlerRequest = StartCrawlerRequest {
            name = crawlerName
        }

        GlueClient { region = "us-east-1" }.use { glueClient ->
            glueClient.startCrawler(crawlerRequest)
            println("$crawlerName was successfully started.")
        }
 }

suspend fun getDatabase(databaseName: String?) {

    val request = GetDatabaseRequest {
        name = databaseName
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getDatabase(request)
        val dbDesc = response.database?.description
        println("The database description is $dbDesc")
    }
}

suspend fun getGlueTables( dbName: String?) {

    val tableRequest = GetTablesRequest {
        databaseName = dbName
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getTables(tableRequest)
        response.tableList?.forEach { tableName ->
            println("Table name is ${tableName.name}")
        }
    }
}

suspend fun startJob(jobNameVal: String?) {

        val runRequest = StartJobRunRequest {
            workerType = WorkerType.G1X
            numberOfWorkers = 10
            jobName = jobNameVal
        }

        GlueClient { region = "us-east-1" }.use { glueClient ->
          val response = glueClient.startJobRun(runRequest)
          println("The job run Id is ${response.jobRunId}")
      }
 }

suspend fun createJob(jobName: String, iam: String?, scriptLocationVal: String?) {

    val commandOb = JobCommand {
        pythonVersion = "3"
        name = "MyJob1"
        scriptLocation = scriptLocationVal
    }

    val jobRequest = CreateJobRequest {
        description = "A Job created by using the AWS SDK for Java V2"
        glueVersion = "2.0"
        workerType = WorkerType.G1X
        numberOfWorkers = 10
        name = jobName
        role = iam
        command = commandOb
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        glueClient.createJob(jobRequest)
        println("$jobName was successfully created.")
    }
}

suspend fun getJobs() {

    val request = GetJobsRequest {
        maxResults = 10
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getJobs(request)
        response.jobs?.forEach { job ->
            println("Job name is ${job.name}")
        }
    }
}

suspend fun getJobRuns(jobNameVal: String?) {

    val request = GetJobRunsRequest {
        jobName = jobNameVal
   }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getJobRuns(request)
        response.jobRuns?.forEach { job ->
            println("Job name is ${job.jobName}")
        }
    }
}

suspend fun deleteJob(jobNameVal: String) {

    val jobRequest = DeleteJobRequest {
        jobName = jobNameVal
    }

    GlueClient { region = "us-east-1" }.use { glueClient ->
        glueClient.deleteJob(jobRequest)
        println("$jobNameVal was successfully deleted")
   }
}

suspend fun deleteMyDatabase(databaseName: String) {

        val request = DeleteDatabaseRequest {
            name = databaseName
        }

        GlueClient { region = "us-east-1" }.use { glueClient ->
          glueClient.deleteDatabase(request)
          println("$databaseName was successfully deleted")
        }
 }

suspend fun deleteCrawler(crawlerName: String) {

    val request = DeleteCrawlerRequest {
        name = crawlerName
    }
    GlueClient { region = "us-east-1" }.use { glueClient ->
        glueClient.deleteCrawler(request)
        println("$crawlerName was deleted")
    }
}
//snippet-end:[glue.kotlin.scenario.main]
