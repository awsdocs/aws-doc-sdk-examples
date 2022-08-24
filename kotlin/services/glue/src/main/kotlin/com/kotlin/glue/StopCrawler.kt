// snippet-sourcedescription:[StopCrawler.kt demonstrates how to stop an AWS Glue crawler.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.stop_crawler.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.StopCrawlerRequest
import kotlin.system.exitProcess
// snippet-end:[glue.kotlin.stop_crawler.import]

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
         crawlerName - the name of the crawler to delete. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val crawlerName = args[0]
    stopSpecificCrawler(crawlerName)
}

// snippet-start:[glue.kotlin.stop_crawler.main]
suspend fun stopSpecificCrawler(crawlerName: String) {

    val request = StopCrawlerRequest {
        name = crawlerName
    }

    GlueClient { region = "us-west-2" }.use { glueClient ->
        glueClient.stopCrawler(request)
        println("$crawlerName was stopped")
    }
}
// snippet-end:[glue.kotlin.stop_crawler.main]
