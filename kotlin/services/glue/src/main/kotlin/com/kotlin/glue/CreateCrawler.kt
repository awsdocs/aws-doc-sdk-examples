//snippet-sourcedescription:[CreateCrawler.kt demonstrates how to create an AWS Glue crawler.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.create_crawler.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.CrawlerTargets
import aws.sdk.kotlin.services.glue.model.CreateCrawlerRequest
import aws.sdk.kotlin.services.glue.model.S3Target
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.create_crawler.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <IAM> <s3Path> <cron> <dbName> <crawlerName>

    Where:
        IAM - the ARN of the IAM role that has AWS Glue and S3 permissions. 
        s3Path - the Amazon Simple Storage Service (Amazon S3) target that contains data (for example, CSV data).
        cron - a cron expression used to specify the schedule  (i.e., cron(15 12 * * ? *).
        dbName - the database name. 
        crawlerName - the name of the crawler. 
    """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val iam = args[0]
    val s3Path = args[1]
    val cron = args[2]
    val dbName = args[3]
    val crawlerName = args[4]
    createGlueCrawler(iam, s3Path, cron,dbName, crawlerName)
 }

//snippet-start:[glue.kotlin.create_crawler.main]
suspend fun createGlueCrawler(
    iam: String?,
    s3Path: String?,
    cron: String?,
    dbName: String?,
    crawlerName: String
) {
       val s3Target = S3Target {
            path = s3Path
        }

        // Add the S3Target to a list.
        val targetList = mutableListOf<S3Target>()
        targetList.add(s3Target)

        val targetOb = CrawlerTargets {
            s3Targets = targetList
        }

       val request = CreateCrawlerRequest {
           databaseName = dbName
           name = crawlerName
           description = "Created by the AWS Glue Kotlin API"
           targets = targetOb
           role = iam
           schedule = cron
       }

       GlueClient { region = "us-west-2" }.use { glueClient ->
        glueClient.createCrawler(request)
        println("$crawlerName was successfully created")
       }
}
//snippet-end:[glue.kotlin.create_crawler.main]