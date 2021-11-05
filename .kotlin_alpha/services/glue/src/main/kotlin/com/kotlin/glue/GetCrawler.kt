//snippet-sourcedescription:[GetCrawler.kt demonstrates how to get an AWS Glue crawler.]
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

//snippet-start:[glue.kotlin.get_crawler.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetCrawlerRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_crawler.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <crawlerName>

    Where:
        crawlerName - the name of the crawler. 
    """

   if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val crawlerName = args[0]
    val glueClient= GlueClient{region ="us-east-1"}
    getSpecificCrawler(glueClient, crawlerName)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_crawler.main]
suspend fun getSpecificCrawler(glueClient: GlueClient, crawlerName: String?) {
    try {

        val crawlerRequest = GetCrawlerRequest {
            name = crawlerName
        }

        val response = glueClient.getCrawler(crawlerRequest)
        val role = response.crawler?.role
        println("The role associated with this crawler is $role")

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_crawler.main]