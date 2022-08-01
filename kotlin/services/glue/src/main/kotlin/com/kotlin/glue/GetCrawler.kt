// snippet-sourcedescription:[GetCrawler.kt demonstrates how to get an AWS Glue crawler.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Glue]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

// snippet-start:[glue.kotlin.get_crawler.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetCrawlerRequest
import kotlin.system.exitProcess
// snippet-end:[glue.kotlin.get_crawler.import]

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
        crawlerName - The name of the crawler. 
    """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val crawlerName = args[0]
    getSpecificCrawler(crawlerName)
}

// snippet-start:[glue.kotlin.get_crawler.main]
suspend fun getSpecificCrawler(crawlerName: String?) {

    val request = GetCrawlerRequest {
        name = crawlerName
    }
    GlueClient { region = "us-east-1" }.use { glueClient ->
        val response = glueClient.getCrawler(request)
        val role = response.crawler?.role
        println("The role associated with this crawler is $role")
    }
}
// snippet-end:[glue.kotlin.get_crawler.main]
