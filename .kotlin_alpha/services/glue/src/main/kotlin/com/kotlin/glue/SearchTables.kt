//snippet-sourcedescription:[SearchTables.kt demonstrates how to search a set of tables based on properties.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Glue]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[6/4/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.glue

//snippet-start:[glue.kotlin.search_table.import]
import aws.sdk.kotlin.services.glue.GlueClient
import aws.sdk.kotlin.services.glue.model.SearchTablesRequest
import aws.sdk.kotlin.services.glue.model.GlueException
import aws.sdk.kotlin.services.glue.model.ResourceShareType
import kotlin.system.exitProcess
//snippet-end:[glue.kotlin.search_table.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <text>
    Where:
        text - a string used for a text search. 
    """

    if (args.size != 1) {
         println(usage)
         exitProcess(0)
    }

    val text = args[0]
    val glueClient= GlueClient{region ="us-east-1"}
    searchGlueTable(glueClient, text)
    glueClient.close()
}

//snippet-start:[glue.kotlin.search_table.main]
suspend fun searchGlueTable(glueClient: GlueClient, text: String?) {
    try {
        val tablesRequest = SearchTablesRequest {
            searchText = text
            resourceShareType = ResourceShareType.fromValue("All")
            maxResults = 10
        }

        val tablesResponse = glueClient.searchTables(tablesRequest)
        val tables = tablesResponse.tableList

        if (tables != null) {
            for (table in tables) {
                println("Table name is ${table.name}")
                println("Database name is ${table.databaseName}")
            }
        }

    } catch (e: GlueException) {
        println(e.message)
        glueClient.close()
        exitProcess(0)
    }
}
//snippet-end:[glue.kotlin.search_table.main]