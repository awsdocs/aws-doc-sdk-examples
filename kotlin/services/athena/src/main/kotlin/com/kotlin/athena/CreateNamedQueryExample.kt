// snippet-sourcedescription:[CreateNamedQueryExample.kt demonstrates how to create a named query.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Athena]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

// snippet-start:[athena.kotlin.CreateNamedQueryExample.import]
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.CreateNamedQueryRequest
import kotlin.system.exitProcess
// snippet-end:[athena.kotlin.CreateNamedQueryExample.import]

suspend fun main(args: Array<String>) {

    val usage = """
    Usage:
        <queryString> <namedQuery> <database>

    Where:
        queryString - The query string to use (for example, "SELECT * FROM mydatabase"; ).
        namedQuery - The name of the query to create. 
        database - The name of the database to use (for example, mydatabase).
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val queryString = args[0]
    val namedQuery = args[1]
    val database = args[2]
    val id = createNamedQuery(queryString, namedQuery, database)
    println("The query ID is $id")
}

// snippet-start:[athena.kotlin.CreateNamedQueryExample.main]
suspend fun createNamedQuery(queryStringVal: String, namedQuery: String, databaseVal: String): String? {

    AthenaClient { region = "us-west-2" }.use { athenaClient ->
        val resp = athenaClient.createNamedQuery(
            CreateNamedQueryRequest {
                database = databaseVal
                queryString = queryStringVal
                description = "Created via the AWS SDK for Kotlin"
                this.name = namedQuery
            }
        )
        return resp.namedQueryId
    }
}
// snippet-end:[athena.kotlin.CreateNamedQueryExample.main]
