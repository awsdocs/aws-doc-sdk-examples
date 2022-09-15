// snippet-sourcedescription:[DeleteCrawler.kt demonstrates how to delete an AWS Glue crawler.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.delete_crawler.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.DeleteCrawlerRequest
import kotlin.system.exitProcess
// snippet-end:[glue.kotlin.delete_crawler.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <crawlerName>

    Where:
         crawlerName - The name of the crawler to delete. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val crawlerName = args[0]
    deleteSpecificCrawler(crawlerName)
}

// snippet-start:[glue.kotlin.delete_crawler.main]
suspend fun deleteSpecificCrawler(crawlerName: String) {

    val request = DeleteCrawlerRequest {
        name = crawlerName
    }
    GlueClient { region = "us-east-1" }.use { glueClient ->
        glueClient.deleteCrawler(request)
        println("$crawlerName was deleted")
    }
}
// snippet-end:[glue.kotlin.delete_crawler.main]
