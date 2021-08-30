//snippet-sourcedescription:[CreateNamedQueryExample.kt demonstrates how to create a named query.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/14/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.athena

//snippet-start:[athena.kotlin.CreateNamedQueryExample.import]
import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.athena.AthenaClient
import aws.sdk.kotlin.services.athena.model.CreateNamedQueryRequest
import kotlin.system.exitProcess
//snippet-end:[athena.kotlin.CreateNamedQueryExample.import]

suspend fun main(args:Array<String>) {

    val usage = """
    Usage:
        <queryString> <namedQuery> <database>

    Where:
        queryString - the query string to use (for example, "SELECT * FROM mydatabase"; ).
        namedQuery - the name of the query to create. 
        database - the name of the database to use (for example, mydatabase ).
        
    """

    if (args.size != 3) {
        println(usage)
        exitProcess(0)
    }

    val queryString = args[0]
    val namedQuery = args[1]
    val database = args[2]
    val athenaClient = AthenaClient { region = "us-west-2" }
    val id = createNamedQuery(athenaClient,queryString, namedQuery,database)
    println("The query ID is $id")
    athenaClient.close()
}

    //snippet-start:[athena.kotlin.CreateNamedQueryExample.main]
    suspend  fun createNamedQuery(athenaClient: AthenaClient, queryStringVal:String, namedQuery:String, databaseVal:String ):String? {

        try {
            // Create the named query request.
            val createNamedQueryRequest =  CreateNamedQueryRequest {
                 database = databaseVal
                 queryString = queryStringVal
                 description = "Created via the AWS SDK for Kotlin"
                 this.name = namedQuery
            }

            val resp =  athenaClient.createNamedQuery(createNamedQueryRequest)
            return resp.namedQueryId

        } catch (ex: AwsServiceException) {
            println(ex.message)
            athenaClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[athena.kotlin.CreateNamedQueryExample.main]
