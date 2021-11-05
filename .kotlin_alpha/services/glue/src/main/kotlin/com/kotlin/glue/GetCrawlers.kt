//snippet-sourcedescription:[GetCrawlers.kt demonstrates how to get AWS Glue crawlers.]
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

//snippet-start:[glue.kotlin.get_crawlers.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.GetCrawlersRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.get_crawlers.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val glueClient= GlueClient{region ="us-east-1"}
    getAllCrawlers(glueClient)
    glueClient.close()
}

//snippet-start:[glue.kotlin.get_crawlers.main]
suspend fun getAllCrawlers(glueClient: GlueClient) {
    try {

        val crawlersRequest = GetCrawlersRequest {
            maxResults = 10
        }

        val response = glueClient.getCrawlers(crawlersRequest)
        response.crawlers?.forEach { crawler ->
            println("The crawler name is ${crawler.name}")
        }

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.get_crawlers.main]